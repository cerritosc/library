package com.focusservices.library.versionamiento;

import java.util.Comparator;
import com.focusservices.library.commons.datatables.mapping.Column;
import com.focusservices.library.commons.datatables.mapping.DataTablesInput;
import com.focusservices.library.commons.datatables.mapping.Order;

/** Indica el orden de sorting que debe tener los registros versionados.
 *  Se asume que cada campo tiene orden natural e implementa la interface
 *  {@link java.util.Comparator}.
 *
 * @author Vansi Olivares
 * @see java.util.Comparator
 */
public class RegistroOrden implements Comparator<RegistroVersionado> {
    private String nombreColumna;
    private String order;

    public RegistroOrden(DataTablesInput input) {
        Order ordenDataTables = input.getOrder().get(0);
        Column columna = input.getColumns().get(ordenDataTables.getColumn());
        
        this.nombreColumna = mapCampo(columna.getData());
        this.order = ordenDataTables.getDir().equals("desc") ? "desc" : "asc";
    }
    
    @Override
    public int compare(RegistroVersionado o1, RegistroVersionado o2) {
        if(order.equals("asc")) {
            return compararValores(o1, o2);
        }
        else {
            return -1 * compararValores(o1, o2);
        }
    }
    
    private String mapCampo(String columna) {
        if(columna.equals("tipoCambio")) {
            return "idTipoCambio";
        }
        else return columna;
    }
    
    
    private <T extends Comparable<T>> int compararValores(RegistroVersionado o1, RegistroVersionado o2) {
        T valor1 = (T) o1.getValor(nombreColumna);
        T valor2 = (T) o2.getValor(nombreColumna);
        return valor1.compareTo(valor2);
    }
}
