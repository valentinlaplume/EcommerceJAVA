// src/main/java/com/example/ecommerceJAVA/repository/PedidoEstadoRepository.java
package com.example.ecommerceJAVA.repository;

import com.example.ecommerceJAVA.entity.PedidoEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Importar Optional

@Repository
public interface PedidoEstadoRepository extends JpaRepository<PedidoEstado, Long> {
    // Necesitamos este método para buscar el estado "PENDIENTE" por su nombre
    Optional<PedidoEstado> findByNombre(String nombre);
}