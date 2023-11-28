package com.focusservices.library.versionamiento;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.javers.shadow.Shadow;

@Slf4j
public class ShadowProcesadorRegistrosVersionados<T> implements ProcesadorRegistrosVersionados<Shadow<T>> {
    private ConsumerVersionamiento<Shadow<T>> consumerVersionamiento = ConsumerVersionamiento.vacio();

    public ShadowProcesadorRegistrosVersionados(ConsumerVersionamiento<Shadow<T>> consumerVersionamiento) {
        if(consumerVersionamiento != null) {
            this.consumerVersionamiento = consumerVersionamiento;
        }
    }
    
    @Override
    public AlmacenRegistrosProcesados procesar(List<Shadow<T>> logs, IdTrabajo idTrabajo, MetadataTablaVersionada tabla) {
        // ahora se procede a procesar cada registro de javers a los valores finales a enviar
        Map<RegistroUnico, RegistroVersionado> valoresEntidad = new HashMap<>();
        Map<RegistroUnico, Map<String, EntidadId>> cacheJoins = new HashMap<>();
        for(Shadow<T> shadow : logs) {
            BigInteger idCommit = VersionamientoUtils.commitIdToBigInteger(shadow.getCommitId());
            Object valor = VersionamientoUtils.getValorDePropiedad(shadow.get(), tabla.getIdTabla());
            
            Optional<RegistroUnico> optionalRegistroUnico = idTrabajo.getRegistroUnico(valor, idCommit);
            if(optionalRegistroUnico.isPresent()) {
                RegistroUnico registroUnico = optionalRegistroUnico.get();
                RegistroVersionado registroVersionado = procesarEntidad(shadow, tabla, registroUnico);
                valoresEntidad.put(registroUnico, registroVersionado);

                Map<String, EntidadId> joins = obtenerMetadataJoins(shadow, tabla);
                cacheJoins.put(registroUnico, joins);
            }
            else {
                log.warn("No se encontro registro en el versionamiento para el siguiente par."
                        + " localId: " + valor
                        + ", idCommit: " + idCommit
                        + ". Valores seleccionados: " + idTrabajo.toTupla());
            }
            
            
        }
        
        return new AlmacenRegistrosProcesados(valoresEntidad, cacheJoins);
    }
    
    private RegistroVersionado procesarEntidad(Shadow<T> shadow, MetadataTablaVersionada tabla, RegistroUnico registroUnico) {
        /** Detalles de implementacion.
         * 
         *  Al cliente final se envia un Map con las propiedades en la metadata en lugar
         *  de un objeto completo para tener el control completo del objeto. Ademas
         *  asi se pueden agregar otras propiedades extras por registro.
         */
        RegistroVersionado registroVersionado = new RegistroVersionado(registroUnico);
        T valor = shadow.get();
        
        /** Obtengo el valor de cada propiedad local que se encuentra en la metadata.
         *  Si la columna no existe, se arroja una excepcion.
         * 
         */
        for(MetadataColumnaVersionadaLocal columna : tabla.getColumnaVersionadaLocal()) {
            String nombreColumna = columna.getNombreColumna();
            Object valorColumna = VersionamientoUtils.getValorDePropiedad(valor, nombreColumna);
            registroVersionado.addValor(nombreColumna, VersionamientoUtils.transformarValor(valorColumna));
        }
        
        /** Se agregan campos extras dependiendo del caso. Esto es enviado por el cliente
         * 
         */
        consumerVersionamiento.consume(registroVersionado, registroUnico, shadow);
        return registroVersionado;
    }
    
    private Map<String, EntidadId> obtenerMetadataJoins(Shadow<T> shadow, MetadataTablaVersionada tabla) {
        T valor = shadow.get();
        
        /** Obtengo el valor de cada propiedad join que se encuentra en la metadata.
         *  Si la columna no existe, se arroja una excepcion.
         * 
         *  Como Javers solo guarda el id de las entidades y no se cual campo es su id,
         *  obtengo esta informacion en el momento y luego procedo a obtener
         *  el id de la entidad y guardarla como un par clase/id.
         * 
         *  La llave que se guarda del diccionario es el prefijo que la metadata asigno
         *  a esta relacion.
         * 
         *  En el caso en que el registro no haya sido establecido, Javers no guarda informacion
         *  del mismo. Para que el mensaje mostrado al usuario sea coherente, se ha creado un id especifica
         *  llamada 'id nula' que es usada en vez del join. Con esta id el versionamiento sabra que
         *  debe dejar la columna vacia.
         */
        Map<String, EntidadId> entityCache = new HashMap<>();
        for(MetadataColumnaVersionadaJoin columna : tabla.getColumnaVersionadaJoin()) {
            String nombreEntidad = columna.getEntidad();
            Object entidad = VersionamientoUtils.getValorDePropiedad(valor, nombreEntidad);
            if(entidad != null) {
                Class<?> claseEntidad = entidad.getClass();
                String campoId = VersionamientoUtils.getIdTabla(claseEntidad);
                Object idEntidad = VersionamientoUtils.getValorDePropiedad(entidad, campoId);

                EntidadId valorCache = entityCache.getOrDefault(nombreEntidad, new EntidadId(claseEntidad, idEntidad));
                entityCache.put(columna.getPrefijo(), valorCache);
            }
            else {
                EntidadId valorCache = entityCache.getOrDefault(nombreEntidad, EntidadId.getEntidadIdNula());
                entityCache.put(columna.getPrefijo(), valorCache);
            }
        }
        
        return entityCache;
    }
}
