// src/main/java/com/example/ecommerceJAVA/dto/CategoriaRequestDTO.java
package com.example.ecommerceJAVA.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// No usamos anotaciones de validación aquí, ya que decidimos hacerlo manual en el servicio.
//
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaRequestDTO {
    private String nombre;
}