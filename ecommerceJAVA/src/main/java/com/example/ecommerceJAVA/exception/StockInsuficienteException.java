// src/main/java/com/example/ecommerceJAVA/exception/StockInsuficienteException.java
package com.example.ecommerceJAVA.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // Esto le dice a Spring que devuelva un 400 Bad Request
public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(String message) {
        super(message);
    }

    public StockInsuficienteException(String message, Throwable cause) {
        super(message, cause);
    }
}