/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.focusservices.library.commons.datatables.criteria;

import java.util.Objects;

public class TuplaClases<A, B> {
    private Class<A> claseOrigen;
    private Class<B> claseDestino;

    public TuplaClases(Class<A> claseOrigen, Class<B> claseDestino) {
        this.claseOrigen = claseOrigen;
        this.claseDestino = claseDestino;
    }

    @Override
    public String toString() {
        return "TuplaClases{" + "claseOrigen=" + claseOrigen + ", claseDestino=" + claseDestino + '}';
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.claseOrigen);
        hash = 41 * hash + Objects.hashCode(this.claseDestino);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TuplaClases<?, ?> other = (TuplaClases<?, ?>) obj;
        if (!Objects.equals(this.claseOrigen, other.claseOrigen)) {
            return false;
        }
        if (!Objects.equals(this.claseDestino, other.claseDestino)) {
            return false;
        }
        return true;
    }

}
