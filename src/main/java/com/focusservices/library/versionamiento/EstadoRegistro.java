package com.focusservices.library.versionamiento;

import java.util.HashMap;
import java.util.Map;

public enum EstadoRegistro {
    INICIAL(0, "Inicial")
    , MODIFICADO(1, "Modificado")
    , ELIMINADO(2, "Eliminado");
    
    private static final Map<Integer, EstadoRegistro> MAPA = crearMapa();
    
    private static Map<Integer, EstadoRegistro> crearMapa() {
        Map<Integer, EstadoRegistro> mapaTemporal = new HashMap<>();
        for(EstadoRegistro estado: values()) {
            mapaTemporal.put(estado.getCodigoEstado(), estado);
        }
        
        return mapaTemporal;
    }

    private final Integer codigoEstado;
    private final String nombre;
    
    private EstadoRegistro(Integer codigoEstado, String nombre) {
        this.codigoEstado = codigoEstado;
        this.nombre = nombre;
    }

    public int getCodigoEstado() {
        return codigoEstado;
    }

    public String getNombre() {
        return nombre;
    }
    
    public static String getNombreEstado(Integer codigoEstado) {
        return MAPA.get(codigoEstado).name();
    }
}
