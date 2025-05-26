window.addEventListener('load', function () {
    /* ---------------------- We obtain global variables ---------------------- */
    const form = document.forms[0];
    const email = document.querySelector("#inputEmail");
    const password = document.getElementById("inputPassword");
    const url = "http://localhost:8081/api/auth/login";

    /* -------------------------------------------------------------------------- */
    /*            FUNCTION 1: We listen to the submit and prepare the shipment.           */
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
    /*                     FUNCTION 2: Simulate login with JSON Server [GET].         */
    /* -------------------------------------------------------------------------- */
    function realizarLogin(payload) {
        fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Error in the credentials");
            }
            return response.json();
        })
        .then(data => {
            // Save the actual JWT returned by your backend
            localStorage.setItem("jwt", data.token);
            localStorage.setItem("userId", data.id); // Only if you return the id in the response
    
            console.log("Successful login, redirecting...");
            location.replace("./mis-tareas.html");
        })
        .catch(err => {
            console.error("Login error:", err);
            alert("Unable to log in");
        });
    }
    
});