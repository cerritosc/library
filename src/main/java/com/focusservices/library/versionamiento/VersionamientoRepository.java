package com.focusservices.library.versionamiento;

import java.util.Map;
import java.util.Set;
import com.focusservices.library.commons.datatables.mapping.DataTablesInput;

public interface VersionamientoRepository {
    /** Consulta los commitId de javers que pertenezcan a una clase aplicando los filtros 
     *  solicitados por el cliente.
     * 
     * @param clase la clase que se desea consultar. Esta debe venir de la metadata.
     * @param input los filtros a aplicar.
     * @return los ids que cumplen con la criteria y el conteo total de registros.
     */
    QueryResult<Set<RegistroUnico>> getUltimosIdsRegistros(Class<?> clase, DataTablesInput input);
    
    /** Obtiene los commitId de los cambios realizados a un registro en particular que
     *  cumplen con la criteria enviada por el cliente.
     * 
     * @param idRegistro el id fisico del registro.
     * @param clase la clase a la que pertenece el registro.
     * @param input los filtros a aplicar 
     * @return el listado de registros que cumplen con lo anterior y el conteo de registros.
     */
    QueryResult<Set<RegistroUnico>> getLogRegistroById(Integer idRegistro, Class<?> clase, DataTablesInput input);

    public <T> Map<EntidadId, Object> getEntidadesJoins(Class<T> aClass, Set<Object> value);
}
