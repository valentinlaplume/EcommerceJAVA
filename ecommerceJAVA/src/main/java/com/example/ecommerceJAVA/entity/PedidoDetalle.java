// src/main/java/com/example/ecommerceJAVA/entity/PedidoDetalle.java
package com.example.ecommerceJAVA.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pedidos_detalle") // El nombre de la tabla también se actualiza
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación ManyToOne con Pedido: Varias PedidoDetalle pertenecen a un solo Pedido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false) // Columna FK en la tabla pedidos_detalle
    private Pedido pedido; // Referencia al pedido al que pertenece esta línea

    // Relación ManyToOne con Producto: Un PedidoDetalle se refiere a un Producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false) // Columna FK en la tabla pedidos_detalle
    private Producto producto; // Referencia al producto comprado

    private Integer cantidad; // Cantidad de este producto en el pedido

    @Column(nullable = false)
    private Double precioUnitario; // Precio del producto en el momento de la compra (snapshot)

    // Constructor sin ID (útil al crear nuevas líneas de detalle de pedido)
    public PedidoDetalle(Producto producto, Integer cantidad, Double precioUnitario) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // Método para calcular el subtotal de esta línea de pedido
    public Double getSubtotal() {
        return this.cantidad * this.precioUnitario;
    }
}