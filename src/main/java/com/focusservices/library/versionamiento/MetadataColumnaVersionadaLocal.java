package com.focusservices.library.versionamiento;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class MetadataColumnaVersionadaLocal implements MetadataColumnaVersionada {
    private String nombreColumna;
    private String etiquetaColumna;
    private int orden;
}
