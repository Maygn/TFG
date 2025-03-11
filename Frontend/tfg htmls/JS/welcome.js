document.addEventListener("DOMContentLoaded", function () {
    const formTitle = document.getElementById("formTitle");
    const formulario = document.getElementById("formulario");
    const registroBtn = document.getElementById("registroBtn");
    const volverLoginBtn = document.getElementById("volverLoginBtn");
    let esRegistro = false;

    // Cambiar formulario a registro
    registroBtn.addEventListener("click", function () {
        formTitle.innerText = "Regístrate en Bard’s Playlist";
        esRegistro = true;

        formulario.innerHTML = `
            <div class="mb-3">
                <label for="username" class="form-label">Usuario</label>
                <input type="text" class="form-control mx-auto" id="username" name="usuario" placeholder="Introduce tu usuario">
            </div>
            <div class="mb-3">
                <label for="password" class="form-label">Contraseña</label>
                <input type="password" class="form-control mx-auto" id="password" name="contrasena" placeholder="Introduce tu contraseña">
            </div>
            <button type="submit" class="btn">Regístrate</button>
        `;

        registroBtn.style.display = "none";
        volverLoginBtn.style.display = "inline-block";
    });

    // Volver al formulario de inicio de sesión
    volverLoginBtn.addEventListener("click", function () {
        formTitle.innerText = "Inicia sesión en Bard’s Playlist";
        esRegistro = false;

        formulario.innerHTML = `
            <div class="mb-3">
                <label for="usuario" class="form-label">Usuario</label>
                <input type="text" class="form-control mx-auto" id="usuario" name="usuario" placeholder="Introduce tu usuario">
            </div>
            <div class="mb-3">
                <label for="password" class="form-label">Contraseña</label>
                <input type="password" class="form-control mx-auto" id="password" name="contrasena" placeholder="Introduce tu contraseña">
            </div>
            <button type="submit" class="btn">Iniciar sesión</button>
            <p class="mt-3">O bien...</p>
        `;

        registroBtn.style.display = "inline-block";
        volverLoginBtn.style.display = "none";
    });

    // Manejar el envío del formulario
    formulario.addEventListener("submit", async function (event) {
        event.preventDefault();

        const formData = new FormData(formulario);
        const formParams = new URLSearchParams(formData);

        // Asegurarse de que el campo 'usuario' o 'username' esté disponible dependiendo del formulario
        const usuarioElement = esRegistro ? document.getElementById("username") : document.getElementById("usuario");
        if (!usuarioElement) {
            console.error("El campo de usuario no está disponible.");
            return; // Detener el flujo si el campo no se encuentra
        }

        const cuenta = usuarioElement.value;  // Obtener el valor del usuario

        // Definir la URL y el método de la petición
        let url, options;
        if (esRegistro) {
            url = "http://localhost:8081/usuarios/guardar";
            options = {
                method: "POST",
                body: formParams.toString(),
                headers: { "Content-Type": "application/x-www-form-urlencoded" }
            };
        } else {
            url = `http://localhost:8081/usuarios/verificar?${formParams.toString()}`;
            options = {
                method: "GET",
                headers: { "Content-Type": "application/x-www-form-urlencoded" }
            };
        }

        const response = await fetch(url, options);
        const mensaje = await response.text();
        
        console.log("Mensaje recibido del servidor:", mensaje);
        debugger
        if (response.ok === true) {
            debugger
            // Si la respuesta es 'OK', entonces obtener el token llamando a obtenerUsuario
            const tokenResponse = await fetch("http://localhost:8081/usuarios/obtener/" + encodeURIComponent(cuenta));
            const token = await tokenResponse.text();  // El token es devuelto aquí
        
            console.log("Token recibido:", token);  // Verifica que el token es el que esperas
        
            // Eliminar el token anterior si existe
            localStorage.removeItem("jwtToken");
        
            // Guardar el nuevo token en el almacenamiento local
            localStorage.setItem("jwtToken", token);
        
            // Redirigir a la página principal después de iniciar sesión
            window.location.href = "http://127.0.0.1:5500/Frontend/tfg%20htmls/HTML/Pagina_principal.html";
        } else {
            document.getElementById("mensaje").innerText = mensaje;
            document.getElementById("mensaje").style.color = "red";
        }
    });
});
