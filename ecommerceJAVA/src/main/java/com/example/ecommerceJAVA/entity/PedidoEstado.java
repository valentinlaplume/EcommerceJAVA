// src/main/java/com/example/ecommerceJAVA/entity/PedidoEstado.java
package com.example.ecommerceJAVA.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pedidos_estados") // Define explícitamente el nombre de la tabla en snake_case
@Data // Anotación de Lombok que genera @Getter, @Setter, @EqualsAndHashCode, @ToString
@NoArgsConstructor // Lombok: Genera el constructor sin argumentos (necesario para JPA)
@AllArgsConstructor // Lombok: Genera un constructor con todos los campos
public class PedidoEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID único para el estado del pedido

    @Column(nullable = false, unique = true)
    private String nombre; // Nombre del estado (ej. "PENDIENTE", "ENVIADO", "ENTREGADO")

    // Constructor para crear un PedidoEstado sin ID (cuando es nuevo)
    public PedidoEstado(String nombre) {
        this.nombre = nombre;
    }
}