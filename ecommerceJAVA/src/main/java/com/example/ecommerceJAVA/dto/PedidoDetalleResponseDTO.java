// src/main/java/com/example/ecommerceJAVA/dto/PedidoDetalleResponseDTO.java
package com.example.ecommerceJAVA.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDetalleResponseDTO {
    private Long id; // ID del detalle de pedido
    private Long idProducto; // ID del producto
    private String nombreProducto; // Nombre del producto para mostrar en el frontend
    private Integer cantidad;
    private Double precioUnitario; // Precio al momento de la compra
}