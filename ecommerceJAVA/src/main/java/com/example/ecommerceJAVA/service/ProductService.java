// src/main/java/com/example/ecommerceJAVA/service/ProductService.java
package com.example.ecommerceJAVA.service;

import com.example.ecommerceJAVA.dto.CategoriaResponseDTO;
import com.example.ecommerceJAVA.dto.ProductoRequestDTO;
import com.example.ecommerceJAVA.dto.ProductoResponseDTO;
import com.example.ecommerceJAVA.entity.Categoria;
import com.example.ecommerceJAVA.entity.Producto;
import com.example.ecommerceJAVA.exception.CategoryNotFoundException;
import com.example.ecommerceJAVA.exception.ProductNotFoundException;
import com.example.ecommerceJAVA.exception.ValidationException; // Importa tu nueva excepción
import com.example.ecommerceJAVA.repository.CategoriaRepository;
import com.example.ecommerceJAVA.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList; // Necesario para List
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

  private final ProductoRepository productoRepository;
  private final CategoriaRepository categoriaRepository;

  public ProductService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
    this.productoRepository = productoRepository;
    this.categoriaRepository = categoriaRepository;
  }

  @Transactional
  public ProductoResponseDTO agregarProducto(ProductoRequestDTO productoDTO) {
    // --- VALIDACIONES MANUALES ---
    List<String> errors = new ArrayList<>();

    if (productoDTO.getNombre() == null || productoDTO.getNombre().trim().isEmpty()) {
      errors.add("El nombre del producto no puede estar vacío.");
    } else if (productoDTO.getNombre().length() > 255) {
      errors.add("El nombre del producto no puede exceder los 255 caracteres.");
    }

    if (productoDTO.getPrecio() == null) {
      errors.add("El precio del producto es obligatorio.");
    } else if (productoDTO.getPrecio() <= 0.0) {
      errors.add("El precio del producto debe ser mayor que cero.");
    }

    if (productoDTO.getStock() == null) {
      errors.add("El stock del producto es obligatorio.");
    } else if (productoDTO.getStock() < 0) {
      errors.add("El stock del producto no puede ser negativo.");
    }

    if (productoDTO.getDescripcion() != null && productoDTO.getDescripcion().length() > 1000) {
      errors.add("La descripción no puede exceder los 1000 caracteres.");
    }

    if (productoDTO.getIdCategoria() == null) {
      errors.add("La categoría del producto es obligatoria.");
    } else if (productoDTO.getIdCategoria() <= 0) {
      errors.add("El ID de la categoría debe ser un número positivo.");
    }

    // Si se encontraron errores, lanzar la excepción personalizada
    if (!errors.isEmpty()) {
      throw new ValidationException("Error en la validación de los campos del producto.", errors);
    }
    // --- FIN VALIDACIONES MANUALES ---

    Categoria categoria = categoriaRepository.findById(productoDTO.getIdCategoria())
            .orElseThrow(() -> new CategoryNotFoundException("Categoría no encontrada con ID: " + productoDTO.getIdCategoria()));

    Producto producto = new Producto();
    producto.setNombre(productoDTO.getNombre());
    producto.setPrecio(productoDTO.getPrecio());
    producto.setStock(productoDTO.getStock());
    producto.setDescripcion(productoDTO.getDescripcion());
    producto.setIdCategoria(productoDTO.getIdCategoria());
    producto.setCategoria(categoria);

    Producto productoGuardado = this.productoRepository.save(producto);

    return new ProductoResponseDTO(
            productoGuardado.getId(),
            productoGuardado.getNombre(),
            productoGuardado.getPrecio(),
            productoGuardado.getStock(),
            productoGuardado.getDescripcion(),
            productoGuardado.getIdCategoria(),
            new CategoriaResponseDTO(productoGuardado.getCategoria().getId(), productoGuardado.getCategoria().getNombre())
    );
  }

  @Transactional
  public ProductoResponseDTO editarProducto(Long id, ProductoRequestDTO productoDTO) {
    Producto productoAActualizar = this.productoRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado para editar con ID: " + id));

    // --- VALIDACIONES MANUALES ---
    List<String> errors = new ArrayList<>();

    if (productoDTO.getNombre() == null || productoDTO.getNombre().trim().isEmpty()) {
      errors.add("El nombre del producto no puede estar vacío.");
    } else if (productoDTO.getNombre().length() > 255) {
      errors.add("El nombre del producto no puede exceder los 255 caracteres.");
    }

    if (productoDTO.getPrecio() == null) {
      errors.add("El precio del producto es obligatorio.");
    } else if (productoDTO.getPrecio() <= 0.0) {
      errors.add("El precio del producto debe ser mayor que cero.");
    }

    if (productoDTO.getStock() == null) {
      errors.add("El stock del producto es obligatorio.");
    } else if (productoDTO.getStock() < 0) {
      errors.add("El stock del producto no puede ser negativo.");
    }

    if (productoDTO.getDescripcion() != null && productoDTO.getDescripcion().length() > 1000) {
      errors.add("La descripción no puede exceder los 1000 caracteres.");
    }

    if (productoDTO.getIdCategoria() == null) {
      errors.add("La categoría del producto es obligatoria.");
    } else if (productoDTO.getIdCategoria() <= 0) {
      errors.add("El ID de la categoría debe ser un número positivo.");
    }

    if (!errors.isEmpty()) {
      throw new ValidationException("Error en la validación de los campos del producto.", errors);
    }
    // --- FIN VALIDACIONES MANUALES ---

    productoAActualizar.setNombre(productoDTO.getNombre());
    productoAActualizar.setPrecio(productoDTO.getPrecio());
    productoAActualizar.setStock(productoDTO.getStock());
    productoAActualizar.setDescripcion(productoDTO.getDescripcion());

    if (productoDTO.getIdCategoria() != null && !productoDTO.getIdCategoria().equals(productoAActualizar.getIdCategoria())) {
      Categoria nuevaCategoria = categoriaRepository.findById(productoDTO.getIdCategoria())
              .orElseThrow(() -> new CategoryNotFoundException("Nueva categoría no encontrada con ID: " + productoDTO.getIdCategoria()));
      productoAActualizar.setIdCategoria(productoDTO.getIdCategoria());
      productoAActualizar.setCategoria(nuevaCategoria);
    }

    Producto productoActualizado = this.productoRepository.save(productoAActualizar);

    return new ProductoResponseDTO(
            productoActualizado.getId(),
            productoActualizado.getNombre(),
            productoActualizado.getPrecio(),
            productoActualizado.getStock(),
            productoActualizado.getDescripcion(),
            productoActualizado.getIdCategoria(),
            new CategoriaResponseDTO(productoActualizado.getCategoria().getId(), productoActualizado.getCategoria().getNombre())
    );
  }

  // --- MÉTODOS EXISTENTES SIN CAMBIOS RELEVANTES DE VALIDACIÓN ---
  @Transactional(readOnly = true)
  public List<ProductoResponseDTO> listarProductos() {
    List<Producto> productos = this.productoRepository.findAll();
    return productos.stream()
            .map(producto -> new ProductoResponseDTO(
                    producto.getId(),
                    producto.getNombre(),
                    producto.getPrecio(),
                    producto.getStock(),
                    producto.getDescripcion(),
                    producto.getIdCategoria(),
                    new CategoriaResponseDTO(producto.getCategoria().getId(), producto.getCategoria().getNombre())
            ))
            .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<ProductoResponseDTO> buscarProducto(String busqueda) {
    List<Producto> encontrados = this.productoRepository.findByNombreContainingIgnoreCase(busqueda);
    if (encontrados.isEmpty()) {
      return List.of();
    }
    return encontrados.stream()
            .map(producto -> new ProductoResponseDTO(
                    producto.getId(),
                    producto.getNombre(),
                    producto.getPrecio(),
                    producto.getStock(),
                    producto.getDescripcion(),
                    producto.getIdCategoria(),
                    new CategoriaResponseDTO(producto.getCategoria().getId(), producto.getCategoria().getNombre())
            ))
            .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public ProductoResponseDTO buscarPorId(Long id) {
    Producto producto = this.productoRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado con ID: " + id));
    return new ProductoResponseDTO(
            producto.getId(),
            producto.getNombre(),
            producto.getPrecio(),
            producto.getStock(),
            producto.getDescripcion(),
            producto.getIdCategoria(),
            new CategoriaResponseDTO(producto.getCategoria().getId(), producto.getCategoria().getNombre())
    );
  }

  @Transactional
  public void eliminarProducto(Long id) {
    if (!this.productoRepository.existsById(id)) {
      throw new ProductNotFoundException("Producto no encontrado para eliminar con ID: " + id);
    }
    this.productoRepository.deleteById(id);
  }
}