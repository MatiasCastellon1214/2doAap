if (!localStorage.jwt || !localStorage.userId) {
  console.log('Token o ID faltante');
  location.replace("./index.html");
}

window.addEventListener('load', function () {
  const url = "http://localhost:3000";
  const urlTareas = `${url}/tasks`;
  const token = JSON.parse(localStorage.jwt); // Línea 9: token para autorización
  const userId = localStorage.userId; // Obtener userId de localStorage
  const userName = document.querySelector(".user-info p");

  const btnCerrarSesion = document.querySelector("#closeApp");
  const formCrearTarea = document.querySelector(".nueva-tarea");
  const nuevaTarea = document.querySelector("#nuevaTarea");

  obtenerNombreUsuario();
  consultarTareas();

  // Cerrar sesión
  btnCerrarSesion.addEventListener('click', function () {
    if (confirm("¿Desea cerrar sesión?")) {
      localStorage.clear();
      location.replace("./index.html");
    }
  });

  // Obtener nombre del usuario
  function obtenerNombreUsuario() {
    fetch(`${url}/users/${userId}`, {
      headers: { authorization: token }
    })
      .then(res => res.json())
      .then(data => {
        userName.textContent = data.firstName;
      })
      .catch(err => {
        console.log("Error al obtener usuario", err);
      });
  }

  // Consultar tareas del usuario
  function consultarTareas() {
    fetch(`${urlTareas}?userId=${userId}`, {
      headers: { authorization: token }
    })
      .then(res => res.json())
      .then(tareas => {
        renderizarTareas(tareas);
        botonesCambioEstado();
        botonBorrarTarea();
      })
      .catch(err => console.log("Error al consultar tareas", err));
  }

  // Crear nueva tarea
  formCrearTarea.addEventListener('submit', function (e) {
    e.preventDefault();

    const payload = {
      description: nuevaTarea.value.trim(),
      completed: false,
      createdAt: new Date().toISOString(),
      userId: userId
    };

    fetch(urlTareas, {
      method: "POST",
      body: JSON.stringify(payload),
      headers: {
        "Content-Type": "application/json",
        authorization: token
      }
    })
      .then(res => res.json())
      .then(() => consultarTareas());

    formCrearTarea.reset();
  });

  // Renderizar tareas
  function renderizarTareas(tareas) {
    const tareasPendientes = document.querySelector(".tareas-pendientes");
    const tareasTerminadas = document.querySelector(".tareas-terminadas");
    const numeroFinalizadas = document.querySelector("#cantidad-finalizadas");

    tareasPendientes.innerHTML = "";
    tareasTerminadas.innerHTML = "";

    let contador = 0;

    tareas.forEach(tarea => {
      let fecha = new Date(tarea.createdAt);
      const tareaHTML = tarea.completed
        ? `
        <li class="tarea">
          <div class="hecha"><i class="fa-regular fa-circle-check"></i></div>
          <div class="descripcion">
            <p class="nombre">${tarea.description}</p>
            <div class="cambios-estados">
              <button class="change incompleta" data-id="${tarea.id}"><i class="fa-solid fa-rotate-left"></i></button>
              <button class="borrar" data-id="${tarea.id}"><i class="fa-regular fa-trash-can"></i></button>
            </div>
          </div>
        </li>`
        : `
        <li class="tarea">
          <button class="change" data-id="${tarea.id}"><i class="fa-regular fa-circle"></i></button>
          <div class="descripcion">
            <p class="nombre">${tarea.description}</p>
            <p class="timestamp">${fecha.toLocaleDateString()}</p>
          </div>
        </li>`;

      if (tarea.completed) {
        contador++;
        tareasTerminadas.innerHTML += tareaHTML;
      } else {
        tareasPendientes.innerHTML += tareaHTML;
      }
    });

    numeroFinalizadas.textContent = contador;
  }

  // Cambiar estado de tarea
  function botonesCambioEstado() {
    const botones = document.querySelectorAll(".change");
    botones.forEach(btn => {
      btn.addEventListener("click", (e) => {
        const id = e.currentTarget.dataset.id;
        const tareaEsCompleta = e.currentTarget.classList.contains("incompleta");
        const payload = {
          completed: !tareaEsCompleta
        };

        fetch(`${urlTareas}/${id}`, {
          method: "PATCH",
          body: JSON.stringify(payload),
          headers: {
            "Content-Type": "application/json",
            authorization: token
          }
        }).then(() => consultarTareas());
      });
    });
  }

  // Eliminar tarea
  function botonBorrarTarea() {
    const botones = document.querySelectorAll(".borrar");
    botones.forEach(btn => {
      btn.addEventListener("click", (e) => {
        const id = e.currentTarget.dataset.id;
        fetch(`${urlTareas}/${id}`, {
          method: "DELETE",
          headers: { authorization: token }
        }).then(() => consultarTareas());
      });
    });
  }
});
