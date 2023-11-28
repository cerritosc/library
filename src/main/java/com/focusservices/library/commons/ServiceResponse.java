package com.focusservices.library.commons;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Wrapper para manejar respuestas entre capa Controller y Service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean success;
    private String message;
    private Serializable data;

    public ServiceResponse(boolean success, String message){
        this.success = success;
        this.message = message;
    }
}
