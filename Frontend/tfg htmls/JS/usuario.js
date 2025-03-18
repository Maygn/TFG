async function cambiarClave() {
    const token = localStorage.getItem("jwtToken");
    const contrasenaAct = document.getElementById("contrasenaAct").value;  // Captura el valor actual
    const contrasenaNueva = document.getElementById("contrasenaNueva").value;

    if (!contrasenaAct || !contrasenaNueva) {
        alert("Por favor, llena todos los campos.");
        return;
    }

    const formData = new URLSearchParams();
    formData.append("token", token);
    formData.append("contrasenaAct", contrasenaAct);  // Ahora se envía
    formData.append("contrasenaNueva", contrasenaNueva);

    try {
        const response = await fetch("http://localhost:8081/usuarios/cambiarClave", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: formData
        });

        const result = await response.text();
        alert(result);
    } catch (error) {
        console.error("Error al cambiar la contraseña:", error);
        alert("Ocurrió un error. Inténtalo de nuevo.");
    }
}



async function cambiarClave() {
    const token = localStorage.getItem("jwtToken");
    const contrasenaAct = document.getElementById("contrasenaAct").value;
    const contrasenaNueva = document.getElementById("contrasenaNueva").value;

    if (!contrasenaAct || !contrasenaNueva) {
        alert("Por favor, llena todos los campos.");
        return;
    }

    const formData = new URLSearchParams();
    formData.append("token", token);
    formData.append("contrasenaAct", contrasenaAct);
    formData.append("contrasenaNueva", contrasenaNueva);

    try {
        const response = await fetch("http://localhost:8081/usuarios/cambiarClave", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: formData
        });

        const result = await response.text();
        alert(result);

        if (response.ok) {
            // Opcional: limpiar campos tras el cambio exitoso
            document.getElementById("contrasenaAct").value = "";
            document.getElementById("contrasenaNueva").value = "";
        }

    } catch (error) {
        console.error("Error al cambiar la contraseña:", error);
        alert("Ocurrió un error. Inténtalo de nuevo.");
    }
}


async function borrarUsuario() {
    const token = localStorage.getItem("jwtToken");
    const contrasena = document.getElementById("contrasenaBorrar").value; // Obtener contraseña del modal

    if (!contrasena) {
        alert("Debes introducir tu contraseña.");
        return;
    }

    const formData = new URLSearchParams();
    formData.append("contrasena", contrasena);

    try {
        // Eliminar sonidos del usuario primero
        const sonidoResponse = await fetch(`http://localhost:8080/sonidos/borrarTodo?${token}`, {
            method: "DELETE"
        });

        if (!sonidoResponse.ok) {
            alert("Hubo un error al eliminar los sonidos.");
            return;
        }

        // Eliminar música del usuario
        const musicaResponse = await fetch(`http://localhost:8090/musica/borrar?token=${token}`, {
            method: "DELETE"
        });

        if (!musicaResponse.ok) {
            alert("Hubo un error al eliminar la música.");
            return;
        }

        // Eliminar usuario
        const response = await fetch(`http://localhost:8081/usuarios/borrar/usuario?token=${token}&contrasena=${contrasena}`, {
            method: "DELETE"
        });

        const result = await response.text();
        alert(result);

        if (response.ok) {
            localStorage.removeItem("jwtToken"); // Cerrar sesión tras eliminación
            window.location.href = "http://127.0.0.1:5500/HTML/welcome.html"; // Redirigir a la página principal
        }

    } catch (error) {
        console.error("Error al borrar el usuario:", error);
        alert("No se pudo borrar la cuenta. Inténtalo de nuevo.");
    }
}





function cambiarImagen(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            document.getElementById('profilePic').src = e.target.result;
        }
        reader.readAsDataURL(file);
    }
}