// src/main/java/com/example/ecommerceJAVA/dto/PedidoRequestDTO.java
package com.example.ecommerceJAVA.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequestDTO {
    private Long idUsuario;
    private List<PedidoDetalleRequestDTO> itemsPedido;
}