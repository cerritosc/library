package com.focusservices.library.versionamiento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import com.focusservices.library.commons.datatables.mapping.DataTablesOutput;

/** Construye la metadata de las tablas que son versionadas.
 * 
 *  Para que una tabla sea mostrada en los logs debe ser anotada a nivel de clase con
 *  la anotacion {@link com.focusservices.library.versionamiento.TablaVersionada} y cada
 *  campo que se desea que aparezca en la tabla de logs debe ser anotado con
 *  {@link com.focusservices.library.versionamiento.CampoVersionada}
 * 
 *  *Detalles de Implementacion*
 *  
 *  Para obtener la metadata inicial se usa una clase especial
 *  llamada {@link org.hibernate.integrator.spi.Integrator} la cual envia la
 *  metadata obtenida por Hibernate junto con otros datos extras. Muchos
 *  de los framework de auditoria utilizan este mecanismo para implementar
 *  su logica de negocio.
 * 
 *  La idea fue inspirada en el siguiente enlace web:
 * 
 *      https://thorben-janssen.com/conditional-auditing-hibernate-envers/
 *  
 * @author Vansi Olivares
 * @see com.focusservices.library.versionamiento.TablaVersionada
 * @see com.focusservices.library.versionamiento.CampoVersionada
 * @see org.hibernate.integrator.spi.Integrator
 */
@Slf4j
public class MetadataIntegrator implements Integrator {
    private static final Map<String, MetadataTablaVersionada> METADATA = new HashMap<>();
    
    @Override
    public void integrate(Metadata mtdt, SessionFactoryImplementor sfi, SessionFactoryServiceRegistry sfsr) {
        log.info("Obteniendo metadata de las entidades para versionamiento... ");
        List<Class> classes = mtdt.getEntityBindings()
                .stream()
                .map(PersistentClass::getMappedClass)
                .filter(clase -> clase.getAnnotation(TablaVersionada.class) != null)
                .collect(Collectors.toList());
        
        log.info("Clases obtenidas: " + classes);
        
        for(Class clase : classes) {
            MetadataTablaVersionada metadataTabla = new MetadataTablaVersionada(clase);
            METADATA.put(metadataTabla.getNombre().toLowerCase(), metadataTabla);
        }
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sfi, SessionFactoryServiceRegistry sfsr) {
        /** Este metodo queda vacio porque no requiero destruir nada.
         * 
         */
    }
    
    /** Obtiene la metadata de una tabla. Este metodo es equivalente a obtener
     *  la metadata de un registro tal que el campo 'nombre' de alguna metadata
     *  sea igual al parametro enviado.
     * 
     * @param nombreClase el nombre de la tabla a buscar.
     * @return la metadata de la tabla o una excepcion si no existe el registro.
     */
    public static MetadataTablaVersionada getMetadataTabla(String nombreClase) {
        return METADATA.get(nombreClase.toLowerCase());
    }

    /** Obtiene la metadata de todas las tablas disponibles para auditar el versionamiento.
     *  La metadata incluye toda la informacion necesaria para crear vistas de cada
     *  tabla de forma dinamica y el campo 'nombre' debe ser usado en todas las 
     *  transacciones posteriores (auditar los registros de X origen).
     * 
     * @return la metadata de todas las tablas versionadas
     */
    public static DataTablesOutput<MetadataTablaVersionada> getMetadataTablas() {
        DataTablesOutput<MetadataTablaVersionada> output = new DataTablesOutput<>();
        output.setData(new ArrayList<>(METADATA.values()));
        output.setDraw(1);
        output.setRecordsFiltered(METADATA.size());
        output.setRecordsTotal(METADATA.size());
        
        return output;
    }
}
