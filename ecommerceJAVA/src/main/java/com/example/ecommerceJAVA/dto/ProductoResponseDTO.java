// src/main/java/com/example/ecommerceJAVA/dto/ProductoResponseDTO.java
package com.example.ecommerceJAVA.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
// Tenes que regenerar este constructor con el nuevo campo CategoriaResponseDTO
// o si usas @AllArgsConstructor, Lombok lo hará por vos.
@AllArgsConstructor
public class ProductoResponseDTO {
    private Long id;
    private String nombre;
    private Double precio;
    private Integer stock;
    private String descripcion;
    private Long idCategoria; // Seguimos manteniendo el ID explícito si lo necesitas.

    // ¡NUEVO CAMPO para el objeto Categoria en la respuesta!
    private CategoriaResponseDTO categoria;
}