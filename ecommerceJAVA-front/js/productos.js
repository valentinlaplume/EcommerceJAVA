document.addEventListener("DOMContentLoaded", () => {
  // --- Referencias a elementos del DOM ---
  const messageArea = document.getElementById("messageArea");
  const productForm = document.getElementById("productForm");
  const productIdInput = document.getElementById("productId");
  const productNameInput = document.getElementById("productName");
  const productPriceInput = document.getElementById("productPrice");
  const productStockInput = document.getElementById("productStock");
  const productDescriptionInput = document.getElementById("productDescription");
  const productCategorySelect = document.getElementById("productCategory");
  const saveProductButton = document.getElementById("saveProductButton");
  const cancelEditButton = document.getElementById("cancelEditButton");
  const formTitle = document.getElementById("formTitle");
  const productTableBody = document.querySelector("#productTable tbody");

  // --- Configuración de la API ---
  const API_BASE_URL = "http://localhost:8080/api"; // Asegúrate que esta URL sea correcta para tu backend

  // --- Funciones de Utilidad ---

  // Muestra un mensaje al usuario (éxito o error)
  function showMessage(message, type) {
    messageArea.textContent = message;
    messageArea.className = `message ${type}`; // Añade clase 'success' o 'error'
    messageArea.style.display = "block";
    setTimeout(() => {
      messageArea.style.display = "none";
    }, 5000); // Oculta el mensaje después de 5 segundos
  }

  // Limpia el formulario y lo pone en modo "Agregar"
  function resetForm() {
    productIdInput.value = "";
    productNameInput.value = "";
    productPriceInput.value = "";
    productStockInput.value = "";
    productDescriptionInput.value = "";
    productCategorySelect.value = ""; // Selecciona la opción por defecto
    formTitle.textContent = "Agregar Nuevo Producto";
    saveProductButton.textContent = "Guardar Producto";
    cancelEditButton.style.display = "none";
    productForm.reset(); // Resetea el formulario completamente
  }

  // --- Carga de Datos Iniciales ---

  // Cargar categorías en el select del formulario
  async function loadCategories() {
    try {
      const response = await fetch(`${API_BASE_URL}/categories/list`);
      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }
      const categories = await response.json();

      productCategorySelect.innerHTML =
        '<option value="">-- Selecciona una Categoría --</option>';
      categories.forEach((category) => {
        const option = document.createElement("option");
        option.value = category.id; // El ID de la categoría
        option.textContent = category.nombre; // El nombre de la categoría
        productCategorySelect.appendChild(option);
      });
    } catch (error) {
      console.error("Error al cargar categorías:", error);
      productCategorySelect.innerHTML =
        '<option value="">Error al cargar categorías</option>';
      showMessage(
        "Error al cargar las categorías. Intenta recargar la página.",
        "error"
      );
    }
  }

  // Cargar y mostrar todos los productos en la tabla
  async function loadProducts() {
    productTableBody.innerHTML =
      '<tr><td colspan="7" style="text-align: center;">Cargando productos...</td></tr>';
    try {
      const response = await fetch(`${API_BASE_URL}/products/list`);
      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }
      const products = await response.json();

      productTableBody.innerHTML = ""; // Limpiar la tabla antes de añadir nuevos datos
      if (products.length === 0) {
        productTableBody.innerHTML =
          '<tr><td colspan="7" style="text-align: center;">No hay productos registrados.</td></tr>';
        return;
      }

      products.forEach((product) => {
        const row = productTableBody.insertRow();
        row.dataset.productId = product.id; // Guarda el ID en el dataset de la fila para facilitar la edición/eliminación

        row.innerHTML = `
                    <td>${product.id}</td>
                    <td>${product.nombre}</td>
                    <td>$${product.precio.toFixed(2)}</td>
                    <td>${product.stock}</td>
                    <td>${product.descripcion || ""}</td>
                    <td>${
                      product.categoria ? product.categoria.nombre : "N/A"
                    }</td>
                    <td class="table-actions">
                        <button class="btn btn-warning edit-btn" data-id="${
                          product.id
                        }">Editar</button>
                        <button class="btn btn-danger delete-btn" data-id="${
                          product.id
                        }">Eliminar</button>
                    </td>
                `;
      });
    } catch (error) {
      console.error("Error al cargar productos:", error);
      productTableBody.innerHTML =
        '<tr><td colspan="7" style="text-align: center; color: red;">Error al cargar productos.</td></tr>';
      showMessage(
        "Error al cargar los productos. Verifica que el backend esté funcionando.",
        "error"
      );
    }
  }

  // --- Manejo del Formulario (Agregar/Editar) ---

  productForm.addEventListener("submit", async (e) => {
    e.preventDefault(); // Prevenir el envío por defecto del formulario

    const productId = productIdInput.value;
    const productName = productNameInput.value.trim();
    const productPrice = parseFloat(productPriceInput.value);
    const productStock = parseInt(productStockInput.value, 10);
    const productDescription = productDescriptionInput.value.trim();
    const productCategoryId = productCategorySelect.value;

    // Validaciones básicas del lado del cliente
    if (
      !productName ||
      isNaN(productPrice) ||
      isNaN(productStock) ||
      !productCategoryId
    ) {
      showMessage(
        "Por favor, completa todos los campos obligatorios (Nombre, Precio, Stock, Categoría).",
        "error"
      );
      return;
    }
    if (productPrice <= 0) {
      showMessage("El precio debe ser mayor que cero.", "error");
      return;
    }
    if (productStock < 0) {
      showMessage("El stock no puede ser negativo.", "error");
      return;
    }

    const productData = {
      nombre: productName,
      precio: productPrice,
      stock: productStock,
      descripcion: productDescription,
      idCategoria: parseInt(productCategoryId, 10), // Asegúrate de enviar el ID de la categoría
    };

    let url = `${API_BASE_URL}/products`;
    let method = "POST";
    let successMessage = "Producto agregado exitosamente.";

    if (productId) {
      // Si hay un ID, estamos editando
      url = `${API_BASE_URL}/products/${productId}`;
      method = "PUT";
      successMessage = "Producto actualizado exitosamente.";
    }

    try {
      const response = await fetch(url, {
        method: method,
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(productData),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || `Error HTTP: ${response.status}`);
      }

      showMessage(successMessage, "success");
      resetForm(); // Limpiar formulario
      loadProducts(); // Recargar la tabla de productos
    } catch (error) {
      console.error("Error al guardar producto:", error);
      showMessage(`Error al guardar producto: ${error.message}`, "error");
    }
  });

  // --- Manejo de Acciones de la Tabla (Editar/Eliminar) ---

  productTableBody.addEventListener("click", async (e) => {
    const target = e.target;
    const productId = target.dataset.id;

    if (target.classList.contains("edit-btn")) {
      // Modo Edición
      try {
        const response = await fetch(`${API_BASE_URL}/products/${productId}`);
        if (!response.ok) {
          throw new Error(`Error HTTP: ${response.status}`);
        }
        const product = await response.json();

        // Rellenar el formulario con los datos del producto
        productIdInput.value = product.id;
        productNameInput.value = product.nombre;
        productPriceInput.value = product.precio;
        productStockInput.value = product.stock;
        productDescriptionInput.value = product.descripcion || "";
        // Asegúrate de que la categoría se seleccione correctamente
        if (product.categoria && product.categoria.id) {
          productCategorySelect.value = product.categoria.id;
        } else {
          productCategorySelect.value = ""; // Si no tiene categoría o es inválida
        }

        formTitle.textContent = `Editar Producto: ${product.nombre}`;
        saveProductButton.textContent = "Actualizar Producto";
        cancelEditButton.style.display = "inline-block"; // Mostrar botón de cancelar
        window.scrollTo({ top: 0, behavior: "smooth" }); // Desplazar al inicio de la página
      } catch (error) {
        console.error("Error al cargar producto para edición:", error);
        showMessage(
          `Error al cargar producto para edición: ${error.message}`,
          "error"
        );
      }
    } else if (target.classList.contains("delete-btn")) {
      // Modo Eliminación
      if (confirm("¿Estás seguro de que quieres eliminar este producto?")) {
        try {
          const response = await fetch(
            `${API_BASE_URL}/products/${productId}`,
            {
              method: "DELETE",
            }
          );

          if (!response.ok) {
            const errorData = await response.json();
            throw new Error(
              errorData.message || `Error HTTP: ${response.status}`
            );
          }

          showMessage("Producto eliminado exitosamente.", "success");
          loadProducts(); // Recargar la tabla
          resetForm(); // Limpiar formulario por si estaba en modo edición
        } catch (error) {
          console.error("Error al eliminar producto:", error);
          showMessage(`Error al eliminar producto: ${error.message}`, "error");
        }
      }
    }
  });

  // Manejar el botón "Cancelar Edición"
  cancelEditButton.addEventListener("click", resetForm);

  // --- Carga Inicial de Datos al Cargar la Página ---
  loadCategories(); // Cargar categorías primero
  loadProducts(); // Luego cargar productos
});
