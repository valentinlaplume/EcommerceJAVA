document.addEventListener("DOMContentLoaded", () => {
  const API_BASE_URL = "http://localhost:8080/api/usuarios"; // Asegúrate de que esta sea la URL correcta de tu backend Java
  const ordersTableBody = document.getElementById("ordersTableBody");
  const messageArea = document.getElementById("messageArea");

  // Elementos de filtro
  const filterOrderIdInput = document.getElementById("filterOrderId");
  const filterUserIdInput = document.getElementById("filterUserId");
  const filterStatusSelect = document.getElementById("filterStatus");
  const applyFiltersBtn = document.getElementById("applyFiltersBtn");
  const clearFiltersBtn = document.getElementById("clearFiltersBtn");

  // Función para mostrar mensajes
  function showMessage(message, type) {
    messageArea.textContent = message;
    messageArea.className = `message-area ${type}`; // Elimina clases anteriores y añade la nueva
    messageArea.style.display = "block";
    setTimeout(() => {
      messageArea.style.display = "none";
    }, 5000); // Ocultar después de 5 segundos
  }

  // Función para obtener la clase CSS del estado
  function getStatusBadgeClass(status) {
    switch (status.toUpperCase()) {
      case "PENDIENTE":
        return "status-pendiente";
      case "PROCESANDO":
        return "status-procesando";
      case "ENVIADO":
        return "status-enviado";
      case "ENTREGADO":
        return "status-entregado";
      case "CANCELADO":
        return "status-cancelado";
      default:
        return "";
    }
  }

  // Función para cargar los pedidos
  async function fetchOrders() 
  {
    ordersTableBody.innerHTML =
      '<tr><td colspan="6" class="text-center text-info">Cargando pedidos...</td></tr>';
    messageArea.style.display = "none"; // Oculta mensajes anteriores

    const loggedInUserId = localStorage.getItem("loggedInUserId");
    if (!loggedInUserId) {
      showMessage(
        "No hay un usuario seleccionado. Por favor, selecciona tu usuario en la página principal.",
        "error"
      );
      return;
    }

    const orderId = filterOrderIdInput.value;
    const userId = filterUserIdInput.value;
    const status = filterStatusSelect.value;

    // Construir URL con parámetros de filtro
    const queryParams = new URLSearchParams();
    if (orderId) queryParams.append("id", orderId); // Asumiendo que tu backend filtra por 'id'
    if (userId) queryParams.append("userId", userId); // Asumiendo que tu backend filtra por 'userId'
    if (status) queryParams.append("estado", status); // Asumiendo que tu backend filtra por 'estado'

    const url = `${API_BASE_URL}/${loggedInUserId.toString()}/pedidos`;

    try {
      const response = await fetch(url);
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(
          errorData.message ||
            `Error HTTP: ${response.status} - ${response.statusText}`
        );
      }
      const orders = await response.json();
      renderOrdersTable(orders);
    } catch (error) {
      console.error("Error al cargar pedidos:", error);
      showMessage(`Error al cargar pedidos: ${error.message}`, "error");
      ordersTableBody.innerHTML =
        '<tr><td colspan="6" class="text-center text-danger">Error al cargar pedidos. Intenta de nuevo más tarde.</td></tr>';
    }
  }

  // Función para renderizar la tabla de pedidos
  function renderOrdersTable(orders) {
    ordersTableBody.innerHTML = ""; // Limpiar la tabla

    if (orders.length === 0) {
      ordersTableBody.innerHTML =
        '<tr><td colspan="6" class="text-center text-muted">No se encontraron pedidos.</td></tr>';
      return;
    }

    orders.forEach((order) => {
      const row = document.createElement("tr");

      // Formatear fecha (si tu backend envía un string ISO)
      const orderDate = new Date(order.fechaPedido || order.fechaCreacion); // Ajusta según el nombre real de tu campo de fecha
      const formattedDate = orderDate.toLocaleDateString("es-AR", {
        year: "numeric",
        month: "long",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit",
      });

      // Construir lista de ítems del pedido
      const itemsList = document.createElement("ul");
      itemsList.className = "order-items-list";
      if (order.itemsPedido && Array.isArray(order.itemsPedido)) {
        // Asegúrate de que 'itemsPedido' sea el nombre correcto
        order.itemsPedido.forEach((item) => {
          // Itera sobre los ítems del pedido
          const itemLi = document.createElement("li");
          // Ajusta estos nombres de campo según tu ItemPedidoResponseDTO del backend
          itemLi.textContent = `${item.cantidad} x ${
            item.nombreProducto || "Producto Desconocido"
          } ($${(item.precioUnitario * item.cantidad).toFixed(2)})`;
          itemsList.appendChild(itemLi);
        });
      } else {
        const itemLi = document.createElement("li");
        itemLi.textContent = "No hay ítems registrados.";
        itemsList.appendChild(itemLi);
      }

      console.log(order)
      const statusClass = getStatusBadgeClass(order.estado.nombre); // Ajusta según el nombre real de tu campo de estado

      row.innerHTML = `
                <td>${order.id}</td>
                <td>${order.idUsuario || "N/A"}</td>
                <td>${formattedDate}</td>
                <td>$${order.total ? order.total.toFixed(2) : "0.00"}</td>
                <td><span class="status-badge ${statusClass}">${
        order.estado.nombre || "DESCONOCIDO"
      }</span></td>
                <td></td> `;
      // Insertar la lista de ítems en la celda correspondiente
      row.querySelector("td:last-child").appendChild(itemsList);
      ordersTableBody.appendChild(row);
    });
  }

  // Event Listeners para filtros
  applyFiltersBtn.addEventListener("click", fetchOrders);
  clearFiltersBtn.addEventListener("click", () => {
    filterOrderIdInput.value = "";
    filterUserIdInput.value = "";
    filterStatusSelect.value = "";
    fetchOrders(); // Recargar todos los pedidos
  });

  // Cargar pedidos al iniciar la página
  fetchOrders();
});
