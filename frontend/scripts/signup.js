window.addEventListener("load", function () {
  const form = document.forms[0];
  const name = document.querySelector("#inputNombre");
  const lastName = document.querySelector("#inputApellido");
  const email = document.querySelector("#inputEmail");
  const password = document.querySelector("#inputPassword");
  const repeatPassword = document.querySelector("#inputPasswordRepetida");
  const url = "http://localhost:8081/api/auth";

  form.addEventListener("submit", function (event) {
    event.preventDefault();

    const nombreValido = validarTexto(name.value);
    const apellidoValido = validarTexto(lastName.value);

    if (compararContrasenias(password.value, repeatPassword.value)) {
      if (!nombreValido || !apellidoValido) {
        alert("Incorrect first and/or last name formato");
      } else if (!validarEmail(email.value)) {
        alert("Enter a valid email address");
      } else if (!validarContrasenia(password.value)) {
        alert("The password must have 8 characters, one uppercase, one lowercase, one number and one special character.");
      } else {
        const payload = {
          firstName: normalizarTexto(name.value),
          lastName: normalizarTexto(lastName.value),
          email: normalizarEmail(email.value),
          password: password.value
        };

        registrarUsuario(payload);
        form.reset();
      }
    } else {
      alert("Passwords do not match");
    }
  });

  function registrarUsuario(payload) {
    fetch(`${url}/register`, {
      method: "POST",
      body: JSON.stringify(payload),
      headers: { 
        'Content-Type': 'application/json'
      }
    })
    .then(response => {
      if (!response.ok) {
        return response.json().then(err => { throw err; });
      }
      return response.json();
    })
    .then(data => {
      console.log("Registro exitoso:", data);
      // Save JWT token and user data
      localStorage.setItem("jwt", data.token);
      localStorage.setItem("userData", JSON.stringify({
        id: data.userId,
        email: data.email,
        firstName: data.firstName,
        lastName: data.lastName
      }));
      
      // Redirect to dashboard or task page
      window.location.href = './mis-tareas.html';
    })
    .catch(error => {
      console.error("Error in registration:", error);
      if (error.message && error.message.includes("email")) {
        mostrarError("The email is already registered");
      } else {
        mostrarError(error.message || "There was an error while registering");
      }
    });
  }

});
