package com.focusservices.library.versionamiento;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Function;
import com.focusservices.library.commons.datatables.mapping.Column;
import com.focusservices.library.commons.datatables.mapping.DataTablesInput;
import com.focusservices.library.commons.datatables.mapping.Order;

public class OrdenamientoNativo {
    private Class<?> clase;
    private String columnaTarget;
    private String nombreColumna;
    private String orden;
    private boolean isColumnaEntidad;
    private Function<String, Optional<String>> camposEspeciales = campo -> Optional.empty();
    
    private static final String SQL_FRAGMENTO_JSON = "${FUNCION}(JSON_VALUE(${COLUMNA_TARGET}, '$.${COLUMNA}'))";

    public OrdenamientoNativo(String columnaTarget, Class<?> clase, DataTablesInput input) {
        this.columnaTarget = columnaTarget;
        this.clase = clase;
        extraerInformacionInput(input);
    }

    public OrdenamientoNativo(String columnaTarget, Class<?> clase, DataTablesInput input, Function<String, Optional<String>> camposEspeciales) {
        this(columnaTarget, clase, input);
        if(camposEspeciales != null) {
            this.camposEspeciales = camposEspeciales;
        }
    }
    
    private void extraerInformacionInput(DataTablesInput input) {
        Order ordenDataTables = input.getOrder().get(0);
        Column columna = input.getColumns().get(ordenDataTables.getColumn());
        
        this.nombreColumna = columna.getData();
        this.isColumnaEntidad = VersionamientoUtils.isColumnaDeEntidad(nombreColumna);
        this.orden = ordenDataTables.getDir().equals("desc") ? "desc" : "asc";
    }
    
    public String getOrdenamientoResultante() {
        if(!isColumnaEntidad) {
            Optional<String> campoEspecial = camposEspeciales.apply(nombreColumna);
            if(campoEspecial.isPresent()) {
                return crearOrderByExpresion(campoEspecial.get());
            }
            else {
                return crearOrdenamientoColumnaLocal();
            }
        }
        else return "";
    }

    private String crearOrdenamientoColumnaLocal() {
        String expresionOrderByFinal = SQL_FRAGMENTO_JSON
                .replace("${FUNCION}", getFuncionTransformacion())
                .replace("${COLUMNA_TARGET}", columnaTarget)
                .replace("${COLUMNA}", nombreColumna);
        
        return crearOrderByExpresion(expresionOrderByFinal);
    }
    
    private String crearOrderByExpresion(String expresion) {
        return " order by " + expresion + " " + orden;
    }

    private String getFuncionTransformacion() {
        try {
            Field campo = clase.getDeclaredField(nombreColumna);
            Class<?> claseCampo = campo.getType();
            return getFuncionTransformacionDeClase(claseCampo);
        } catch (Exception ex) {
            return getFuncionTransformacionDeClase(String.class);
        }
    }
    
    private String getFuncionTransformacionDeClase(Class<?> claseCampo) {
        if(isNumero(claseCampo)) {
            return "TO_NUMBER";
        }
        else return "TO_CHAR";
    }

    private static boolean isNumero(Class<?> claseCampo) {
        return claseCampo.equals(Integer.class) || claseCampo.equals(Long.class) || claseCampo.equals(BigDecimal.class);
    }
}
