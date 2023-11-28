package com.focusservices.library.versionamiento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.repository.jql.QueryBuilder;
import org.javers.shadow.Shadow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.focusservices.library.commons.ServiceResponse;
import com.focusservices.library.commons.datatables.mapping.DataTablesInput;
import com.focusservices.library.commons.datatables.mapping.DataTablesOutput;

@Service
@Slf4j
public class VersionamientoService {
    @Autowired
    private Javers javers;
    
    @Autowired
    private VersionamientoRepository versionamientoRepository;
    
    /** Obtiene todos los registros unicos versionados para una tabla dada.
     *  Si el registro fue eliminado, se obtiene el ultimo registro antes de su
     *  eliminacion.
     * 
     * @param nombreClase el nombre de la tabla a obtener sus registros.
     * @param input el paginado y filtrado solicitado por el cliente.
     * @return un listado en formato de DataTable de los registros antes mencionados
     */
    public DataTablesOutput<Map<String, Object>> getRegistrosVersionados(String nombreClase, DataTablesInput input) {
        MetadataTablaVersionada tabla = MetadataIntegrator.getMetadataTabla(nombreClase);
        return getRegistrosVersionadosByClase(tabla, tabla.getClase(), input);
    }

    private <T> DataTablesOutput<Map<String, Object>> getRegistrosVersionadosByClase(MetadataTablaVersionada tabla, Class<T> clase, DataTablesInput input) {
        // obtengo los ids de los registros
        QueryResult<Set<RegistroUnico>> ultimosIds = versionamientoRepository.getUltimosIdsRegistros(clase, input);
        IdTrabajo idTrabajo = new IdTrabajo(ultimosIds);
        
        // realizo la consulta de javers para obtener los registros
        List<Shadow<T>> snapshots = javers.findShadows(QueryBuilder.byClass(clase)
                .withCommitIds(idTrabajo.getIdsUnicas())
                .withChildValueObjects()
                .build()
        );
        
        ShadowProcesadorRegistrosVersionados<T> procesadorShadows = crearShadowLogRegistrosUnicos();
        AlmacenRegistrosProcesados almacen = procesadorShadows.procesar(snapshots, idTrabajo, tabla);
        
        List<RegistroVersionado> valoresConJoin = procesarJoins(tabla, almacen.getValoresEntidad(), almacen.getCacheJoins());
        RegistroOrden registroOrden = new RegistroOrden(input);
        List<Map<String, Object>> valoresFinales = valoresConJoin.stream()
                .sorted(registroOrden)
                .map(RegistroVersionado::toMap)
                .collect(Collectors.toList());
        
        // por ultimo, se crea el objeto final a enviar al cliente
        return VersionamientoUtils.crearDataTablesOutput(new QueryResult<>(valoresFinales, ultimosIds.getCount()), input);
    }

    private <T> ShadowProcesadorRegistrosVersionados<T> crearShadowLogRegistrosUnicos() {
        /** Agrego campos extras en la metadata del versionamiento
         *  - Se agrega el tipo de cambio realizado (si es el registro original,
         *    modificado o borrado).
         *
         *  - Se agrega el campo de la entidad identificado como id.
         */
        ConsumerVersionamiento<Shadow<T>> consumerVersionado = (RegistroVersionado registroVersionado, RegistroUnico registroUnico, Shadow<T> valorVersionado) -> {
            registroVersionado.addValor("tipoCambio", registroUnico.getEstadoRegistro());
            registroVersionado.addValor("idTipoCambio", registroUnico.getCodigoEstadoRegistro());
        };
        return new ShadowProcesadorRegistrosVersionados<>(consumerVersionado);
    }
    
    private List<RegistroVersionado> procesarJoins(MetadataTablaVersionada tabla, Map<RegistroUnico, RegistroVersionado> entidades, Map<RegistroUnico, Map<String, EntidadId>> cacheJoins) {
        /** Procedo a obtener todos los ids diferentes de una entidad para luego ejecutar un query.
         *  De esta manera se a que entidad pertenece cada id.
         * 
         *  Esto se realiza para que el numero de consultas SQL sea el mas eficiente posible,
         *  realizando una consulta SQL por entidad.
         */
        Map<Class<?>, Set<Object>> idsDiferentes = cacheJoins
                .values()
                .stream()
                .flatMap(cache -> cache.values().stream())
                .filter(entidadId -> !entidadId.isEntidadNula())
                .collect(Collectors.groupingBy(EntidadId::getClase, Collectors.mapping(EntidadId::getId, Collectors.toSet())))
                ;
        
        /** Procedo a realizar una consulta SQL de cada entidad por todos los ids que le pertenecen. 
         *  Se guarda en una cache para luego obtener a que registro versionado pertenece cada entidad.
         * 
         */
        Map<EntidadId, Object> entidadesConsultadas = new HashMap<>();
        for(Map.Entry<Class<?>, Set<Object>> entradaJoins : idsDiferentes.entrySet()) {
            Map<EntidadId, Object> valoresFinales = versionamientoRepository.getEntidadesJoins(entradaJoins.getKey(), entradaJoins.getValue());
            entidadesConsultadas.putAll(valoresFinales);
        }
        
        /** Se recorre todos los registros versionados para luego obtener el valor
         *  de cada columna de su respectiva entidad. Ademas se consulta la metadata
         *  de los joins que es la que determina que columna(s) extraer para el versionamiento.
         * 
         */
        Set<MetadataColumnaVersionadaJoin> metadataJoins = tabla.getColumnaVersionadaJoin();
        List<RegistroVersionado> objetosFinales = new ArrayList<>();
        for(Map.Entry<RegistroUnico, RegistroVersionado> entradaLog : entidades.entrySet()) {
            RegistroUnico registro = entradaLog.getKey();
            RegistroVersionado valores = entradaLog.getValue();
            
            /** Por cada entrada de join, busco por el par commitId/localId
             *  que hace match a la metadata, siendo el prefijo la parte que la
             *  metadata provee. No se usa directamente la clase de la entidad
             *  para dar soporte a multiples joins de una entidad.
             * 
             *  Una vez obtenido el par clase/id del registro versionado,
             *  se procede a obtener el valor de la entidad completa de la cache.
             *  Si por alguna razon no se encuentra valor, se envia la cadena "REGISTO NO ENCONTRADO".
             * 
             *  En caso de encontrar la entidad, se procede a extraer la columna que la metadata
             *  requiere y se agrega su valor a la informacion del registro versionado.
             * 
             *  Si la entidadId es nula, se deja el campo vacio.
             */
            for(MetadataColumnaVersionadaJoin metadataJoin : metadataJoins) {
                EntidadId entidadId = cacheJoins.get(registro)
                        .get(metadataJoin.getPrefijo());

                if(!entidadId.isEntidadNula()) {
                    Object entidad = entidadesConsultadas.get(entidadId);
                    Object valorColumna = entidad != null ? VersionamientoUtils.getValorDePropiedad(entidad, metadataJoin.getColumnaDeEntidad())
                            : "REGISTRO NO ENCONTRADO";

                    valores.addValor(metadataJoin.getNombreColumna(), valorColumna);
                }
                else {
                    valores.addValor(metadataJoin.getNombreColumna(), "");
                }
            }
            
            objetosFinales.add(valores);
        }
        
        return objetosFinales;
    }
    
