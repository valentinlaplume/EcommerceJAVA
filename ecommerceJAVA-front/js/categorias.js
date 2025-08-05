document.addEventListener("DOMContentLoaded", () => {
  // --- Referencias a elementos del DOM ---
  const messageArea = document.getElementById("messageArea");
  const categoryForm = document.getElementById("categoryForm");
  const categoryIdInput = document.getElementById("categoryId");
  const categoryNameInput = document.getElementById("categoryName");
  const saveCategoryButton = document.getElementById("saveCategoryButton");
  const cancelEditButton = document.getElementById("cancelEditButton");
  const formTitle = document.getElementById("formTitle");
  const categoryTableBody = document.querySelector("#categoryTable tbody");

  // --- Configuración de la API ---
  const API_BASE_URL = "http://localhost:8080/api"; // Asegúrate que esta URL sea correcta

  // --- Funciones de Utilidad ---

  // Muestra un mensaje al usuario (éxito o error)
  function showMessage(message, type) {
    messageArea.textContent = message;
    messageArea.className = `message ${type}`;
    messageArea.style.display = "block";
    setTimeout(() => {
      messageArea.style.display = "none";
    }, 5000);
  }

  // Limpia el formulario y lo pone en modo "Agregar"
  function resetForm() {
    categoryIdInput.value = "";
    categoryNameInput.value = "";
    formTitle.textContent = "Agregar Nueva Categoría";
    saveCategoryButton.textContent = "Guardar Categoría";
    cancelEditButton.style.display = "none";
    categoryForm.reset();
  }

  // --- Carga de Datos Iniciales ---

  // Cargar y mostrar todas las categorías en la tabla
  async function loadCategories() {
    categoryTableBody.innerHTML =
      '<tr><td colspan="3" style="text-align: center;">Cargando categorías...</td></tr>';
    try {
      const response = await fetch(`${API_BASE_URL}/categories/list`);
      if (!response.ok) {
        if (response.status === 204) {
          // No Content
          categoryTableBody.innerHTML =
            '<tr><td colspan="3" style="text-align: center;">No hay categorías registradas.</td></tr>';
          return;
        }
        throw new Error(`Error HTTP: ${response.status}`);
      }
      const categories = await response.json();

      categoryTableBody.innerHTML = ""; // Limpiar la tabla
      if (categories.length === 0) {
        categoryTableBody.innerHTML =
          '<tr><td colspan="3" style="text-align: center;">No hay categorías registradas.</td></tr>';
        return;
      }

      categories.forEach((category) => {
        const row = categoryTableBody.insertRow();
        row.dataset.categoryId = category.id; // Guarda el ID en el dataset

        row.innerHTML = `
                    <td>${category.id}</td>
                    <td>${category.nombre}</td>
                    <td class="table-actions">
                        <button class="btn btn-warning edit-btn" data-id="${category.id}">Editar</button>
                        <button class="btn btn-danger delete-btn" data-id="${category.id}">Eliminar</button>
                    </td>
                `;
      });
    } catch (error) {
      console.error("Error al cargar categorías:", error);
      categoryTableBody.innerHTML =
        '<tr><td colspan="3" style="text-align: center; color: red;">Error al cargar categorías.</td></tr>';
      showMessage(
        "Error al cargar las categorías. Verifica que el backend esté funcionando.",
        "error"
      );
    }
  }

  // --- Manejo del Formulario (Agregar/Editar) ---

  categoryForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const categoryId = categoryIdInput.value;
    const categoryName = categoryNameInput.value.trim();

    if (!categoryName) {
      showMessage("El nombre de la categoría no puede estar vacío.", "error");
      return;
    }

    const categoryData = {
      nombre: categoryName,
    };

    let url = `${API_BASE_URL}/categories`;
    let method = "POST";
    let successMessage = "Categoría agregada exitosamente.";

    if (categoryId) {
      // Si hay un ID, estamos editando
      url = `${API_BASE_URL}/categories/${categoryId}`;
      method = "PUT";
      successMessage = "Categoría actualizada exitosamente.";
    }

    try {
      const response = await fetch(url, {
        method: method,
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(categoryData),
      });

      if (!response.ok) {
        const errorData = await response.json();
        // Algunas excepciones de Spring Boot pueden devolver 'details' con una lista de errores
        const errorMessageText =
          errorData.message ||
          (errorData.details && errorData.details.join(", ")) ||
          `Error HTTP: ${response.status}`;
        throw new Error(errorMessageText);
      }

      showMessage(successMessage, "success");
      resetForm();
      loadCategories(); // Recargar la tabla
    } catch (error) {
      console.error("Error al guardar categoría:", error);
      showMessage(`Error al guardar categoría: ${error.message}`, "error");
    }
  });

  // --- Manejo de Acciones de la Tabla (Editar/Eliminar) ---

  categoryTableBody.addEventListener("click", async (e) => {
    const target = e.target;
    const categoryId = target.dataset.id;

    if (target.classList.contains("edit-btn")) {
      // Modo Edición
      try {
        const response = await fetch(
          `${API_BASE_URL}/categories/${categoryId}`
        );
        if (!response.ok) {
          throw new Error(`Error HTTP: ${response.status}`);
        }
        const category = await response.json();

        // Rellenar el formulario
        categoryIdInput.value = category.id;
        categoryNameInput.value = category.nombre;

        formTitle.textContent = `Editar Categoría: ${category.nombre}`;
        saveCategoryButton.textContent = "Actualizar Categoría";
        cancelEditButton.style.display = "inline-block";
        window.scrollTo({ top: 0, behavior: "smooth" }); // Desplazar al inicio
      } catch (error) {
        console.error("Error al cargar categoría para edición:", error);
        showMessage(
          `Error al cargar categoría para edición: ${error.message}`,
          "error"
        );
      }
    } else if (target.classList.contains("delete-btn")) {
      // Modo Eliminación
      if (
        confirm(
          "¿Estás seguro de que quieres eliminar esta categoría? Si hay productos asociados, la eliminación no será posible."
        )
      ) {
        try {
          const response = await fetch(
            `${API_BASE_URL}/categories/${categoryId}`,
            {
              method: "DELETE",
            }
          );

          if (!response.ok) {
            const errorData = await response.json();
            // Captura el mensaje de CategoryInUseException o ValidationException
            const errorMessageText =
              errorData.message ||
              (errorData.details && errorData.details.join(", ")) ||
              `Error HTTP: ${response.status}`;
            throw new Error(errorMessageText);
          }

          showMessage("Categoría eliminada exitosamente.", "success");
          loadCategories(); // Recargar la tabla
          resetForm();
        } catch (error) {
          console.error("Error al eliminar categoría:", error);
          showMessage(`Error al eliminar categoría: ${error.message}`, "error");
        }
      }
    }
  });

  // Manejar el botón "Cancelar Edición"
  cancelEditButton.addEventListener("click", resetForm);

  // --- Carga Inicial de Datos al Cargar la Página ---
  loadCategories();
});
