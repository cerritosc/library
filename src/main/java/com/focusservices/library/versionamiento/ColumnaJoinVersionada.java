package com.focusservices.library.versionamiento;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Esta anotacion indica que este campo debe aparecer en las pantallas de log del sistema.
 *  Esta anotacion solo debe ser usada dentro de la notacion {@link com.focusservices.library.versionamiento.EntidadJoinVersionado}
 *  , es decir por campos que son parte de un Join en una Entidad.
 *
 * @author Vansi Olivares
 * @see com.focusservices.library.versionamiento.EntidadJoinVersionado
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ColumnaJoinVersionada {
    /** Indica el campo fisico del cual se obtiene la información. Dado
     *  que la informacion no es directamente anotada al campo deseado sino
     *  que va dentro de la notacion {@link com.focusservices.library.versionamiento.EntidadJoinVersionado},
     *  se debe indicar que campo debe obtener el registrador de logs.
     * 
     * @return el campo mapeado en la entidad al cual se extraerá la información.
     */
    String campo();
    
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
