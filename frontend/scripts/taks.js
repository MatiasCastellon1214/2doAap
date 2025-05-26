if (!localStorage.getItem('jwt')) {
  location.replace("./index.html");
}

window.addEventListener('load', function () {
  const apiUrl = "http://localhost:8081";
  const tasksEndpoint = `${apiUrl}/tasks`;
  const token = localStorage.getItem('jwt'); // Consistent token access
  const userData = JSON.parse(localStorage.getItem("userData"));
  const userName = document.querySelector(".user-info p");

  // Validate token exists
  if (!token) {
    console.error("No JWT token found");
    location.replace("./index.html");
    return;
  }

  // Show user name
  if (userData?.firstName) {
    userName.textContent = userData.firstName;
  }

  // DOM elements
  const btnCerrarSesion = document.querySelector("#closeApp");
  const formCrearTarea = document.querySelector(".nueva-tarea");
  const nuevaTarea = document.querySelector("#nuevaTarea");

  // Load initial tasks
  loadTasks();

  // Event listeners
  btnCerrarSesion.addEventListener('click', logout);
  formCrearTarea.addEventListener('submit', createTask);

  // Global variable to store current tasks
  let currentTasks = [];

  // Task loading function
  async function loadTasks() {
    try {
      const response = await fetch(`${tasksEndpoint}/my-tasks`, {
        headers: { 
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      });
      
      if (!response.ok) {
        if (response.status === 401) {
          // Token expired or invalid
          localStorage.clear();
          location.replace("./index.html");
          return;
        }
        throw new Error(await getErrorMessage(response));
      }
      
      currentTasks = await response.json();
      renderTasks(currentTasks);
      setupTaskButtons();
    } catch (error) {
      showError(error);
    }
  }

  // Task creation function
  async function createTask(e) {
    e.preventDefault();
    const description = nuevaTarea.value.trim();
    
    if (!description) {
      showError("The description cannot be empty");
      return;
    }

    try {
      if (!token) {
        throw new Error("No authentication token found");
      }

      console.log("Creating task with token:", token ? "Token exists" : "No token");

      const response = await fetch(`${tasksEndpoint}/create`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({
          description: description,
          completed: false
          // userId is set automatically from the token in the backend
        })
      });
      
      console.log("Response status:", response.status);
      
      if (!response.ok) {
        if (response.status === 401) {
          // Token expired or invalid
          localStorage.clear();
          location.replace("./index.html");
          return;
        }
        throw new Error(await getErrorMessage(response));
      }
      
      nuevaTarea.value = "";
      await loadTasks();
    } catch (error) {
      console.error("Create task error:", error);
      showError(error);
    }
  }

  // Task status update function
  async function updateTaskStatus(taskId, completed) {
    try {
      // Find the task to update
      const taskToUpdate = currentTasks.find(t => t.id == taskId);
      if (!taskToUpdate) throw new Error("Task not found");

      // Prepare payload with all necessary fields for PUT update
      const payload = {
        id: Number(taskId),
        description: taskToUpdate.description,
        completed: completed,
        userId: taskToUpdate.user?.id || userData?.id // Use user ID from task or userData
      };

      console.log("Sending PUT update payload:", payload);
      console.log("Task ID:", taskId);
      console.log("User ID:", payload.userId);

      const response = await fetch(`${tasksEndpoint}/update`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });
      
      if (!response.ok) {
        if (response.status === 401) {
          localStorage.clear();
          location.replace("./index.html");
          return;
        }
        const errorData = await response.json();
        throw new Error(errorData.message || 'Error updating task');
      }
      
      await loadTasks();
      return await response.json();
    } catch (error) {
      console.error("Update task error:", {
        error: error,
        taskId: taskId,
        completed: completed
      });
      showError(error);
      throw error;
    }
  }

  // Task deletion function
  async function deleteTask(taskId) {
    if (!confirm("¿Delete this task?")) return;
    
    try {
      // Note: Your backend endpoint is `/delete/{id}` not `/{id}`
      const response = await fetch(`${tasksEndpoint}/delete/${taskId}`, {
        method: "DELETE",
        headers: { 
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      });
      
      if (!response.ok) {
        if (response.status === 401) {
          localStorage.clear();
          location.replace("./index.html");
          return;
        }
        throw new Error(await getErrorMessage(response));
      }
      
      await loadTasks();
    } catch (error) {
      showError(error);
    }
  }

  // Function to render tasks
  function renderTasks(tasks) {
    const pendingTasksContainer = document.querySelector(".tareas-pendientes");
    const completedTasksContainer = document.querySelector(".tareas-terminadas");
    const completedCounter = document.querySelector("#cantidad-finalizadas");

    if (!pendingTasksContainer || !completedTasksContainer || !completedCounter) {
      console.error("Required DOM elements not found");
      return;
    }

    pendingTasksContainer.innerHTML = "";
    completedTasksContainer.innerHTML = "";

    const completedTasks = tasks.filter(task => task.completed);
    completedCounter.textContent = completedTasks.length;

    tasks.forEach(task => {
      const taskElement = createTaskElement(task);
      if (task.completed) {
        completedTasksContainer.appendChild(taskElement);
      } else {
        pendingTasksContainer.appendChild(taskElement);
      }
    });
  }

  // Function to create task element
  function createTaskElement(task) {
    const taskElement = document.createElement("li");
    taskElement.className = "tarea";
    taskElement.dataset.id = task.id;

    const date = new Date(task.createdAt);
    // Handle LocalDate format from backend
    if (typeof task.createdAt === 'string' && task.createdAt.includes('-')) {
      // If it's already a date string, parse it directly
      const dateParts = task.createdAt.split('-');
      date.setFullYear(dateParts[0], dateParts[1] - 1, dateParts[2]);
    }

    if (task.completed) {
      taskElement.innerHTML = `
        <div class="hecha"><i class="fa-regular fa-circle-check"></i></div>
        <div class="descripcion">
          <p class="nombre">${task.description}</p>
          <div class="cambios-estados">
            <button class="change incompleta"><i class="fa-solid fa-rotate-left"></i></button>
            <button class="borrar"><i class="fa-regular fa-trash-can"></i></button>
          </div>
        </div>
      `;
    } else {
      taskElement.innerHTML = `
        <button class="change"><i class="fa-regular fa-circle"></i></button>
        <div class="descripcion">
          <p class="nombre">${task.description}</p>
          <p class="timestamp">${date.toLocaleDateString('es-AR')}</p>
        </div>
      `;
    }

    return taskElement;
  }

  // Configure task buttons
  function setupTaskButtons() {
    document.querySelectorAll(".change").forEach(btn => {
      btn.addEventListener("click", function() {
        const taskId = this.closest(".tarea").dataset.id;
        const isCompleted = this.classList.contains("incompleta");
        updateTaskStatus(taskId, !isCompleted);
      });
    });

    document.querySelectorAll(".borrar").forEach(btn => {
      btn.addEventListener("click", function() {
        const taskId = this.closest(".tarea").dataset.id;
        deleteTask(taskId);
      });
    });
  }

  // Logout function
  function logout() {
    localStorage.clear();
    location.replace("./index.html");
  }

  // Helper functions
  async function getErrorMessage(response) {
    try {
      const error = await response.json();
      return error.message || 'Error in the application';
    } catch {
      return `Error ${response.status}: ${response.statusText}`;
    }
  }

  function showError(error) {
    console.error("Error:", error);
    alert(error.message || error || "An error occurred");
  }
});