// src/main/java/com/example/ecommerceJAVA/repository/CategoriaRepository.java
package com.example.ecommerceJAVA.repository;

import com.example.ecommerceJAVA.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    // Puedes añadir métodos de búsqueda personalizados si los necesitas, por ejemplo:
    Optional<Categoria> findByNombreIgnoreCase(String nombre);
    List<Categoria> findByNombreContainingIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
}