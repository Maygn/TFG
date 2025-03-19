document.addEventListener("DOMContentLoaded", function () { //Esto sirve para que primero se cargue el html y luego el javascrip, para que no de errores. 
    const formTitle = document.getElementById("TituloFormurario"); //Pilla el div del titulo para cambiarlo posteriormente
    const formulario = document.getElementById("formulario"); //..Para el formulario
    const registroBtn = document.getElementById("IrRegistroBoton"); //...Para el boton que lleva al formulario de registro
    const volverLoginBtn = document.getElementById("IrLoginBoton"); //...Para el boton que lleva al formulario de login
    let esRegistro = false; //Variable para saber si el formulario esta en registro o no

   // Manejar el envío del formulario
formulario.addEventListener("submit", async function (event) {
    event.preventDefault(); // Se evita la recarga de la página

    const formData = new FormData(formulario);
    const formParams = new URLSearchParams(formData);

    const usuarioElement = document.getElementById("usuario");
    const cuenta = usuarioElement.value; // Obtener el usuario
    
    if (esRegistro) {
        debugger
    // Obtener el estado del checkbox de admin
    const adminCheckbox = document.getElementById("adminCheckbox");
    const esAdmin = adminCheckbox.checked; // true si está marcado, false si no
    // Agregar el valor de admin al formParams
    formParams.append("admin", esAdmin);   
    localStorage.setItem("esAdmin", esAdmin);

}

    // Definir la URL y el método de la petición
    let url, options;
    if (esRegistro) {
        url = "http://localhost:8081/usuarios/guardar";
        console.log("Datos enviados:", formParams.toString());

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
    if (response.ok === true) {
        // Si el registro fue exitoso, obtener el token
        const tokenResponse = await fetch(`http://localhost:8081/usuarios/obtener/usuario?nombreUsuario=${encodeURIComponent(cuenta)}`);
        const token = await tokenResponse.text(); 
    
        console.log("Token recibido:", token);
    
        // Guardar el nuevo token en el almacenamiento local
        localStorage.removeItem("jwtToken");
        localStorage.setItem("jwtToken", token);
    
        // Enviar la petición para asignar el JSON predeterminado
        const defJson = JSON.stringify({
        });
    
        const jsonResponse = await fetch("http://localhost:8090/musica/nuevo", {
            method: "POST",
            headers: { 
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({ token, defJson })
        });
    
        const mensajeJson = await jsonResponse.text();
        console.log("Respuesta al asignar JSON:", mensajeJson);
    
        // Redirigir a la página principal
        window.location.href = "http://127.0.0.1:5500/HTML/Pagina_principal.html";
    
    } else {
        document.getElementById("mensaje").innerText = mensaje;
        document.getElementById("mensaje").style.color = "red";
    }
});

// Cambiar formulario a registro
registroBtn.addEventListener("click", function () {
    formTitle.innerText = "Regístrate en Bard’s Playlist";
    esRegistro = true;

    formulario.innerHTML = `
        <div class="mb-3">
            <label for="usuario" class="form-label">Usuario</label>
            <input type="text" class="form-control mx-auto" id="usuario" name="usuario" placeholder="Introduce tu usuario">
        </div>
        <div class="mb-3">
            <label for="password" class="form-label">Contraseña</label>
            <input type="password" class="form-control mx-auto" id="password" name="contrasena" placeholder="Introduce tu contraseña">
        </div>
        <div class="mb-3" id="adminKeyContainer" style="display: none;">
            <label for="claveAdmin" class="form-label">Clave de administrador</label>
            <input type="password" class="form-control mx-auto" id="claveAdmin" name="claveAdmin" placeholder="Introduce la clave de administrador">
        </div>
        <div class="mb-3">
            <label class="checkbox-medieval">
                <input type="checkbox" id="adminCheckbox">
                <span class="checkmark"></span>
                ¿Cuenta de administrador?
            </label>
        </div>
        <button type="submit" class="btn">Regístrate</button>
    `;

    // Evento para mostrar/ocultar la clave de administrador
    document.getElementById("adminCheckbox").addEventListener("change", function () {
        const adminKeyContainer = document.getElementById("adminKeyContainer");
        adminKeyContainer.style.display = this.checked ? "block" : "none";
    });

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





    





























});
