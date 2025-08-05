// src/main/java/com/example/ecommerceJAVA/controller/UsuarioController.java
package com.example.ecommerceJAVA.controller;

import com.example.ecommerceJAVA.dto.ErrorResponseDTO;
import com.example.ecommerceJAVA.dto.UsuarioResponseDTO;
import com.example.ecommerceJAVA.exception.UserNotFoundException;
import com.example.ecommerceJAVA.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users") // O /api/usuarios, como prefieras
@CrossOrigin(origins = "http://127.0.0.1:5501")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarUsuarios();
        if (usuarios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/{id}") // Nuevo endpoint para buscar por ID
    public ResponseEntity<?> buscarUsuarioPorId(@PathVariable Long id, WebRequest request) {
        try {
            UsuarioResponseDTO usuario = usuarioService.buscarUsuarioPorId(id);
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    e.getMessage(),
                    request.getDescription(false).replace("uri=", "")
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "An unexpected error occurred: " + e.getMessage(),
                    request.getDescription(false).replace("uri=", "")
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}