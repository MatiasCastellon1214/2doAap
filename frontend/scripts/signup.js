window.addEventListener("load", function () {
  const form = document.forms[0];
  const name = document.querySelector("#inputNombre");
  const lastName = document.querySelector("#inputApellido");
  const email = document.querySelector("#inputEmail");
  const password = document.querySelector("#inputPassword");
  const repeatPassword = document.querySelector("#inputPasswordRepetida");
  const url = "http://localhost:3000";

  form.addEventListener("submit", function (event) {
    event.preventDefault();

    const nombreValido = validarTexto(name.value);
    const apellidoValido = validarTexto(lastName.value);

    if (compararContrasenias(password.value, repeatPassword.value)) {
      if (!nombreValido || !apellidoValido) {
        alert("Formato de nombre y/o apellido incorrecto");
      } else if (!validarEmail(email.value)) {
        alert("Ingrese una dirección de correo válida");
      } else if (!validarContrasenia(password.value)) {
        alert("La contraseña debe tener 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial");
      } else {
        const payload = {
          firstName: normalizarTexto(name.value),
          lastName: normalizarTexto(lastName.value),
          email: normalizarEmail(email.value),
          password: password.value
        };

        verificarYRegistrar(payload);
        form.reset();
      }
    } else {
      alert("Las contraseñas no coinciden");
    }
  });

  function verificarYRegistrar(payload) {
    fetch(`${url}/users?email=${encodeURIComponent(payload.email)}`)
      .then(res => res.json())
      .then(data => {
        if (data.length > 0) {
          alert("El usuario ya está registrado");
          throw new Error("Usuario existente");
        }

        // Agregar un campo fake jwt si querés mantener compatibilidad
        payload.jwt = "fake-jwt-" + Date.now();

        // Crear el usuario
        return fetch(`${url}/users`, {
          method: "POST",
          body: JSON.stringify(payload),
          headers: { 'Content-Type': 'application/json' }
        });
      })
      .then(res => res.json())
      .then(data => {
        console.log("Usuario creado:", data);
        localStorage.setItem("jwt", JSON.stringify(data.jwt));
        localStorage.setItem("userId", data.id);
        location.replace('./mis-tareas.html');
      })
      .catch(err => {
        console.error("Error al registrar:", err);
        alert("Hubo un error al registrarse");
      });
  }
});
