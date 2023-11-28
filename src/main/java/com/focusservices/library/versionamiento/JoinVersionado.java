package com.focusservices.library.versionamiento;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Esta anotacion indica que este campo es un id de una tabla a la cual se debe hacer join.
 *  Esta anotacion solo debe ser usada en DTOs que no tiene mapeado una entidad como
 *  tal sino que solo poseen su ID, pero desean mostrar en el log un campo del join.
 *
 * @author Vansi Olivares
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JoinVersionado {
    Class<?> entidad();
    String campoEntidad();
    String etiqueta();
    int orden();
}
