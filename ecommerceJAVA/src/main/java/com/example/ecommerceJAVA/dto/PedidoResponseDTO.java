// src/main/java/com/example/ecommerceJAVA/dto/PedidoResponseDTO.java
package com.example.ecommerceJAVA.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {
    private Long id;
    private Long idUsuario;
    private List<PedidoDetalleResponseDTO> itemsPedido; // Lista de DTOs de detalles
    private LocalDateTime fechaCreacion;
    private Double total;
    private Long idEstado; // El ID del estado
    private PedidoEstadoResponseDTO estado; // El objeto DTO del estado completo
}