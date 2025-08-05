// src/main/java/com/example/ecommerceJAVA/service/UsuarioService.java
package com.example.ecommerceJAVA.service;

import com.example.ecommerceJAVA.dto.UsuarioResponseDTO;
import com.example.ecommerceJAVA.entity.Usuario;
import com.example.ecommerceJAVA.exception.UserNotFoundException;
import com.example.ecommerceJAVA.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuario -> new UsuarioResponseDTO(
                        usuario.getId(),
                        usuario.getUsuario(),
                        usuario.getNombre(),
                        usuario.getApellido()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getUsuario(),
                usuario.getNombre(),
                usuario.getApellido()
        );
    }
}