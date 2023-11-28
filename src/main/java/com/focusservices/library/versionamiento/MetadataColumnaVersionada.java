package com.focusservices.library.versionamiento;

public interface MetadataColumnaVersionada extends Comparable<MetadataColumnaVersionada> {
    String getNombreColumna();
    String getEtiquetaColumna();
    int getOrden();
    
    @Override
    public default int compareTo(MetadataColumnaVersionada o) {
        int valor = this.getOrden() - o.getOrden();
        if(valor == 0) {
            return this.getNombreColumna().compareTo(o.getNombreColumna());
        }
        else return valor;
    }
}
