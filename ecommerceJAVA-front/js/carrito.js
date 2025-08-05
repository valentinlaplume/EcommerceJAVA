document.addEventListener("DOMContentLoaded", () => {
  // --- Referencias a elementos del DOM ---
  const messageArea = document.getElementById("messageArea");
  const productsListDiv = document.getElementById("productsList"); // Contenedor para las tarjetas de productos
  const cartItemsBody = document.getElementById("cartItemsBody"); // Body de la tabla del carrito
  const cartTotalSpan = document.getElementById("cartTotal");
  const clearCartButton = document.getElementById("clearCartButton");
  const checkoutButton = document.getElementById("checkoutButton");
  const compraExitosaModal = new bootstrap.Modal(
    document.getElementById("compraExitosaModal")
  );
  const modalTotalSpan = document.getElementById("modal-total");
  const logoutNavLink = document.getElementById("logoutNavLink");

  // --- Configuración de la API ---
  const API_BASE_URL = "http://localhost:8080/api"; // ¡VERIFICA ESTA URL!

  // --- Estado del Carrito (se guarda en localStorage) ---
  // Estructura de un item en el carrito: { id: productoId, nombre: "", precio: 0, cantidad: 0, stockDisponible: 0, imageUrl: "" }
  let cart = JSON.parse(localStorage.getItem("ecommerceCart")) || [];

  // --- Funciones de Utilidad ---

  // Muestra un mensaje al usuario
  function showMessage(message, type = "info") {
    messageArea.textContent = message;
    messageArea.className = `message-area ${type}`;
    messageArea.style.display = "block";
    setTimeout(() => {
      messageArea.style.display = "none";
    }, 5000);
  }

  // Guarda el estado actual del carrito en localStorage
  function saveCart() {
    localStorage.setItem("ecommerceCart", JSON.stringify(cart));
  }

  // Calcula y actualiza el total del carrito
  function updateCartTotal() {
    const total = cart.reduce(
      (sum, item) => sum + item.precio * item.cantidad,
      0
    );
    cartTotalSpan.textContent = total.toFixed(2);
    modalTotalSpan.textContent = total.toFixed(2); // Para el modal de éxito
  }

  // Renderiza las tarjetas de productos en la sección izquierda
  async function loadProductsForShop() {
    productsListDiv.innerHTML =
      '<p class="text-center text-muted">Cargando productos disponibles...</p>';
    try {
      const response = await fetch(`${API_BASE_URL}/products/list`);
      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }
      const products = await response.json();

      productsListDiv.innerHTML = ""; // Limpiar el contenedor de productos
      if (products.length === 0) {
        productsListDiv.innerHTML =
          '<p class="text-center text-muted">No hay productos disponibles para mostrar.</p>';
        return;
      }

      products.forEach((product) => {
        const colDiv = document.createElement("div");
        // Col-md-6 para 2 por fila en pantallas medianas y grandes.
        // Col-12 para 1 por fila en pantallas pequeñas.
        colDiv.classList.add(
          "col-12",
          "col-sm-6",
          "col-md-6",
          "col-lg-6",
          "mb-4"
        ); // mb-4 para margen inferior entre filas

        colDiv.innerHTML = `
                    <div class="card h-100">
                        <img src="${
                          product.imageUrl ||
                          "https://dummyimage.com/300x200/cccccc/000000.png&text=Producto"
                        }" class="card-img-top" alt="${product.nombre}">
                        <div class="card-body d-flex flex-column">
                            <h5 class="card-title">${product.nombre}</h5>
                            <p class="card-text text-muted">Categoría: ${
                              product.categoria
                                ? product.categoria.nombre
                                : "N/A"
                            }</p>
                            <p class="card-text text-muted">Stock: ${
                              product.stock
                            }</p>
                            <p class="card-text card-text-description">${
                              product.descripcion &&
                              product.descripcion.length > 100
                                ? product.descripcion.substring(0, 97) + "..."
                                : product.descripcion || "Sin descripción."
                            }</p>
                            <p class="product-price">$${product.precio.toFixed(
                              2
                            )}</p>
                            <button class="btn btn-primary add-to-cart-btn mt-auto" data-id="${
                              product.id
                            }" ${product.stock === 0 ? "disabled" : ""}>
                                ${
                                  product.stock === 0
                                    ? "Sin Stock"
                                    : "Añadir al Carrito"
                                }
                            </button>
                        </div>
                    </div>
                `;
        productsListDiv.appendChild(colDiv);
      });
    } catch (error) {
      console.error("Error al cargar productos para la tienda:", error);
      productsListDiv.innerHTML =
        '<p class="text-center text-danger">Error al cargar productos. Verifica que el backend esté funcionando.</p>';
      showMessage(
        "Error al cargar los productos. Intenta recargar la página.",
        "error"
      );
    }
  }

  // Renderiza los productos en la tabla del carrito (sección derecha)
  function renderCart() {
    cartItemsBody.innerHTML = ""; // Limpiar tabla
    if (cart.length === 0) {
      cartItemsBody.innerHTML =
        '<tr><td colspan="4" class="text-center text-muted">Tu carrito está vacío.</td></tr>';
      checkoutButton.disabled = true;
      clearCartButton.disabled = true;
      cartTotalSpan.textContent = "0.00";
      return;
    }

    checkoutButton.disabled = false;
    clearCartButton.disabled = false;

    cart.forEach((item) => {
      const row = cartItemsBody.insertRow();
      // Eliminado el atributo onchange directamente en el HTML del input
      row.innerHTML = `
                <td>${item.nombre}</td>
                <td>
                    <div class="quantity-controls">
                        <button class="btn btn-outline-secondary btn-sm decrement-quantity" data-id="${
                          item.id
                        }">-</button>
                        <input type="number" class="form-control quantity-input" data-id="${
                          item.id
                        }" value="${item.cantidad}" min="1" max="${
        item.stockDisponible || 999
      }">
                        <button class="btn btn-outline-secondary btn-sm increment-quantity" data-id="${
                          item.id
                        }">+</button>
                    </div>
                </td>
                <td>$${(item.precio * item.cantidad).toFixed(2)}</td>
                <td>
                    <button class="btn btn-danger btn-sm remove-btn" data-id="${
                      item.id
                    }">Eliminar</button>
                </td>
            `;
    });
    updateCartTotal(); // Llamada para actualizar el total
  }

  // Añadir un producto al carrito
  async function addToCart(productId, quantity = 1) {
    try {
      const response = await fetch(`${API_BASE_URL}/products/${productId}`);
      if (!response.ok) {
        throw new Error("Producto no encontrado o error al obtener detalles.");
      }
      const product = await response.json();

      // Validar si el producto tiene stock
      if (product.stock <= 0) {
        showMessage(
          `El producto "${product.nombre}" no tiene stock disponible.`,
          "error"
        );
        return;
      }

      const existingItem = cart.find((item) => item.id === productId);

      if (existingItem) {
        const newQuantity = existingItem.cantidad + quantity;
        if (newQuantity > product.stock) {
          showMessage(
            `Solo hay ${product.stock} unidades de "${product.nombre}" en stock. No se pudo añadir más.`,
            "error"
          );
          return;
        }
        existingItem.cantidad = newQuantity;
      } else {
        if (quantity > product.stock) {
          showMessage(
            `No hay suficiente stock de "${product.nombre}". Solo ${product.stock} disponibles.`,
            "error"
          );
          return;
        }
        cart.push({
          id: product.id,
          nombre: product.nombre,
          precio: product.precio,
          cantidad: quantity,
          stockDisponible: product.stock, // Guardar el stock actual para validaciones en el carrito
          imageUrl: product.imageUrl, // Guardar la URL de la imagen si está disponible
        });
      }
      saveCart();
      renderCart(); // Vuelve a renderizar el carrito para reflejar los cambios
      showMessage(
        `"${product.nombre}" ${
          existingItem ? "cantidad actualizada" : "añadido"
        } al carrito.`,
        "success"
      );
    } catch (error) {
      console.error("Error al añadir producto al carrito:", error);
      showMessage(
        `Error al añadir producto al carrito: ${error.message}`,
        "error"
      );
    }
  }

  // Actualizar la cantidad de un producto en el carrito (llamado por el event listener)
  function updateQuantity(productId, newQuantity) {
    const cartItem = cart.find((item) => item.id === productId);
    if (cartItem) {
      if (isNaN(newQuantity) || newQuantity < 1) {
        newQuantity = 1;
      }
      if (newQuantity > cartItem.stockDisponible) {
        showMessage(
          `Solo hay ${cartItem.stockDisponible} unidades de "${cartItem.nombre}" en stock.`,
          "error"
        );
        newQuantity = cartItem.stockDisponible;
      }
      cartItem.cantidad = newQuantity;
      saveCart();
      renderCart(); // Volver a renderizar para actualizar subtotales y total
    }
  }

  // --- Manejo de Eventos ---

  // Evento para añadir al carrito desde la lista de productos
  productsListDiv.addEventListener("click", (e) => {
    if (e.target.classList.contains("add-to-cart-btn")) {
      const productId = parseInt(e.target.dataset.id);
      addToCart(productId, 1);
    }
  });

  // Delegación de eventos para los controles del carrito (inputs, botones +/- y eliminar)
  cartItemsBody.addEventListener("click", (e) => {
    const target = e.target;
    const productId = parseInt(target.dataset.id);

    if (target.classList.contains("remove-btn")) {
      cart = cart.filter((item) => item.id !== productId);
      saveCart();
      renderCart();
      showMessage("Producto eliminado del carrito.", "info");
    } else if (target.classList.contains("increment-quantity")) {
      // Encuentra el input de cantidad asociado
      const input = target
        .closest(".quantity-controls")
        .querySelector(".quantity-input");
      let currentQuantity = parseInt(input.value);
      updateQuantity(productId, currentQuantity + 1);
    } else if (target.classList.contains("decrement-quantity")) {
      // Encuentra el input de cantidad asociado
      const input = target
        .closest(".quantity-controls")
        .querySelector(".quantity-input");
      let currentQuantity = parseInt(input.value);
      updateQuantity(productId, currentQuantity - 1);
    }
  });

  cartItemsBody.addEventListener("input", (e) => {
    if (e.target.classList.contains("quantity-input")) {
      const productId = parseInt(e.target.dataset.id);
      const newQuantity = parseInt(e.target.value);
      updateQuantity(productId, newQuantity);
    }
  });

  // Vaciar todo el carrito
  clearCartButton.addEventListener("click", () => {
    if (confirm("¿Estás seguro de que quieres vaciar todo el carrito?")) {
      cart = [];
      saveCart();
      renderCart();
      showMessage("El carrito ha sido vaciado.", "info");
    }
  });

  // Realizar el Pedido (Checkout)
  checkoutButton.addEventListener("click", async () => {
    if (cart.length === 0) {
      showMessage(
        "Tu carrito está vacío. Añade productos antes de realizar un pedido.",
        "error"
      );
      return;
    }

    const loggedInUserId = localStorage.getItem("loggedInUserId");
    if (!loggedInUserId) {
      showMessage(
        "No hay un usuario seleccionado. Por favor, selecciona tu usuario en la página principal.",
        "error"
      );
      return;
    }

    const orderItems = cart.map((item) => ({
      idProducto: item.id,
      cantidad: item.cantidad,
    }));

    const orderData = {
      idUsuario: parseInt(loggedInUserId, 10),
      // ¡AQUÍ ESTÁ EL CAMBIO! Cambia 'items' a 'itemsPedido'
      itemsPedido: orderItems,
    };

    console.log("Pedido a generar (body):");
    console.log(JSON.stringify(orderData, null, 2)); // Usa null, 2 para un formato legible en consola

    try {
      const response = await fetch(`${API_BASE_URL}/pedidos`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(orderData),
      });

      if (!response.ok) {
        const errorData = await response.json();
        const errorMessageText =
          errorData.message ||
          (errorData.details && errorData.details.join(", ")) ||
          `Error HTTP: ${response.status} - ${response.statusText}`;
        throw new Error(errorMessageText);
      }

      // Si el pedido es exitoso
      // const orderConfirmation = await response.json(); // Si necesitas los datos de confirmación

      // Muestra el total pagado en el modal
      modalTotalSpan.textContent = cartTotalSpan.textContent; // Usa el total actual del carrito
      compraExitosaModal.show(); // Muestra el modal de Bootstrap

      cart = []; // Vaciar el carrito después de un pedido exitoso
      saveCart();
      renderCart(); // Re-renderiza el carrito vacío
      loadProductsForShop(); // Recarga los productos en la tienda por si el stock cambió
    } catch (error) {
      console.error("Error al realizar el pedido:", error);
      showMessage(`Error al realizar el pedido: ${error.message}`, "error");
    }
  });

  // --- Verificación de Sesión y Logout ---
  // Este bloque de código está en el HTML, pero es bueno tener la referencia aquí.
  // document.addEventListener('DOMContentLoaded', () => { ... });

  // --- Inicialización ---
  // Esta parte ya está en el HTML:
  // const loggedInUserId = localStorage.getItem('loggedInUserId');
  // if (!loggedInUserId) { ... }
  // logoutNavLink.addEventListener('click', (e) => { ... });

  loadProductsForShop(); // Cargar productos para la tienda al inicio
  renderCart(); // Renderizar el carrito (puede estar vacío)
});
