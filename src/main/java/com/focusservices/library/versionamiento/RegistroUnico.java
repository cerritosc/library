package com.focusservices.library.versionamiento;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode
@Value
@AllArgsConstructor
public class RegistroUnico implements Comparable<RegistroUnico> {
    private Object idLocal;
    private BigDecimal idCommit;
    private Integer codigoEstadoRegistro;
    private int version;
    
    public String getEstadoRegistro() {
        return EstadoRegistro.getNombreEstado(codigoEstadoRegistro);
    }

    @Override
    public int compareTo(RegistroUnico o) {
        return idCommit.compareTo(o.idCommit);
    }
}
