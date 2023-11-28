package com.focusservices.library.commons.datatables.criteria;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Transformador {
    private static final Map<TuplaClases, Function<?, ?>> CACHE_FUNCIONES = crearCacheFunciones();
    
    private static Map<TuplaClases, Function<?, ?>> crearCacheFunciones() {
        // filtros de Boolean
        Map<TuplaClases, Function<?, ?>> cache = new ConcurrentHashMap<>();
        cache.put(new TuplaClases<>(String.class, Boolean.class), 
                (String valor) -> Boolean.parseBoolean(valor));
        
        // filtros para Long
        cache.put(new TuplaClases<>(String.class, Long.class), 
                (String valor) -> Long.parseLong(valor));
        
        // filtros para Integer
        cache.put(new TuplaClases<>(String.class, Integer.class), 
                (String valor) -> Integer.parseInt(valor));
        
        // filtros para LocalDate
        cache.put(new TuplaClases<>(String.class, LocalDate.class), 
                (String valor) -> LocalDate.parse(valor, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        
        // filtros para LocalDateTime
        cache.put(new TuplaClases<>(String.class, LocalDateTime.class), 
                (String valor) -> LocalDate.parse(valor, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay());
        
        return cache;
    }
    
    public static <A, B> B transformarValor(A valor, Class<B> destino) {
        TuplaClases<? extends Object, B> tuplaClases = new TuplaClases<>(valor.getClass(), destino);
        Function<A, B> funcion = (Function<A, B>) CACHE_FUNCIONES.get(tuplaClases);
        if(funcion == null) {
            throw new IllegalArgumentException("Transformador no encontrado para tupla: " + tuplaClases);
        }
        
        return funcion.apply(valor);
    }
    
}
