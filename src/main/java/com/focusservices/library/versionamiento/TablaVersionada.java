package com.focusservices.library.versionamiento;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Esta anotacion indica que una entidad esta versionada y debe aparecer en el catalogo
 *  de logs disponibles.
 * 
 *  Se espera que cada campo que desea ser mostrado sea anotado con la anotacion
 *  {@link com.focusservices.library.versionamiento.CampoVersionado}
 *
 * @author Vansi Olivares
 * @see com.focusservices.library.versionamiento.CampoVersionado
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TablaVersionada {
    String nombre();
    String etiqueta();
}
