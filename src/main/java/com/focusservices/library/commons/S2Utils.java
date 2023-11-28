package com.focusservices.library.commons;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.data.domain.Slice;

/**
 *
 * @author VOlivares
 */
public class S2Utils {
    
    private S2Utils() {
        
    }
    
    /** Metodo helper que decora las peticiones web para la creacion de listados
     *  JSON para Select2. La idea de este metodo es no copiar/pegar lo mismo una y otra vez.
     * 
     * @param <T> la clase que se procesara como lista de select2
     * @param data la informacion que he consultado de algun lugar y que debe ser procesada
     * @param mapper el transformador de la informacion que envio a un elemento S2
     * @param rows la cantidad total de filas solicitadas.
     * @return un objeto que tiene una lista que es compatible con Select2.
     */
    public static <T extends Serializable> S2Response<S2> procesarPeticion(Supplier<Slice<T>> data, Function<T, S2> mapper, Integer rows) {
        S2Response<S2> response;
        if (rows != 0) {
            Slice<T> list = data.get();
            List<S2> results = list.getContent().stream()
                    .map(mapper)
                    .toList();
            response = new S2Response<>(results, list.hasNext());
        } else {
            response = new S2Response<>();
        }
        return response;
    }
}
