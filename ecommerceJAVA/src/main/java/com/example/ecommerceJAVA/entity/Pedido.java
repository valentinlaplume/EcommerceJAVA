// src/main/java/com/example/ecommerceJAVA/entity/Pedido.java
package com.example.ecommerceJAVA.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación ManyToOne con Usuario: Un pedido es realizado por un Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false) // Columna FK en la tabla 'pedidos'
    private Usuario usuario; // Referencia al usuario que hizo el pedido

    // Relación OneToMany con PedidoDetalle: Un pedido puede tener muchos detalles de pedido
    // 'mappedBy = "pedido"' indica que 'pedido' es el campo en PedidoDetalle que mapea esta relación.
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PedidoDetalle> pedidosDetalle = new ArrayList<>(); // ¡Lista de PedidoDetalle!

    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false) // Columna FK en la tabla 'pedidos' que apunta al ID del estado en 'pedidos_estados'
    private PedidoEstado estado; // Ahora es una referencia a la ENTIDAD PedidoEstado


    // Constructor sin ID (útil al crear nuevos pedidos)
    public Pedido(Usuario usuario, List<PedidoDetalle> pedidosDetalle, PedidoEstado estado) {
        this.usuario = usuario;
        this.pedidosDetalle = pedidosDetalle;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = estado;
        // Asegurar bidireccionalidad para las líneas de pedido existentes
        if (pedidosDetalle != null) {
            pedidosDetalle.forEach(detalle -> detalle.setPedido(this));
        }
    }

    // Método para calcular el total del pedido
    public Double getTotal() {
        return this.pedidosDetalle.stream()
                .mapToDouble(PedidoDetalle::getSubtotal) // Suma los subtotales de cada detalle
                .sum();
    }

    // Métodos de conveniencia para manejar la lista de PedidoDetalle
    public void addPedidoDetalle(PedidoDetalle pedidoDetalle) {
        this.pedidosDetalle.add(pedidoDetalle);
        pedidoDetalle.setPedido(this); // Establece la relación inversa
    }

    public void removePedidoDetalle(PedidoDetalle pedidoDetalle) {
        this.pedidosDetalle.remove(pedidoDetalle);
        pedidoDetalle.setPedido(null); // Rompe la relación inversa
    }
}