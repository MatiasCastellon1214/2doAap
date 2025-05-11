if (!localStorage.jwt) {
  location.replace("./index.html");
}

window.addEventListener('load', function () {
  const apiUrl = "http://localhost:8081";
  const tasksEndpoint = `${apiUrl}/tasks`;
  const token = localStorage.jwt;
  const userData = JSON.parse(localStorage.getItem("userData"));
  const userName = document.querySelector(".user-info p");

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

  // Task upload function
  async function loadTasks() {
    try {
      const response = await fetch(`${tasksEndpoint}/my-tasks`, {
        headers: { 
          "Authorization": `Bearer ${token}`
        }
      });
      
      if (!response.ok) {
        throw new Error(await getErrorMessage(response));
      }
      
      const tasks = await response.json();
      renderTasks(tasks);
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
    const payload = {
      description: description,
    };

    const response = await fetch(`${tasksEndpoint}/create`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
      },
      body: JSON.stringify(payload) // Send the complete object
    });
    
    if (!response.ok) {
      throw new Error(await getErrorMessage(response));
    }
    
    nuevaTarea.value = "";
    await loadTasks();
  } catch (error) {
    showError(error);
  }
}

  // Task status update function
  async function updateTaskStatus(taskId, completed) {
    try {
      const response = await fetch(`${tasksEndpoint}/${taskId}/status`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({ completed })
      });
      
      if (!response.ok) {
        throw new Error(await getErrorMessage(response));
      }
      
      await loadTasks();
    } catch (error) {
      showError(error);
    }
  }

  // Task deletion function
  async function deleteTask(taskId) {
    if (!confirm("¿Delete this task?")) return;
    
    try {
      const response = await fetch(`${tasksEndpoint}/${taskId}`, {
        method: "DELETE",
        headers: { 
          "Authorization": `Bearer ${token}`
        }
      });
      
      if (!response.ok) {
        throw new Error(await getErrorMessage(response));
      }
      
      await loadTasks();
    } catch (error) {
      showError(error);
    }
  }

  // Función para renderizar tareas
  function renderTasks(tasks) {
    const pendingTasksContainer = document.querySelector(".tareas-pendientes");
    const completedTasksContainer = document.querySelector(".tareas-terminadas");
    const completedCounter = document.querySelector("#cantidad-finalizadas");

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

  // Function to create task item
  function createTaskElement(task) {
    const taskElement = document.createElement("li");
    taskElement.className = "tarea";
    taskElement.dataset.id = task.id;

    const date = new Date(task.createdAt);
    date.setMinutes(date.getMinutes() + date.getTimezoneOffset());

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