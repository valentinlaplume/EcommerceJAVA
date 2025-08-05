// src/main/java/com/example/ecommerceJAVA/dto/PedidoDetalleRequestDTO.java
package com.example.ecommerceJAVA.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDetalleRequestDTO {
    private Long idProducto;
    private Integer cantidad;
}