package com.focusservices.library.versionamiento;

import lombok.AllArgsConstructor;
import lombok.Value;

/** Pequeña clase que representa un id y una clase origen. Es usado para
 *  representar ids de diferentes entidades.
 * 
 * @author Vansi Olivares
 */
@Value
@AllArgsConstructor
public class EntidadId {
    private Class<?> clase;
    private Object id;
    
    private static final EntidadId ENTIDAD_NULA = new EntidadId(Object.class, -1);
    
    public static EntidadId getEntidadIdNula() {
        return ENTIDAD_NULA;
    }
    
    public boolean isEntidadNula() {
        return this.equals(ENTIDAD_NULA);
    }
}
