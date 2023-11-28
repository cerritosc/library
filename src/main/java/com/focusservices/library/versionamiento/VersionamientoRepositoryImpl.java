package com.focusservices.library.versionamiento;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.focusservices.library.commons.datatables.mapping.DataTablesInput;

@Component
@Slf4j
public class VersionamientoRepositoryImpl implements VersionamientoRepository {
    
    private static final String SQL_CONTEO_GENERICO = "select count(1) from (${query})";
    
    private static final String SQL_ULTIMO_COMMIT = "with data_log as (\n" +
                                                    "    select g.local_id\n" +
                                                    "    , case s.type\n" +
                                                    "        when 'TERMINAL'  then -1\n" +
                                                    "        else c.COMMIT_id\n" +
                                                    "       end     as commit_id\n" +
                                                    "    ,  case s.type\n" +
                                                    "        when 'INITIAL'   then 0\n" +
                                                    "        when 'UPDATE'    then 1\n" +
                                                    "        when 'TERMINAL'  then 2\n" +
                                                    "       end     as data_type\n" +
                                                    "    , s.STATE\n" +
                                                    "    from jv_commit      c\n" +
                                                    "    join jv_snapshot    s on (s.COMMIT_FK = c.COMMIT_PK)\n" +
                                                    "    join jv_global_id   g on (g.GLOBAL_ID_PK = s.GLOBAL_ID_FK)\n" +
                                                    "    where g.TYPE_NAME = :tipo\n" +
                                                    ")\n" +
                                                    "select dm.*\n" +
                                                    "from (\n" +
                                                    "    select d.local_id\n" +
                                                    "        , MAX(d.commit_id)      as commit_id\n" +
                                                    "        , MAX(d.data_type)      as data_type\n" +
                                                    "        , 1                     as version\n" +
                                                    "    from data_log d\n" +
                                                    "    group by d.local_id\n" +
                                                    ") dm\n" +
                                                    "join data_log dx on (dx.local_id = dm.local_id and dx.commit_id = dm.commit_id)";
    
    private static final String SQL_HISTORIAL_REGISTRO = "select g.local_id\n" +
                                                "    , c.COMMIT_id   as commit_id\n" +
                                                "    , case s.type\n" +
                                                "        when 'INITIAL'   then 0\n" +
                                                "        when 'UPDATE'    then 1\n" +
                                                "        when 'TERMINAL'  then 2\n" +
                                                "      end           as data_type\n" +
                                                "    , s.version     as version\n" +
                                                "from jv_commit      c\n" +
                                                "join jv_snapshot    s on (s.COMMIT_FK = c.COMMIT_PK)\n" +
                                                "join jv_global_id   g on (g.GLOBAL_ID_PK = s.GLOBAL_ID_FK)\n" +
                                                "where g.TYPE_NAME = :tipo\n" +
                                                "and g.local_id = :idRegistro\n" +
                                                "and s.type != 'TERMINAL'\n" +
                                                "order by s.version asc";
    
    private static final String SQL_ENTIDAD_IN = "select *\n"
                                                + "from ${entidad}\n"
                                                + "where ${campoId} in (:ids)";
    
    @PersistenceContext(name = "prueba")
    private EntityManager entityManager;
    
    @Override
    public QueryResult<Set<RegistroUnico>> getUltimosIdsRegistros(Class<?> clase, DataTablesInput input) {
        OrdenamientoNativo ordenamiento = new OrdenamientoNativo("dx.state", clase, input, campo -> {
            if(campo.equals("tipoCambio")) {
                return Optional.of("dm.data_type");
            }
            else return Optional.empty();
        });
        
        String sqlFinal = SQL_ULTIMO_COMMIT + ordenamiento.getOrdenamientoResultante();
        
        return procesarQueryNativo(sqlFinal
                , input
                , query -> query.setParameter("tipo", VersionamientoUtils.getNombreVersionamiento(clase))
                , this::procesarQueryRegistroUnico
                );
    }

    private Set<RegistroUnico> procesarQueryRegistroUnico(List<Object[]> resultado) {
        return resultado.stream()
                .map(entrada -> new RegistroUnico(
                        entrada[0]
                        , new BigDecimal(entrada[1].toString())
                        , Integer.parseInt(entrada[2].toString())
                        , Integer.parseInt(entrada[3].toString())
                    )
                )
                .collect(Collectors.toSet());
    }

    @Override
    public QueryResult<Set<RegistroUnico>> getLogRegistroById(Integer idRegistro, Class<?> clase, DataTablesInput input) {
        return procesarQueryNativo(SQL_HISTORIAL_REGISTRO
                , input
                , query -> {
                     query.setParameter("tipo", VersionamientoUtils.getNombreVersionamiento(clase));
                     query.setParameter("idRegistro", idRegistro);
                  }
                , this::procesarQueryRegistroUnico
                );
    }
    
    private <T> QueryResult<T> procesarQueryNativo(String queryBase, DataTablesInput input, Consumer<Query> setParametros, Function<List<Object[]>, T> procesador) {
        Query query = entityManager.createNativeQuery(queryBase);
        setParametros.accept(query);
        query.setFirstResult(input.getStart());
        query.setMaxResults(input.getLength());
        
        List<Object[]> resultado = query.getResultList();
        T resultadoFinal = procesador.apply(resultado);
        
        Query queryConteo = entityManager.createNativeQuery(SQL_CONTEO_GENERICO.replace("${query}", queryBase));
        setParametros.accept(queryConteo);
        long conteo = Long.parseLong(queryConteo.getSingleResult().toString());
        
        return new QueryResult<>(resultadoFinal, conteo);
    } 

    @Override
    public <T> Map<EntidadId, Object> getEntidadesJoins(Class<T> aClass, Set<Object> value) {
        String nombreTabla = VersionamientoUtils.getNombreTablaFisica(aClass);
        String campoIdTabla = VersionamientoUtils.getIdTablaFisica(aClass);
        String campoId = VersionamientoUtils.getIdTabla(aClass);
        
        String sqlFinal = SQL_ENTIDAD_IN.replace("${entidad}", nombreTabla)
                .replace("${campoId}", campoIdTabla);
        
        Query query = entityManager.createNativeQuery(sqlFinal, aClass);
        query.setParameter("ids", value);
        
        List<T> valores = query.getResultList();
        
        log.info("Ids usados: " + value);
        log.info("Cantidad de registros obtenidos: " + valores.size());
        
        Map<EntidadId, Object> valoresFinales = new HashMap<>();
        for(T valor : valores) {
            Object id = VersionamientoUtils.getValorDePropiedad(valor, campoId);
            EntidadId entidadId = new EntidadId(aClass, id);
            valoresFinales.put(entidadId, valor);
        }
        
        return valoresFinales;
    }
}
