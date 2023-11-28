package com.focusservices.library.versionamiento;

import java.util.Map;
import lombok.Value;

/**
 *
 * @author VOlivares
 */
@Value
public class AlmacenRegistrosProcesados {
    private Map<RegistroUnico, RegistroVersionado> valoresEntidad;
    private Map<RegistroUnico, Map<String, EntidadId>> cacheJoins;
}
