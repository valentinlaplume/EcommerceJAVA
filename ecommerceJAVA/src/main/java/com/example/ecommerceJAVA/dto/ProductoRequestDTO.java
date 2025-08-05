// src/main/java/com/example/ecommerceJAVA/dto/ProductoRequestDTO.java
package com.example.ecommerceJAVA.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// Elimina esta importación: import org.antlr.v4.runtime.misc.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequestDTO {

    // Elimina todas las anotaciones de validación como @NotBlank, @Size, @NotNull, @DecimalMin, @Min, @Positive
    private String nombre;
    private Double precio;
    private Integer stock;
    private String descripcion;
    private Long idCategoria;
}