// src/main/java/com/example/ecommerceJAVA/service/CategoriaService.java
package com.example.ecommerceJAVA.service;

import com.example.ecommerceJAVA.dto.CategoriaRequestDTO;
import com.example.ecommerceJAVA.dto.CategoriaResponseDTO;
import com.example.ecommerceJAVA.entity.Categoria;
import com.example.ecommerceJAVA.exception.CategoryInUseException;
import com.example.ecommerceJAVA.exception.CategoryNotFoundException;
import com.example.ecommerceJAVA.exception.ValidationException; // Para validación manual
import com.example.ecommerceJAVA.repository.CategoriaRepository;
import com.example.ecommerceJAVA.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    // --- MÉTODOS CRUD ---

    @Transactional
    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO categoriaDTO) {
        List<String> errors = new ArrayList<>();

        // Validaciones manuales
        if (categoriaDTO.getNombre() == null || categoriaDTO.getNombre().trim().isEmpty()) {
            errors.add("El nombre de la categoría no puede estar vacío.");
        } else if (categoriaDTO.getNombre().length() > 255) {
            errors.add("El nombre de la categoría no puede exceder los 255 caracteres.");
        } else if (categoriaRepository.existsByNombreIgnoreCase(categoriaDTO.getNombre().trim())) {
            errors.add("Ya existe una categoría con el nombre '" + categoriaDTO.getNombre().trim() + "'.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Error en la validación de los campos de la categoría.", errors);
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(categoriaDTO.getNombre().trim()); // Elimina espacios en blanco

        Categoria categoriaGuardada = categoriaRepository.save(categoria);
        return new CategoriaResponseDTO(categoriaGuardada.getId(), categoriaGuardada.getNombre());
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarCategorias() {
        return categoriaRepository.findAll().stream()
                .map(categoria -> new CategoriaResponseDTO(categoria.getId(), categoria.getNombre()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO buscarCategoriaPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Categoría no encontrada con ID: " + id));
        return new CategoriaResponseDTO(categoria.getId(), categoria.getNombre());
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> buscarCategoriasPorNombre(String nombreBusqueda) {
        List<Categoria> categorias = categoriaRepository.findByNombreContainingIgnoreCase(nombreBusqueda);
        // Si no se encuentran resultados, devolvemos una lista vacía, no lanzamos excepción.
        if (categorias.isEmpty()) {
            return List.of(); // Devuelve una lista inmutable vacía
        }
        return categorias.stream()
                .map(categoria -> new CategoriaResponseDTO(categoria.getId(), categoria.getNombre()))
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoriaResponseDTO editarCategoria(Long id, CategoriaRequestDTO categoriaDTO) {
        Categoria categoriaAActualizar = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Categoría no encontrada para editar con ID: " + id));

        List<String> errors = new ArrayList<>();

        // Validaciones manuales para edición
        if (categoriaDTO.getNombre() == null || categoriaDTO.getNombre().trim().isEmpty()) {
            errors.add("El nombre de la categoría no puede estar vacío.");
        } else if (categoriaDTO.getNombre().length() > 255) {
            errors.add("El nombre de la categoría no puede exceder los 255 caracteres.");
        } else {
            // Solo verificar unicidad si el nombre ha cambiado
            if (!categoriaDTO.getNombre().trim().equalsIgnoreCase(categoriaAActualizar.getNombre())) {
                if (categoriaRepository.existsByNombreIgnoreCase(categoriaDTO.getNombre().trim())) {
                    errors.add("Ya existe otra categoría con el nombre '" + categoriaDTO.getNombre().trim() + "'.");
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Error en la validación de los campos de la categoría.", errors);
        }

        categoriaAActualizar.setNombre(categoriaDTO.getNombre().trim());
        Categoria categoriaActualizada = categoriaRepository.save(categoriaAActualizar);
        return new CategoriaResponseDTO(categoriaActualizada.getId(), categoriaActualizada.getNombre());
    }

    @Transactional
    public void eliminarCategoria(Long id) {
        // 1. Verificar si la categoría existe
        if (!categoriaRepository.existsById(id)) {
            throw new CategoryNotFoundException("Categoría no encontrada para eliminar con ID: " + id);
        }

        // 2. Validar si existen productos asociados a esta categoría
        // Necesitamos un método en ProductoRepository para contar productos por idCategoria.
        // Aún no lo tenemos, así que lo asumimos por ahora. Lo crearemos en el siguiente paso.
        long productosAsociados = productoRepository.countByIdCategoria(id);

        Categoria item = categoriaRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("Categoría no encontrada con ID: " + id));;

        if (productosAsociados > 0) {
            throw new CategoryInUseException("No se puede eliminar la categoría '" + item.getNombre().trim() + "' porque tiene " + productosAsociados + " productos asociados.");
        }

        // 3. Si no hay productos asociados, proceder con la eliminación
        categoriaRepository.deleteById(id);
    }
}