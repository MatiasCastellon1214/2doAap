window.addEventListener('load', function () {
    /* ---------------------- obtenemos variables globales ---------------------- */
    const form = document.forms[0];
    const email = document.querySelector("#inputEmail");
    const password = document.getElementById("inputPassword");
    const url = "http://localhost:3000";

    /* -------------------------------------------------------------------------- */
    /*            FUNCIÓN 1: Escuchamos el submit y preparamos el envío           */
    /* -------------------------------------------------------------------------- */
    form.addEventListener('submit', function (event) {
        event.preventDefault();

        const payload = {
            email: email.value,
            password: password.value
        };

        console.log(payload);

        realizarLogin(payload);
        form.reset();
    });

    /* -------------------------------------------------------------------------- */
    /*                     FUNCIÓN 2: Simular login con JSON Server [GET]         */
    /* -------------------------------------------------------------------------- */
    function realizarLogin(payload) {
        console.log("Consultando usuario en JSON Server...");

        fetch(`${url}/users?email=${encodeURIComponent(payload.email)}&password=${encodeURIComponent(payload.password)}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error("Error al acceder a /users");
                }
                return response.json();
            })
            .then(data => {
                if (data.length === 0) {
                    alert("Usuario o contraseña incorrectos");
                    throw new Error("Credenciales inválidas");
                }

                const user = data[0];

                // Guardamos "jwt" y userId en localStorage para seguir usando tu app como antes
                localStorage.setItem("jwt", JSON.stringify(user.jwt || "fake-jwt"));
                localStorage.setItem("userId", user.id);

                console.log("Login exitoso, redirigiendo...");
                location.replace("./mis-tareas.html");
            })
            .catch(err => {
                console.error("Error en el login:", err);
                alert("No se pudo iniciar sesión");
            });
    }
});
