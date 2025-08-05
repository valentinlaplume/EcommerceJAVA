// src/main/java/com/example/ecommerceJAVA/service/PedidoService.java
package com.example.ecommerceJAVA.service;

import com.example.ecommerceJAVA.dto.PedidoDetalleRequestDTO;
import com.example.ecommerceJAVA.dto.PedidoDetalleResponseDTO;
import com.example.ecommerceJAVA.dto.PedidoEstadoResponseDTO;
import com.example.ecommerceJAVA.dto.PedidoRequestDTO;
import com.example.ecommerceJAVA.dto.PedidoResponseDTO;
import com.example.ecommerceJAVA.entity.Pedido;
import com.example.ecommerceJAVA.entity.PedidoDetalle;
import com.example.ecommerceJAVA.entity.PedidoEstado;
import com.example.ecommerceJAVA.entity.Producto;
import com.example.ecommerceJAVA.entity.Usuario;
import com.example.ecommerceJAVA.exception.StockInsuficienteException;
import com.example.ecommerceJAVA.repository.PedidoRepository;
import com.example.ecommerceJAVA.repository.PedidoDetalleRepository; // Necesario si manejas PedidoDetalle por separado, aunque CascadeType.ALL lo maneja en Pedido
import com.example.ecommerceJAVA.repository.ProductoRepository;
import com.example.ecommerceJAVA.repository.UsuarioRepository;
import com.example.ecommerceJAVA.repository.PedidoEstadoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar para manejar transacciones

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoEstadoRepository pedidoEstadoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ProductoRepository productoRepository,
                         UsuarioRepository usuarioRepository, PedidoEstadoRepository pedidoEstadoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedidoEstadoRepository = pedidoEstadoRepository;
    }

    /**
     * Confirma un pedido, valida stock, lo descuenta y genera el registro en estado "PENDIENTE".
     *
     * @param pedidoRequestDTO DTO con la información del pedido a crear.
     * @return PedidoResponseDTO con el detalle del pedido confirmado.
     * @throws StockInsuficienteException Si el stock de algún producto es insuficiente.
     * @throws RuntimeException Si el usuario o el estado "PENDIENTE" no se encuentran.
     */
    @Transactional // Asegura que toda la operación sea atómica (todo o nada)
    public PedidoResponseDTO crearPedido(PedidoRequestDTO pedidoRequestDTO) {
        // 1. Validar y obtener el usuario
        Usuario usuario = usuarioRepository.findById(pedidoRequestDTO.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + pedidoRequestDTO.getIdUsuario()));

        // 2. Obtener el estado "PENDIENTE"
        PedidoEstado estadoPendiente = pedidoEstadoRepository.findByNombre("PENDIENTE")
                .orElseThrow(() -> new RuntimeException("Estado 'PENDIENTE' no encontrado. Asegúrate de que exista en la DB."));

        // 3. Crear una nueva entidad Pedido
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setUsuario(usuario);
        nuevoPedido.setFechaCreacion(LocalDateTime.now());
        nuevoPedido.setEstado(estadoPendiente); // Establecer el estado inicial como "PENDIENTE"

        List<PedidoDetalle> detallesPedido = new ArrayList<PedidoDetalle>();

        // 4. Procesar cada ítem del pedido: validar stock y crear PedidoDetalle
        for (PedidoDetalleRequestDTO item : pedidoRequestDTO.getItemsPedido()) {
            Producto producto = productoRepository.findById(item.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + item.getIdProducto()));

            if (producto.getStock() < item.getCantidad()) {
                throw new StockInsuficienteException("Stock insuficiente para el producto: " + producto.getNombre() + ". Stock disponible: " + producto.getStock() + ", solicitado: " + item.getCantidad());
            }

            // Descontar stock
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto); // Guardar el producto con el stock actualizado

            // Crear PedidoDetalle
            PedidoDetalle detalle = new PedidoDetalle(producto, item.getCantidad(), producto.getPrecio());
            nuevoPedido.addPedidoDetalle(detalle); // Añadir el detalle al pedido (maneja la bidireccionalidad)
            detallesPedido.add(detalle);
        }

        // 5. Guardar el Pedido (esto también guarda los PedidoDetalle por CascadeType.ALL)
        Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido);

        // 6. Mapear y devolver el PedidoResponseDTO
        return mapToPedidoResponseDTO(pedidoGuardado);
    }

    /**
     * Obtiene el historial de pedidos de un usuario específico.
     *
     * @param idUsuario ID del usuario.
     * @return Lista de PedidoResponseDTOs.
     * @throws RuntimeException Si el usuario no es encontrado.
     */
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> getHistorialPedidosUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

        List<Pedido> pedidos = pedidoRepository.findByUsuario(usuario);

        return pedidos.stream()
                .map(this::mapToPedidoResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Método auxiliar para mapear una entidad Pedido a un PedidoResponseDTO.
     * Este método es privado porque solo se usa dentro del servicio.
     */
    private PedidoResponseDTO mapToPedidoResponseDTO(Pedido pedido) {
        // Mapear los detalles del pedido a PedidoDetalleResponseDTOs
        List<PedidoDetalleResponseDTO> detalleDTOs = pedido.getPedidosDetalle().stream()
                .map(detalle -> new PedidoDetalleResponseDTO(
                        detalle.getId(),
                        detalle.getProducto().getId(), // Solo el ID del producto
                        detalle.getProducto().getNombre(), // Nombre del producto para el frontend
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario()
                ))
                .collect(Collectors.toList());

        // Mapear el estado del pedido a PedidoEstadoResponseDTO
        PedidoEstadoResponseDTO estadoDTO = new PedidoEstadoResponseDTO(
                pedido.getEstado().getId(),
                pedido.getEstado().getNombre()
        );

        // Construir y devolver el PedidoResponseDTO
        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getUsuario().getId(),
                detalleDTOs,
                pedido.getFechaCreacion(),
                pedido.getTotal(),
                pedido.getEstado().getId(),
                estadoDTO
        );
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO getPedidoById(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + idPedido));
        return mapToPedidoResponseDTO(pedido);
    }
}