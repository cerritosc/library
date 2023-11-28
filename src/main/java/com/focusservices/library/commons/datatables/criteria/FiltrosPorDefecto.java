package com.focusservices.library.commons.datatables.criteria;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.LoggerFactory;

public class FiltrosPorDefecto {
    public static final String PARAMETRO_POR_DEFECTO = "cn";
    public static final List<String> PARAMETROS_EQUALS = Arrays.asList("eq", "=");
    public static final String PARAMETRO_NO_EQUALS = "ne";
    public static final String PARAMETRO_EQUALS_OR_NULL = "eqn";
    public static final String PARAMETRO_NULO = "nu";
    
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FiltrosPorDefecto.class);
    
    private static final Map<Class<?>, Map<String, FilterToPredicate>> FILTROS_GENERADOS = new HashMap<>();
    
    static {
        // generados para cualquier objecto que venga, estos son en comun para todos los tipos
        Class<Object> claseBase = Object.class;
        Map<String, FilterToPredicate> mapaGeneral = crearParametrosPorDefecto();
        
        // procedo a agregar todo estos parametros en el mapa principal usando como llave Object.class
        FILTROS_GENERADOS.put(claseBase, mapaGeneral);
        
        
        FILTROS_GENERADOS.put(String.class, crearFiltrosString());
        FILTROS_GENERADOS.put(BigDecimal.class, crearFiltrosBigDecimal());
    }

    private static Map<String, FilterToPredicate> crearParametrosPorDefecto() {
        Map<String, FilterToPredicate> mapaGeneral = new HashMap<>();
        
        /**
         *  Filtro equals para todos los casos por defecto
         */
        FilterToPredicate filtroEquals = (From root, CriteriaBuilder cb, CriteriaFilter regla) 
                -> cb.equal(root.get(regla.getNombre()), regla.getValorTransformado());
        for(String parametro: PARAMETROS_EQUALS) {
            mapaGeneral.put(parametro, filtroEquals);
        }
        
        // se agrega el 'filtro por defecto'
        mapaGeneral.put(PARAMETRO_POR_DEFECTO, filtroEquals);
        
        /**
         *  Caso no equals
         */
        mapaGeneral.put(PARAMETRO_NO_EQUALS, (FilterToPredicate) (From root, CriteriaBuilder cb, CriteriaFilter regla) 
                -> cb.notEqual(root.get(regla.getNombre()), regla.getValorTransformado()));
        
        /** Caso null
         * 
         */
        mapaGeneral.put(PARAMETRO_NULO, (FilterToPredicate) (From root, CriteriaBuilder cb, CriteriaFilter regla) 
                -> cb.isNull(root.get(regla.getNombre())));
        
        /**
         *  Caso equals or null
         */
        mapaGeneral.put(PARAMETRO_EQUALS_OR_NULL, (FilterToPredicate) (From root, CriteriaBuilder cb, CriteriaFilter regla) -> {
            Predicate predicadoEquals = cb.equal(cb.upper(root.get(regla.getNombre())), regla.getValor().toUpperCase());
            Predicate predicadoNull = cb.isNull(root.get(regla.getNombre()));
            return cb.or(predicadoEquals, predicadoNull);
        });
        
        return mapaGeneral;
    }
    
    private static Map<String, FilterToPredicate> crearFiltrosString() {
        Map<String, FilterToPredicate> mapaString = new HashMap<>();
        FilterToPredicate filtroLike = (From root, CriteriaBuilder cb, CriteriaFilter regla) -> cb.like(cb.upper(root.<String>get(regla.getNombre())), "%" + regla.getValor().toUpperCase() + "%");
        mapaString.put("lk", filtroLike);
        mapaString.put(PARAMETRO_POR_DEFECTO, filtroLike);
        return mapaString;
    }
    
    private static Map<String, FilterToPredicate> crearFiltrosBigDecimal() {
        Map<String, FilterToPredicate> mapaString = new HashMap<>();
        FilterToPredicate filtroLike = (From root, CriteriaBuilder cb, CriteriaFilter regla) -> cb.like(cb.function("TO_CHAR", String.class, root.<String>get(regla.getNombre())) , "%" + regla.getValor().toUpperCase() + "%");
        mapaString.put("lk", filtroLike);
        mapaString.put(PARAMETRO_POR_DEFECTO, filtroLike);
        return mapaString;
    }
    
    public static Map<String, FilterToPredicate> getFiltrosDisponiblesParaField(Class<?> claseField) {
        return FILTROS_GENERADOS.get(claseField);
    }
    
    /** Obtiene el filtro adecuado en base a la clase del campo y tipo de operacion deseada
     * 
     * @param claseField la clase del campo a filtrar
     * @param operandoFiltro el tipo de filtro a efectuar
     * @return el filtro adecuado o una excepcion si no existe
     */
    public static FilterToPredicate getFiltroParaField(Class<?> claseField, String operandoFiltro) {
        Class<Object> clasePorDefecto = Object.class;
        
        Map<String, FilterToPredicate> mapaPorTipo = FILTROS_GENERADOS.get(claseField);
        Map<String, FilterToPredicate> mapaGeneral = FILTROS_GENERADOS.get(clasePorDefecto);
        
        // si no hay filtros especiales se viene aqui sin problemas
        if(mapaPorTipo == null) {
            mapaPorTipo = new HashMap<>();
        }
        
        /** El orden de los filtros es el siguiente:
         *  
         *  - Operando coincide y esta en el map de filtros por tipo
         *  - Operando coincide y esta en el map general
         *  - Operando no coincide y esta en map de filtros por tipo
         *  - Operando no coincide y esta en el map general
         */
        List<FilterToPredicate> ordenDeEvaluacion = new ArrayList<>();
        ordenDeEvaluacion.add(mapaPorTipo.get(operandoFiltro));
        ordenDeEvaluacion.add(mapaGeneral.get(operandoFiltro));
        ordenDeEvaluacion.add(mapaPorTipo.get(PARAMETRO_POR_DEFECTO));
        ordenDeEvaluacion.add(mapaGeneral.get(PARAMETRO_POR_DEFECTO));
        
        return encontrarPrimerPredicadoNoNulo(ordenDeEvaluacion, operandoFiltro);
    }
    
    private static FilterToPredicate encontrarPrimerPredicadoNoNulo(List<FilterToPredicate> predicadosCandidatos, String operandoFiltro) {
        for(FilterToPredicate predicado : predicadosCandidatos) {
            if(predicado != null) {
                return predicado;
            }
        }
        throw new UnsupportedOperationException("No se pudo procesar el siguiente filtro: " + operandoFiltro);
    }
}
