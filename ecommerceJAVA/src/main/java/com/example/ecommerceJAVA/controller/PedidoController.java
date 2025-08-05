// src/main/java/com/example/ecommerceJAVA/controller/PedidoController.java
package com.example.ecommerceJAVA.controller;

import com.example.ecommerceJAVA.dto.ErrorResponseDTO; // Importa tu nuevo DTO de error
import com.example.ecommerceJAVA.dto.PedidoRequestDTO;
import com.example.ecommerceJAVA.dto.PedidoResponseDTO;
import com.example.ecommerceJAVA.exception.StockInsuficienteException;
import com.example.ecommerceJAVA.service.PedidoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest; // Necesario para obtener la ruta

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://127.0.0.1:5501")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // ENDPOINTS

    @PostMapping("/pedidos")
    public ResponseEntity<?> crearPedido(@RequestBody PedidoRequestDTO pedidoRequestDTO, WebRequest request) {
        try {
            PedidoResponseDTO nuevoPedido = pedidoService.crearPedido(pedidoRequestDTO);
            return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED); // Retorna 201 Created con el pedido
        } catch (StockInsuficienteException e) {
            // Devuelve 400 Bad Request con un cuerpo JSON de ErrorResponseDTO
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request", // Tipo de error HTTP
                    e.getMessage(), // El mensaje de tu excepción de stock
                    request.getDescription(false).replace("uri=", "") // Obtiene la URI de la petición
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            // Captura otras RuntimeExceptions como "Usuario no encontrado" o "Producto no encontrado"
            // Devolver 404 para "no encontrado" y 500 para otros errores inesperados
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            String errorType = "Internal Server Error";
            if (e.getMessage().contains("Usuario no encontrado") || e.getMessage().contains("Producto no encontrado")) {
                status = HttpStatus.NOT_FOUND;
                errorType = "Not Found";
            }

            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    status.value(),
                    errorType,
                    e.getMessage(),
                    request.getDescription(false).replace("uri=", "")
            );
            return new ResponseEntity<>(errorResponse, status);
        }
    }

    @GetMapping("/usuarios/{idUsuario}/pedidos")
    public ResponseEntity<?> getHistorialPedidosUsuario(@PathVariable Long idUsuario, WebRequest request) {
        try {
            List<PedidoResponseDTO> pedidos = pedidoService.getHistorialPedidosUsuario(idUsuario);
            if (pedidos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Retorna 204 No Content si no hay pedidos
            }
            return new ResponseEntity<>(pedidos, HttpStatus.OK); // Retorna 200 OK con la lista de pedidos
        } catch (RuntimeException e) {
            // En caso de que el usuario no sea encontrado
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    e.getMessage(),
                    request.getDescription(false).replace("uri=", "")
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/pedidos/{id}")
    public ResponseEntity<?> getPedidoById(@PathVariable Long id, WebRequest request) {
        try {
            PedidoResponseDTO pedido = pedidoService.getPedidoById(id);
            return new ResponseEntity<>(pedido, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Manejar el caso de que el pedido no se encuentre
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    e.getMessage(),
                    request.getDescription(false).replace("uri=", "")
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }
}