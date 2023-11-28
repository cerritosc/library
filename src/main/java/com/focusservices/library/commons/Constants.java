package com.focusservices.library.commons;

public class Constants {
    public static final String REDIRECT_FORM = "redirect:form";
    public static final String SERVICE_RESPONSE_NAME = "serviceResponse";
    public static final String MSG_EXCEPCION_ACCION = "Ocurri\u00f3 una excepci\u00f3n al realizar la acci\u00f3n";
    public static final String MSG_GUARDADO_EXITOSO = "¡Guardado con \u00e9xito!";
    public static final String MSG_NO_ENCONTRADO = "¡Registro No Encontrado!";
    public static final String MSG_EXCEPCION_ELIMINAR = "¡Existen Registros Asociados No Es Posible Eliminar!";
    public static final String MSG_BINDING_ERROR= "Error al guardar el registro, causas: ";
    public static final String MSG_ELIMINADO_EXITOSO = "¡Eliminado con \u00e9xito!";
    public static final String APPLICATION_JSON = "application/json;charset=UTF-8";
    public static final String APPLICATION_VND_MS_EXCEL = "application/vnd.ms-excel";
    public static final String APPLICATION_VND_WORD_DOCUMENT = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String APPLICATION_PDF = "application/pdf";
    public static final String EXTENSION_XLS = "xls";
    public static final String EXTENSION_PDF = "pdf";
    public static final String EXTENSION_DOCX = "docx";
    public static final String PREFIX_CLASSPATH = "classpath:";
    public static final String MSG_INACTIVADO_EXITOSO = "¡Inactivado con \u00e9xito!";
    public static final String MSG_ACTIVADO_EXITOSO = "¡Activado con \u00e9xito!";
    public static final String MSG_EDITADO_EXITOSO = "¡Editado con \u00e9xito!";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

    private Constants() {
        throw new IllegalStateException("Constants class must not be instantiated!");
    }
}
