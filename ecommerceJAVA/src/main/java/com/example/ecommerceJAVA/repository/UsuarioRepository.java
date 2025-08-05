// src/main/java/com/example/ecommerceJAVA/repository/UsuarioRepository.java
package com.example.ecommerceJAVA.repository;

import com.example.ecommerceJAVA.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Importar Optional para findById

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Puedes añadir métodos de búsqueda personalizados si los necesitas, por ejemplo:
    Optional<Usuario> findByMail(String mail); // <-- Cambiado de findByEmail a findByMail
    boolean existsByMail(String mail);
}