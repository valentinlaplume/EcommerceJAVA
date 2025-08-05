// src/main/java/com/example/ecommerceJAVA/repository/PedidoRepository.java
package com.example.ecommerceJAVA.repository;

import com.example.ecommerceJAVA.entity.Pedido;
import com.example.ecommerceJAVA.entity.Usuario; // Necesitamos importar Usuario para buscar por él
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Método para encontrar pedidos por un usuario específico
    // Spring Data JPA puede generar esto automáticamente si sigues las convenciones de nombres
    List<Pedido> findByUsuario(Usuario usuario);

    // O si prefieres buscar por el ID del usuario directamente (aunque 'findByUsuario' es más JPA-idiomático)
    // List<Pedido> findByUsuario_Id(Long idUsuario);
}