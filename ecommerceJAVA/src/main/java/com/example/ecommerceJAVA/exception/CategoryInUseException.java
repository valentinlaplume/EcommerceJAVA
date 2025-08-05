// src/main/java/com/example/ecommerceJAVA/exception/CategoryInUseException.java
package com.example.ecommerceJAVA.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // HTTP 409 Conflict
public class CategoryInUseException extends RuntimeException {

  public CategoryInUseException(String message) {
    super(message);
  }
}