    /** Obtiene la metadata de las tablas versionadas por el sistema.
     * 
     * @return la metadata de las tablas formateadas como dataTable.
     */
    public DataTablesOutput<MetadataTablaVersionada> getTablasVersionadas() {
        return MetadataIntegrator.getMetadataTablas();
    }

    /** Obtiene el historial de los cambios realizados a un registro. Estos cambios
     *  son los que javers detecto y guardo. El orden de los cambios es desde la creacion
     *  del registro hasta el ultimo cambio realizado al mismo.
     * 
     * @param nombreTabla el nombre del tipo de registro que se debe buscar. Esto es equivalente
     * al nombre de la tabla y viene de la metadata de cada tabla.
     * @param idRegistro el id del registro a buscar su historial
     * @param input los filtros a aplicar a la consulta de parte del cliente.
     * @return los resultados de la consulta listos para ser consumidos por dataTables.
     */
    public DataTablesOutput<Map<String, Object>> getHistorialDeRegistro(String nombreTabla, Integer idRegistro, DataTablesInput input) {
        // obtengo la metadata a usar
        MetadataTablaVersionada tabla = MetadataIntegrator.getMetadataTabla(nombreTabla);
        return getRegistrosVersionadosByIdRegistro(tabla, idRegistro, input);
    }

    private <T> DataTablesOutput<Map<String, Object>> getRegistrosVersionadosByIdRegistro(MetadataTablaVersionada tabla, Integer idRegistro, DataTablesInput input) {
        Class<T> clase = (Class<T>) tabla.getClase();
        
        // obtengo el resultado de la consulta usando la clase obtenida y la informacion del cliente
        QueryResult<Set<RegistroUnico>> idsRegistro = versionamientoRepository.getLogRegistroById(idRegistro, clase, input);
        
        IdTrabajo idTrabajo = new IdTrabajo(idsRegistro);
        
        // ahora realizo la consulta de javers que trae los cambios realizados al sistema en dicha clase.
        List<Shadow<T>> cambios = javers.findShadows(QueryBuilder.byInstanceId(idRegistro, clase)
                .withCommitIds(idTrabajo.getIdsUnicas())
                .withChildValueObjects()
                .build());
        
        ShadowProcesadorRegistrosVersionados procesadorSnapshot = crearProcesadorLogCambiosARegistro();
        AlmacenRegistrosProcesados almacen = procesadorSnapshot.procesar(cambios, idTrabajo, tabla);
        
        List<RegistroVersionado> valoresConJoin = procesarJoins(tabla, almacen.getValoresEntidad(), almacen.getCacheJoins());
        List<Map<String, Object>> valoresFinales = valoresConJoin.stream()
                .sorted()
                .map(RegistroVersionado::toMap)
                .collect(Collectors.toList());
        
        // por ultimo se crea el objeto final que es devuelto al cliente.
        return VersionamientoUtils.crearDataTablesOutput(new QueryResult<>(valoresFinales, idsRegistro.getCount()), input);
    }

    private <T> ShadowProcesadorRegistrosVersionados<T> crearProcesadorLogCambiosARegistro() {
        // se agrega el numero de version como campo quemado
        ConsumerVersionamiento<Shadow<T>> consumerVersionado = (RegistroVersionado registroVersionado, RegistroUnico registroUnico, Shadow<T> valorVersionado) 
                -> registroVersionado.addValor("numeroVersion", registroUnico.getVersion());
        return new ShadowProcesadorRegistrosVersionados(consumerVersionado);
    }

    public ServiceResponse getMetadataTabla(String nombreTabla) {
        MetadataTablaVersionada metadata = MetadataIntegrator.getMetadataTabla(nombreTabla);
        if(metadata == null) {
            return new ServiceResponse(false, "Tabla solicitada no existe...");
        }
        else {
            return new ServiceResponse(true, "Metadata consultada de forma exitosa", metadata);
        }
    }
}
