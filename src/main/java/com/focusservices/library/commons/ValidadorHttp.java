package com.focusservices.library.commons;

import java.io.IOException;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

public class ValidadorHttp {
    
    private ValidadorHttp() {
        
    }
    
    public static boolean isPeticionCorrectaExcluyendoCampos(BindingResult resultado, String... camposExcluir) {
        int conteoErrores = resultado.getFieldErrorCount();
        int conteoTotal = 0;
        for(String campo: camposExcluir) {
            conteoTotal += resultado.getFieldErrorCount(campo);
        }
        
        return conteoErrores == 0 || (conteoErrores == conteoTotal);
    }
    
    public static byte[] getBytes(MultipartFile archivo) {
        try {
            return archivo.getBytes();
        } catch (IOException ex) {
            throw new IllegalArgumentException("Ocurrio un error al extraer el archivo " + archivo.getName(), ex);
        }
    }
}
