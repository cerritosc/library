package com.focusservices.library.versionamiento;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class MetadataColumnaVersionadaJoin implements MetadataColumnaVersionada {
    private String nombreColumna;
    private String etiquetaColumna;
    private int orden;
    
    @JsonIgnore
    private String prefijo;
    
    @JsonIgnore
    private String entidad;
    
    @JsonIgnore
    private String columnaDeEntidad;

    public MetadataColumnaVersionadaJoin(ColumnaJoinVersionada columna, String entidad, String prefijo) {
        this.entidad = entidad;
        this.prefijo = prefijo;
        this.nombreColumna = prefijo + columna.campo();
        this.columnaDeEntidad = columna.campo();
        this.etiquetaColumna = columna.etiqueta();
        this.orden = columna.orden();
    }
}
