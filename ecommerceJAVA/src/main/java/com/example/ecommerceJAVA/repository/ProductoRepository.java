package com.example.ecommerceJAVA.repository;

import com.example.ecommerceJAVA.entity.Producto; // Asegúrate de que esta importación sea correcta
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interfaz de repositorio para la entidad Producto.
 * Spring Data JPA generará automáticamente la implementación de esta interfaz en tiempo de ejecución.
 *
 * Extiende JpaRepository<T, ID>, donde:
 * - T: Es el tipo de la entidad con la que trabajará este repositorio (Producto).
 * - ID: Es el tipo de la clave primaria de la entidad (Long para Producto).
 */
@Repository // Esta anotación es opcional en interfaces JpaRepository, pero es una buena práctica para indicar su rol.
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Este es un método de consulta derivado.
     * Spring Data JPA automáticamente construirá una consulta SQL para encontrar
     * todos los productos cuyo campo 'nombre' contenga la cadena proporcionada,
     * sin distinguir entre mayúsculas y minúsculas.
     *
     * Ejemplo de uso en el servicio:
     * List<Producto> productosEncontrados = productoRepository.findByNombreContainingIgnoreCase("monitor");
     *
     * @param nombre La cadena de texto a buscar dentro del nombre de los productos.
     * @return Una lista de objetos Producto que coinciden con el criterio de búsqueda.
     */
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Con solo extender JpaRepository, automáticamente obtienes los siguientes métodos (entre otros):
    // - save(Producto entity): Guarda o actualiza un producto.
    // - findById(Long id): Busca un producto por su ID, devolviendo un Optional.
    // - findAll(): Devuelve una lista de todos los productos.
    // - delete(Producto entity): Elimina un producto por su objeto.
    // - deleteById(Long id): Elimina un producto por su ID.
    // - existsById(Long id): Verifica si un producto existe por su ID.

    long countByIdCategoria(Long idCategoria);
}