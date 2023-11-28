package com.focusservices.library.versionamiento;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Data
@EqualsAndHashCode
@Slf4j
public class MetadataTablaVersionada implements Serializable {
    private String nombre;
    private String etiqueta;
    
    @JsonIgnore
    private Class<?> clase;
    
    private TreeSet<MetadataColumnaVersionada> columnaVersionada = new TreeSet<>();

    public MetadataTablaVersionada(Class<?> claseReal) {
        TablaVersionada tablaVersionada = claseReal.getAnnotation(TablaVersionada.class);
        
        this.clase = claseReal;
        this.nombre = tablaVersionada.nombre();
        this.etiqueta = tablaVersionada.etiqueta();
        
        obtenerInformacionClase(claseReal);
    }
    
    private void obtenerInformacionClase(Class<?> clase) {
        obtenerInformacionCamposLocales(clase);
        obtenerInformacionCamposJoin(clase);
    }

    private void obtenerInformacionCamposLocales(Class<?> clase1) {
        List<Field> camposVersionados = Stream.of(clase1.getDeclaredFields())
                .filter(campo -> campo.getAnnotation(ColumnaVersionada.class) != null)
                .collect(Collectors.toList());
        
        for (Field campo : camposVersionados) {
            ColumnaVersionada columna = campo.getAnnotation(ColumnaVersionada.class);
            String nombreColumna = campo.getName();
            String etiquetaColumna = columna.etiqueta();
            int orden = columna.orden();
            
            MetadataColumnaVersionadaLocal metadataColumna = new MetadataColumnaVersionadaLocal(nombreColumna, etiquetaColumna, orden);
            if(!columnaVersionada.add(metadataColumna)) {
                log.warn("Se ha detectado que la entidad " + clase.getSimpleName()
                        + " posee 2 campos con el mismo valor de 'orden': "
                        + orden 
                        + ", lo que significa que el segundo valor es ignorado: "
                        + nombreColumna
                        + ", por favor realizar las correcciones pertinentes..."
                );
            }
        }
    }
    
    private void obtenerInformacionCamposJoin(Class<?> clase1) {
        List<Field> entidadesVersionados = Stream.of(clase1.getDeclaredFields())
                .filter(campo -> campo.getAnnotation(EntidadJoinVersionado.class) != null)
                .collect(Collectors.toList());
        
        for(int i = 0; i < entidadesVersionados.size(); i++) {
            Field campo = entidadesVersionados.get(i);
            EntidadJoinVersionado entidadJoin = campo.getAnnotation(EntidadJoinVersionado.class);
            String prefijoEntidad = "e" + i + "-";
            
            for(ColumnaJoinVersionada columna : entidadJoin.columnas()) {
                MetadataColumnaVersionadaJoin metadataJoin = new MetadataColumnaVersionadaJoin(columna, campo.getName(), prefijoEntidad);
                columnaVersionada.add(metadataJoin);
            }
        }
    }
    
    public String getIdTabla() {
        return VersionamientoUtils.getIdTabla(getClase());
    }
    
    @JsonIgnore
    public Set<MetadataColumnaVersionadaLocal> getColumnaVersionadaLocal() {
        return columnaVersionada.stream()
                .filter(columna -> columna instanceof MetadataColumnaVersionadaLocal)
                .map(columna -> (MetadataColumnaVersionadaLocal)columna)
                .collect(Collectors.toSet());
    }
    
    @JsonIgnore
    public Set<MetadataColumnaVersionadaJoin> getColumnaVersionadaJoin() {
        return columnaVersionada.stream()
                .filter(columna -> columna instanceof MetadataColumnaVersionadaJoin)
                .map(columna -> (MetadataColumnaVersionadaJoin)columna)
                .collect(Collectors.toSet());
    }
}
