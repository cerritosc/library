package com.focusservices.library.versionamiento;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Esta anotacion indica que este campo debe aparecer en las pantallas de log del sistema.
 *  Esta anotacion solo debe ser usada por campos que son o fueron columnas 
 *  de la entidad.
 * 
 *  Para que funcione, se debe decorar una propiedad de la entidad como lo muestra el ejemplo:
 * 
 *  <pre>
 *      &#64;ColumnaVersionada(etiqueta = "Fecha de Inicio", orden = 3)
 *      private LocalDate fcInicio;
 *  </pre>
 * 
 *  Por favor, siempre debe anotarse la propiedad id de una entidad con esta anotación.
 *
 * @author Vansi Olivares
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ColumnaVersionada {
    /** Etiqueta de la columna. Esta etiqueta es usada en las pantallas del usuario
     *  por lo que debe escribirse algo que sea representativo para el usuario final,
     *  respetando tildes..
     * 
     * @return descripción que representa al campo.
     */
    String etiqueta();
    
    /** Indica el orden en que debe ser mostrado esta columna. Se espera que en una
     *  entidad el campo orden sea unico entre todas las otras anotaciones que requieran uno.
     *  El orden no necesariamente tiene que ser consecutivo, pero se recomienda dejarlo asi.
     * 
     * @return el orden al que este campo pertenece.
     */
    int orden();
}
