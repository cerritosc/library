package com.focusservices.library.versionamiento;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.InstanceId;


public class SnapshotProcesadorRegistrosVersionados implements ProcesadorRegistrosVersionados<CdoSnapshot> {
    private ConsumerVersionamiento<CdoSnapshot> consumerVersionamiento = ConsumerVersionamiento.vacio();
    
    public SnapshotProcesadorRegistrosVersionados(ConsumerVersionamiento<CdoSnapshot> consumerVersionamiento) {
        if(consumerVersionamiento != null) {
            this.consumerVersionamiento = consumerVersionamiento;
        }
    }

    @Override
    public AlmacenRegistrosProcesados procesar(List<CdoSnapshot> logs, IdTrabajo idTrabajo, MetadataTablaVersionada tabla) {
        // se procesan los registros
        Map<RegistroUnico, RegistroVersionado> valoresEntidad = new HashMap<>();
        Map<RegistroUnico, Map<String, EntidadId>> cacheJoins = new HashMap<>();
        for(CdoSnapshot cambio : logs) {
            BigInteger idCommit = VersionamientoUtils.commitIdToBigInteger(cambio.getCommitId());
            Object valor = cambio.getPropertyValue(tabla.getIdTabla());
                    
            Optional<RegistroUnico> optionalRegistroUnico = idTrabajo.getRegistroUnico(valor, idCommit);
            if(optionalRegistroUnico.isPresent()) {
                RegistroUnico registroUnico = optionalRegistroUnico.get();
                RegistroVersionado valores = procesarCambioDeRegistro(tabla, cambio, registroUnico);
                valoresEntidad.put(registroUnico, valores);

                Map<String, EntidadId> cacheJoin = obtenerMetadataJoinsDetalles(cambio, tabla);
                cacheJoins.put(registroUnico, cacheJoin);
            }
        }
        
        return new AlmacenRegistrosProcesados(valoresEntidad, cacheJoins);
    }
    
    private RegistroVersionado procesarCambioDeRegistro(MetadataTablaVersionada tabla, CdoSnapshot cambio, RegistroUnico registroUnico) {
        RegistroVersionado registroVersionado = new RegistroVersionado(registroUnico);
        for(MetadataColumnaVersionada columna: tabla.getColumnaVersionada()) {
            String nombreColumna = columna.getNombreColumna();
            if(cambio.hasChangeAt(nombreColumna)) {
                Object valorCambiado = cambio.getPropertyValue(nombreColumna);
                registroVersionado.addValor(nombreColumna, VersionamientoUtils.transformarValor(valorCambiado));
            }
            else {
                registroVersionado.addValor(nombreColumna, "");
            }
        }
        
        // se agregan campos extras segun el cliente lo desea.
        consumerVersionamiento.consume(registroVersionado, registroUnico, cambio);
        return registroVersionado;
    }
    
    private Map<String, EntidadId> obtenerMetadataJoinsDetalles(CdoSnapshot snapshot, MetadataTablaVersionada tabla) {
        /** Obtengo el valor de cada propiedad join que se encuentra en la metadata.
         *  Si la columna no existe, se arroja una excepcion.
         * 
         *  En este caso Javers mapea un id de una entidad como la clase InstanceId,
         *  se obtiene la propiedad si esta existe y luego se extrae el valor.
         * 
         *  Al igual que con la otra extraccion de metadata, se tiene como llave
         *  el prefijo que la metadata le asigno al join y el valor es un par clase/id.
         */
        Map<String, EntidadId> entityCache = new HashMap<>();
        for(MetadataColumnaVersionadaJoin columna : tabla.getColumnaVersionadaJoin()) {
            String nombreEntidad = columna.getEntidad();
            Object posibleIdEntidad = snapshot.getPropertyValue(nombreEntidad);
            if(posibleIdEntidad instanceof InstanceId) {
                InstanceId id = (InstanceId) posibleIdEntidad;
                try {
                    Class<?> claseEntidad = Class.forName(id.getTypeName());
                    Object idEntidad = id.getCdoId();
                    
                    EntidadId valorCache = entityCache.getOrDefault(nombreEntidad, new EntidadId(claseEntidad, idEntidad));
                    entityCache.put(columna.getPrefijo(), valorCache);
                } catch (ClassNotFoundException ex) {
                    throw new IllegalArgumentException("Ocurrio un error al intentar leer la clase: " 
                            + id.getTypeName(), ex);
                }
            }
            else {
                EntidadId valorCache = entityCache.getOrDefault(nombreEntidad, EntidadId.getEntidadIdNula());
                entityCache.put(columna.getPrefijo(), valorCache);
            }
        }
        
        return entityCache;
    }
    
}
