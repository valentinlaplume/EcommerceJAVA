// src/main/java/com/example/ecommerceJAVA/controller/CategoriaController.java
package com.example.ecommerceJAVA.controller;

import com.example.ecommerceJAVA.dto.CategoriaRequestDTO;
import com.example.ecommerceJAVA.dto.CategoriaResponseDTO;
import com.example.ecommerceJAVA.dto.ErrorResponseDTO;
import com.example.ecommerceJAVA.exception.CategoryInUseException;
import com.example.ecommerceJAVA.exception.CategoryNotFoundException;
import com.example.ecommerceJAVA.exception.ValidationException;
import com.example.ecommerceJAVA.service.CategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories") // Prefijo de URL para categorías
@CrossOrigin(origins = "http://127.0.0.1:5501")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<?> crearCategoria(@RequestBody CategoriaRequestDTO categoriaRequestDTO, WebRequest request) {
        try {
            CategoriaResponseDTO nuevaCategoria = categoriaService.crearCategoria(categoriaRequestDTO);
            return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED);
        } catch (ValidationException e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Validation Error",
                    e.getMessage(),
                    request.getDescription(false).replace("uri=", "")
            );
            return new ResponseEntity<>(Map.of("error", errorResponse, "details", e.getErrors()), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "An unexpected error occurred during category creation: " + e.getMessage(),
                    request.getDescription(false).replace("uri=", "")
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias() {
        List<CategoriaResponseDTO> categorias = categoriaService.listarCategorias();
        if (categorias.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content si no hay categorías
        }
        return new ResponseEntity<>(categorias, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarCategoriaPorId(@PathVariable Long id, WebRequest request) {
        try {
            CategoriaResponseDTO categoria = categoriaService.buscarCategoriaPorId(id);
            return new ResponseEntity<>(categoria, HttpStatus.OK);
        } catch (CategoryNotFoundException e) {
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

    @GetMapping("/find") // Ejemplo: GET /api/categories/find?nombre=electronicos
    public ResponseEntity<List<CategoriaResponseDTO>> buscarCategoriasPorNombre(@RequestParam String nombre, WebRequest request) {
        List<CategoriaResponseDTO> categorias = categoriaService.buscarCategoriasPorNombre(nombre);
        if (categorias.isEmpty()) {
            // Para búsquedas, 200 OK con lista vacía es estándar, no 404
            return new ResponseEntity<>(HttpStatus.OK); // O HttpStatus.NO_CONTENT si quieres
        }
        return new ResponseEntity<>(categorias, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editarCategoria(@PathVariable Long id, @RequestBody CategoriaRequestDTO categoriaRequestDTO, WebRequest request) {
        try {
            CategoriaResponseDTO categoriaActualizada = categoriaService.editarCategoria(id, categoriaRequestDTO);
            return new ResponseEntity<>(categoriaActualizada, HttpStatus.OK);
        } catch (ValidationException e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Validation Error",
                    e.getMessage(),
                    request.getDescription(false).replace("uri=", "")
            );
            return new ResponseEntity<>(Map.of("error", errorResponse, "details", e.getErrors()), HttpStatus.BAD_REQUEST);
        } catch (CategoryNotFoundException e) {
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
                    "An unexpected error occurred during category update: " + e.getMessage(),
                    request.getDescription(false).replace("uri=", "")
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id, WebRequest request) { // Añade WebRequest aquí
        try {
            categoriaService.eliminarCategoria(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } catch (CategoryNotFoundException e) {
            // Manejo explícito de 404 para un ErrorResponseDTO consistente
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    e.getMessage(),
                    request.getDescription(false).replace("uri=", "")
            );
            return new ResponseEntity(errorResponse, HttpStatus.NOT_FOUND); // Devuelve el cuerpo del error
        } catch (CategoryInUseException e) { // ¡Nuevo catch para categorías en uso!
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.CONFLICT.value(), // 409 Conflict
                    "Conflict",
                    e.getMessage(),
                    request.getDescription(false).replace("uri=", "")
            );
            return new ResponseEntity(errorResponse, HttpStatus.CONFLICT); // Devuelve el cuerpo del error
        } catch (RuntimeException e) { // Captura cualquier otra RuntimeException inesperada
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "An unexpected error occurred during category deletion: " + e.getMessage(),
                    request.getDescription(false).replace("uri=", "")
            );
            return new ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR); // Devuelve el cuerpo del error
        }
    }
}