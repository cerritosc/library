package com.focusservices.library.versionamiento;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Esta anotacion indica que este join es parte de los logs del sistema.
 *  A la vez, indica las columnas a tomar de la entidad a la que se le hace join.
 *  Esta anotacion solo debe ser usada para join ManyToOne o OneToOne.
 * 
 *  Por el momento los logs de los joins obtienen la informacion
 *  de las tablas en si y no de los logs de Javers.
 * 
 *  A continuacion se muestra un ejemplo de su uso:
 * 
 *  <pre>
 *      public class OrdenItem ... {
 * 
 *          &#64;Column
 *          private String descripcionItem;
 *      }
 * 
 *      ...
 * 
 *      public class Orden ... {
 *          ...
 * 
 *          &#64;ManyToOne
 *          &#64;EntidadJoinVersionado(columnas = {
 *              &#64;ColumnaJoinVersionada(campo = "descripcionItem", etiqueta = "Item", orden = 10)
 *          })
 *      }
 *  </pre>
 *
 * @author Vansi Olivares
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EntidadJoinVersionado {
    /** Obtiene la(s) columna(s) a mostrar en el log. Puede ser una o más dependiendo
     *  de lo requerido. Cabe notar que las columnas deben pertenecer a la entidad anotada,
     *  como el ejemplo mencionado en la clase.
     * 
     * @return las columnas a mostrar que son de la entidad anotada.
     */
    ColumnaJoinVersionada[] columnas();
}
