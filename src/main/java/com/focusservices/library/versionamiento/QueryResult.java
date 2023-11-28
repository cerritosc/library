package com.focusservices.library.versionamiento;

import lombok.AllArgsConstructor;
import lombok.Value;

/** Clase que guarda la informacion de un query y el conteo del mismo. 
 *
 * @author Vansi Adonay Olivares
 */
@Value
@AllArgsConstructor
public class QueryResult<T> {
    /** Indica el valor que el query retorna en si.
     * 
     */
    private T value;
    
    /** Indica el conteo de registros que un query obtiene. Equivalente
     *  a un count(*) del query.
     * 
     */
    private long count;
}
