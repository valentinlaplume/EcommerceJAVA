// src/main/java/com/example/ecommerceJAVA/repository/PedidoDetalleRepository.java
package com.example.ecommerceJAVA.repository;

import com.example.ecommerceJAVA.entity.PedidoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, Long> {
    // Puedes añadir métodos personalizados si los necesitas, por ejemplo,
    // buscar detalles por ID de producto o ID de pedido.
    // List<PedidoDetalle> findByPedido_Id(Long idPedido);
    // List<PedidoDetalle> findByProducto_Id(Long idProducto);
}