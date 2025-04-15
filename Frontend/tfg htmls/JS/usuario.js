async function cambiarClave() {
    const token = localStorage.getItem("jwtToken");
    if (!token) {
        alert("No se encontró el token de autenticación. Por favor, inicia sesión nuevamente.");
        return;
    }

    const contrasenaAct = document.getElementById("contrasenaAct")?.value;
    const contrasenaNueva = document.getElementById("contrasenaNueva")?.value;

    if (!contrasenaAct || !contrasenaNueva) {
        alert("Por favor, llena todos los campos.");
        return;
    }

    const formData = new URLSearchParams();
    formData.append("contrasenaAct", contrasenaAct);
    formData.append("contrasenaNueva", contrasenaNueva);

    try {
        const response = await fetch("http://localhost:8081/usuarios/cambiarClave", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                "Authorization": token
            },
            body: formData
        });

        const result = await response.text();

        if (!response.ok) {
            alert("Error del servidor al cambiar la contraseña: " + result);
            return;
        }

        alert(result);

        // Limpiar campos si el cambio fue exitoso
        document.getElementById("contrasenaAct").value = "";
        document.getElementById("contrasenaNueva").value = "";

    } catch (error) {
        console.error("Error al cambiar la contraseña:", error);
        alert("Ocurrió un error de conexión al intentar cambiar la contraseña.");
    }
}



async function borrarUsuario() {
    const token = localStorage.getItem("jwtToken");
    if (!token) {
        alert("No se encontró el token de autenticación. Por favor, inicia sesión nuevamente.");
        return;
    }

    const contrasena = document.getElementById("contrasenaBorrar")?.value;
    if (!contrasena) {
        alert("Debes introducir tu contraseña.");
        return;
    }

    const formData = new URLSearchParams();
    formData.append("contrasena", contrasena);

    try {
        // Eliminar sonidos del usuario primero
        const sonidosResponse = await fetch("http://localhost:8080/sonidos/borrarTodo", {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!sonidosResponse.ok) {
            alert("Error al eliminar los sonidos.");
            return;
        }

        // Eliminar música del usuario
        const musicaResponse = await fetch("http://localhost:8090/musica/borrar", {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!musicaResponse.ok) {
            alert("Error al eliminar la música.");
            return;
        }

        // Eliminar usuario
        const response = await fetch("http://localhost:8081/usuarios/borrar", {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({ contrasena }) // Pasar la contraseña correctamente en el body
        });

        const result = await response.text();

        if (!response.ok) {
            alert("Error al borrar el usuario: " + result);
            return;
        }

        alert(result);

        // Cerrar sesión tras eliminación y redirigir
        localStorage.removeItem("jwtToken");
        window.location.href = "http://127.0.0.1:5500/HTML/welcome.html"; // Redirigir a la página principal

    } catch (error) {
        console.error("Error al borrar el usuario:", error);
        alert("No se pudo borrar la cuenta. Inténtalo de nuevo.");
    }
}

document.addEventListener("DOMContentLoaded", async () => {
    debugger
    const token = localStorage.getItem("jwtToken");
    const userInfoElement = document.getElementById("userInfo");
  
    if (!token) {
        userInfoElement.innerText = "No hay sesión iniciada.";
        alert("No se encontró el token de sesión.");
        return;
    }
  
    try {
        const usuarioId = extraerUsuarioDesdeToken(token);
        if (!usuarioId) throw new Error("No se pudo obtener el usuario.");
  
        // Obtener datos del usuario desde la API
        const usuarioData = await obtenerUsuarioDesdeAPI(usuarioId,token);
        if (!usuarioData) {
            userInfoElement.innerText = "Error al obtener los datos del usuario.";
            alert("Error al obtener los datos del usuario.");
            return;
        }
        userInfoElement.innerText = `${usuarioData?.usuario || usuarioId}`;
  
    } catch (error) {
        console.error("Error:", error);
        userInfoElement.innerText = "Error al obtener el usuario.";
        alert("Error al obtener el usuario.");
    }
});
  
  /**
  * Decodifica un token JWT y extrae el campo 'Usuario'.
  */
  function extraerUsuarioDesdeToken(token) {
    debugger
    try {
        const [header, payload, signature] = token.split(".");
        if (!header || !payload || !signature) throw new Error("Token JWT no válido");
  
        const decodedPayload = JSON.parse(base64UrlDecode(payload));
        return decodedPayload.Usuario;
    } catch (error) {
        console.error("Error al decodificar el token JWT:", error);
        return null;
    }
  }
  
  /**
  * Obtiene los datos del usuario desde la API.
  */
  async function obtenerUsuarioDesdeAPI(usuarioId, token) {
    const url = `http://localhost:8081/usuarios/obtener/usuario?nombreUsuario=${encodeURIComponent(usuarioId)}`;

    try {
        const response = await fetch(url, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) throw new Error(`Error en la solicitud: ${response.statusText}`);

        return await response.text(); // El backend devuelve el token como texto plano

    } catch (error) {
        console.error("Error al obtener los datos del usuario:", error);
        return null;
    }
}

  /**
  * Decodifica una cadena base64-url.
  */
  function base64UrlDecode(str) {
    str = str.replace(/-/g, "+").replace(/_/g, "/");
    return atob(str.padEnd(str.length + (4 - (str.length % 4)) % 4, "="));
  }