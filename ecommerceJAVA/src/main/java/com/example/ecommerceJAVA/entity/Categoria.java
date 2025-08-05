// src/main/java/com/example/ecommerceJAVA/entity/Categoria.java
package com.example.ecommerceJAVA.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre; // Ej: "Bebida", "Tecnología", "Electrónica"
}