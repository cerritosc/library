package com.focusservices.library.versionamiento;

import java.util.List;

/** Interface que debe ser implementado por los procesadores de logs de Javers.
 *  La idea es estandarizar la entrada y salida de datos.
 *
 * @author Vansi Olivares
 */
public interface ProcesadorRegistrosVersionados<L> {
    AlmacenRegistrosProcesados procesar(List<L> logs, IdTrabajo idTrabajo, MetadataTablaVersionada tabla);
}
