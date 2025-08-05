// src/main/java/com/example/ecommerceJAVA/dto/UsuarioResponseDTO.java
package com.example.ecommerceJAVA.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String usuario;
    private String nombre;
    private String apellido;
}