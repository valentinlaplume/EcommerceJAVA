package com.example.ecommerceJAVA.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Si vas a usar Normalizer en contieneNombre, asegúrate de tener esta importación
// import java.text.Normalizer;

@Entity
@Table(name = "productos")
@Data // Anotación de Lombok para getters, setters, etc.
@NoArgsConstructor // Constructor sin argumentos (para JPA)
@AllArgsConstructor // Constructor con todos los argumentos (útil para pruebas)
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Double precio;
    private Integer stock;
    private String descripcion; // Campo 'description' renombrado a 'descripcion'

    // Relación Many-to-One con la entidad Categoria
    // JPA creará una columna 'id_categoria' en la tabla 'productos'
    // que almacenará el ID de la categoría asociada.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    // El 'id' de la categoría será mapeado a esta variable para operaciones de lectura.
    private Categoria categoria;

    // Este campo es el que pediste: el ID de la categoría directamente.
    // Lo marcamos como columna y hacemos que sea actualizable/insertable por JPA.
    // JPA sabe que este campo 'idCategoria' se corresponde con la FK 'categoria_id'
    // en la base de datos gracias a la configuración de @JoinColumn de 'categoria'.
    @Column(name = "id_categoria", nullable = false)
    private Long idCategoria; // Aquí almacenamos el ID numérico de la categoría

    // --- CONSTRUCTORES ---

    // Constructor para crear un Producto sin ID (cuando es nuevo)
    // Ahora incluye 'descripcion' y el 'idCategoria'
    // Nota: Si usas DTOs de creación, este constructor podría ser diferente.
    // Para simplificar, asumimos que este constructor es para inicialización interna o pruebas.
    public Producto(String nombre, Double precio, Integer stock, String descripcion, Long idCategoria) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.descripcion = descripcion;
        this.idCategoria = idCategoria;
    }

    // --- MÉTODOS DE NEGOCIO Y AYUDA ---

    // Setter para el precio con validación (se mantiene igual)
    public void setPrecio(Double precio) {
        if (precio == null) {
            throw new IllegalArgumentException("El precio no puede ser nulo.");
        }
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        this.precio = precio;
    }

    // Método para actualizar el precio (se mantiene igual)
    public void actualizarPrecio(double nuevoPrecio) {
        this.setPrecio(nuevoPrecio);
    }

    // Método para verificar si el nombre contiene una cadena de búsqueda (se mantiene igual)
    public boolean contieneNombre(String busqueda) {
        if (this.nombre == null) return false;
        // Para manejar acentos de forma más robusta:
        // Normalizer.normalize(this.nombre, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase().contains(busqueda.toLowerCase());
        return this.nombre.toLowerCase().contains(busqueda.toLowerCase());
    }
}