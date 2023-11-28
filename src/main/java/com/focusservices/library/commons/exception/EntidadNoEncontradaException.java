package com.focusservices.library.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntidadNoEncontradaException extends RuntimeException {
	
    public EntidadNoEncontradaException() {
    }

    public EntidadNoEncontradaException(String message) {
        super(message);
    }

    public EntidadNoEncontradaException(String message, Throwable cause) {
        super(message, cause);
    }
}
