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
    formData.append("contrasenaAct", contrasenaAct);
    formData.append("contrasenaNueva", contrasenaNueva);

    try {
        const response = await fetch("http://localhost:8081/usuarios/cambiarClave", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                "Authorization": token // Token en el header, no en el body
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
        await fetch("http://localhost:8080/sonidos/borrarTodo", {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });



        // Eliminar música del usuario
        await fetch(`http://localhost:8090/musica/borrar`, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

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


document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("jwtToken");
    const userInfoElement = document.getElementById("userInfo");
  
    if (!token) {
        userInfoElement.innerText = "No hay sesión iniciada.";
        return;
    }
  
    try {
        const usuarioId = extraerUsuarioDesdeToken(token);
        if (!usuarioId) throw new Error("No se pudo obtener el usuario.");
  
        // Obtener datos del usuario desde la API
        const usuarioData = await obtenerUsuarioDesdeAPI(usuarioId);
        userInfoElement.innerText = `${usuarioData?.usuario || usuarioId}`;
  
    } catch (error) {
        console.error("Error:", error);
        userInfoElement.innerText = "Error al obtener el usuario.";
    }
  });
  
  /**
  * Decodifica un token JWT y extrae el campo 'Usuario'.
  */
  function extraerUsuarioDesdeToken(token) {
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
    const url = `http://localhost:8081/usuarios/obtener/usuario`;
  
    try {
        const response = await fetch(url, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            }
        });
  
        if (!response.ok) throw new Error(`Error en la solicitud: ${response.statusText}`);
  
        return await response.text(); // En tu código, el backend devuelve el token como texto plano
  
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