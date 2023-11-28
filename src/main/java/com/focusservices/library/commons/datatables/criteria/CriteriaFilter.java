package com.focusservices.library.commons.datatables.criteria;

public class CriteriaFilter {
    private String nombre;
    private Class<?> clase;
    private String valor;

    public CriteriaFilter(String nombre, Class<?> clase, String valor) {
        this.nombre = nombre;
        this.clase = clase;
        this.valor = valor;
    }

    public String getNombre() {
        return nombre;
    }

    public Class<?> getClase() {
        return clase;
    }

    public String getValor() {
        return valor;
    }
    
    public Object getValorTransformado() {
        return Transformador.transformarValor(valor, clase);
    }
}
