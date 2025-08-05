// src/main/java/com/example/ecommerceJAVA/dto/ErrorResponseDTO.java
package com.example.ecommerceJAVA.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path; // La ruta de la petición que causó el error
}