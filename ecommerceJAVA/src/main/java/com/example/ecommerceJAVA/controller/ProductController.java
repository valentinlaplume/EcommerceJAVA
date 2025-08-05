// src/main/java/com/example/ecommerceJAVA/controller/ProductController.java
package com.example.ecommerceJAVA.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import com.example.ecommerceJAVA.dto.ProductoRequestDTO;
import com.example.ecommerceJAVA.dto.ProductoResponseDTO;
import com.example.ecommerceJAVA.dto.ErrorResponseDTO;
import com.example.ecommerceJAVA.exception.ProductNotFoundException;
import com.example.ecommerceJAVA.exception.CategoryNotFoundException;
import com.example.ecommerceJAVA.exception.ValidationException; // ¡Importa la nueva excepción!
import com.example.ecommerceJAVA.service.ProductService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map; // Para el cuerpo de error más detallado si quieres enviar la lista de errores

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://127.0.0.1:5501")
public class ProductController {

    private ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // ENDPOINTS

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductoRequestDTO productoRequestDTO, WebRequest request) { // SIN @Valid
        try {
            ProductoResponseDTO nuevoProducto = this.service.agregarProducto(productoRequestDTO);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(nuevoProducto);
        } catch (ValidationException e) { // Captura la excepción de validación manual
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Validation Error", // Tipo de error más específico
                    e.getMessage(), // Mensaje general de la excepción
                    request.getDescription(false).replace("uri=", "")
            );
            // Devuelve el ErrorResponseDTO junto con los detalles de los errores de validación
            return new ResponseEntity<>(Map.of("error", errorResponse, "details", e.getErrors()), HttpStatus.BAD_REQUEST);
        } catch (CategoryNotFoundException e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    request.getDescription(false).replace("uri=", "")
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) { // Captura otras RuntimeExceptions inesperadas
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "An unexpected error occurred during product creation: " + e.getMessage(),
                    request.getDescription(false).replace("uri=", "")
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ... otros métodos GET, DELETE quedan como estaban ...

    @PutMapping("/{id}")
    public ResponseEntity<?> editarProducto(@PathVariable Long id, @RequestBody ProductoRequestDTO productoRequestDTO, WebRequest request) { // SIN @Valid
        try {
            ProductoResponseDTO productoActualizado = this.service.editarProducto(id, productoRequestDTO);
            return new ResponseEntity<>(productoActualizado, HttpStatus.OK);
        } catch (ValidationException e) { // Captura la excepción de validación manual
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Validation Error",
                    e.getMessage(),
                    request.getDescription(false).replace("uri=", "")
            );
            return new ResponseEntity<>(Map.of("error", errorResponse, "details", e.getErrors()), HttpStatus.BAD_REQUEST);
        } catch (ProductNotFoundException e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    e.getMessage(),
                    request.getDescription(false).replace("uri=", "")
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (CategoryNotFoundException e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    request.getDescription(false).replace("uri=", "")
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "An unexpected error occurred during product update: " + e.getMessage(),
                    request.getDescription(false).replace("uri=", "")
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/list")
    public List<ProductoResponseDTO> obtenerListadoProductos() {
        return this.service.listarProductos();
    }

    @GetMapping("/find")
    public ResponseEntity<?> obtenerProductos(@RequestParam String nombreBusqueda, WebRequest request) {
        try {
            List<ProductoResponseDTO> encontrados = this.service.buscarProducto(nombreBusqueda);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(encontrados);
        } catch (ProductNotFoundException e) {
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

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProductoPorId(@PathVariable Long id, WebRequest request) {
        try {
            ProductoResponseDTO producto = this.service.buscarPorId(id);
            return new ResponseEntity<>(producto, HttpStatus.OK);
        } catch (ProductNotFoundException e) {
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarProducto(@PathVariable Long id) {
        try {
            this.service.eliminarProducto(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (ProductNotFoundException e) {
            throw e; // El @ResponseStatus en la excepción se encargará del 404
        }
    }
}