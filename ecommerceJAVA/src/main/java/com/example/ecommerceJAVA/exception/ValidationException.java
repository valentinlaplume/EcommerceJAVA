// src/main/java/com/example/ecommerceJAVA/exception/ValidationException.java
package com.example.ecommerceJAVA.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List; // Importar List

// Usamos HttpStatus.BAD_REQUEST (400) para errores de validación de entrada
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {

    private final List<String> errors;

    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}