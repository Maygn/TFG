document.getElementById("addMusicBtn").addEventListener("click", function () {
    document.getElementById("fileInput").click();
});

document.getElementById("fileInput").addEventListener("change", async function (event) {
    const file = event.target.files[0]; // Obtener el archivo seleccionado

    if (!file) return;

    let nombreArchivo = prompt("Introduce el nombre del archivo:", file.name);
    if (!nombreArchivo) {
        nombreArchivo = file.name; // Si no se ingresa un nombre, usar el original
    }

    const token = localStorage.getItem("jwtToken");
    if (!token) {
        alert("No hay sesión iniciada. Inicia sesión para subir música.");
        return;
    }

    try {
        const formData = new FormData();
        formData.append("file", file);
        formData.append("nombre", nombreArchivo);

        const response = await fetch("http://localhost:8080/sonidos/subir", {
            method: "POST",
            body: formData,
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        console.log("Respuesta completa del servidor:", response);

        const mensaje = await response.text();
        console.log("Mensaje recibido:", mensaje);

        document.getElementById("mensaje").innerText = mensaje;
        document.getElementById("mensaje").style.color = response.ok ? "green" : "red";

        // Recargar la lista de sonidos después de subir uno nuevo
        if (response.ok) {
            cargarSonidos();
        }

    } catch (error) {
        console.error("Error al conectar con el servidor:", error);
        document.getElementById("mensaje").innerText = "Error al conectar con el servidor";
        document.getElementById("mensaje").style.color = "red";
    }
});

document.addEventListener("DOMContentLoaded", function () {
    const token = localStorage.getItem("jwtToken");

    if (!token) {
        document.getElementById("userInfo").innerText = "No hay sesión iniciada.";
        return;
    }

    try {
        const parts = token.split('.');
        if (parts.length !== 3) throw new Error("Token JWT no válido");

        const payload = JSON.parse(base64UrlDecode(parts[1]));
        console.log("Payload decodificado:", payload);

        const usuarioId = payload.Usuario;
        if (!usuarioId) throw new Error("No se pudo obtener el usuario del token");

        document.getElementById("userInfo").innerText = `Puedes subir música a la cuenta de: ${usuarioId}`;

        // Cargar la lista de sonidos del usuario
        cargarSonidos();

    } catch (error) {
        console.error("Error al decodificar el token JWT:", error);
        document.getElementById("userInfo").innerText = `Error al obtener el usuario: ${error.message}`;
    }
});

// Función para cargar los sonidos y añadir un botón para cada uno
async function cargarSonidos() {
    const token = localStorage.getItem("jwtToken");
    if (!token) {
        alert("No hay sesión iniciada.");
        return;
    }

    const parts = token.split('.');
    if (parts.length !== 3) throw new Error("Token JWT no válido");

    const payload = JSON.parse(base64UrlDecode(parts[1]));
    const usuarioId = payload.Usuario;
    if (!usuarioId) throw new Error("No se pudo obtener el usuario del token");

    try {
        const response = await fetch(`http://localhost:8080/sonidos/buscar/${usuarioId}`, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        const sonidos = await response.json();
        const tablaSonidos = document.getElementById("tablaSonidos");
        tablaSonidos.innerHTML = "<tr><th>Nombre</th><th>Reproducir</th><th>Acción</th></tr>";  // Limpiar tabla

        sonidos.forEach((sonido) => {
            const row = document.createElement("tr");

            const nombreCell = document.createElement("td");
            nombreCell.textContent = sonido.nombre;
            row.appendChild(nombreCell);

            const reproducirBtn = document.createElement("button");
            reproducirBtn.textContent = "Reproducir";
            reproducirBtn.addEventListener("click", function () {
                const audio = new Audio(`http://localhost:8080/sonidos/descargar/${sonido.id}`);  // Usar /descargar/{id}
                audio.play();
            });

            const reproducirCell = document.createElement("td");
            reproducirCell.appendChild(reproducirBtn);
            row.appendChild(reproducirCell);

            // Agregar botón de eliminar
            const eliminarBtn = document.createElement("button");
            eliminarBtn.textContent = "🗑️ Borrar";
            eliminarBtn.addEventListener("click", function () {
                borrarSonido(sonido.id);
            });

            const eliminarCell = document.createElement("td");
            eliminarCell.appendChild(eliminarBtn);
            row.appendChild(eliminarCell);

            tablaSonidos.appendChild(row);
        });
    } catch (error) {
        console.error("Error al cargar los sonidos:", error);
    }
}



// 🔹 Función para extraer el usuario desde el JWT
function extraerUsuarioDesdeJWT(token) {
    try {
        const payloadBase64 = token.split('.')[1];
        const payloadJson = atob(payloadBase64);
        const payload = JSON.parse(payloadJson);
        return payload.Usuario;
    } catch (e) {
        console.error("Error al decodificar el token:", e);
        return null;
    }
}

// 🔹 Función para mostrar los sonidos en la tabla
function mostrarSonidos(sonidos) {
    const tabla = document.getElementById("tablaSonidos");
    tabla.innerHTML = `
        <tr>
            <th>Nombre</th>
            <th>Reproducir</th>
            <th>Acción</th>
        </tr>
    `;

    sonidos.forEach(sonido => {
        const fila = document.createElement("tr");
        fila.innerHTML = `
            <td>${sonido.nombre}</td>
            <td>
                <audio controls>
                    <source src="http://localhost:8080/sonidos/buscar/${sonido.id}" type="audio/mpeg">
                    Tu navegador no soporta el elemento de audio.
                </audio>
            </td>
            <td>
                <button class="delete-btn" onclick="borrarSonido(${sonido.id})">🗑️ Borrar</button>
            </td>
        `;
        tabla.appendChild(fila);
    });
}


// 🔹 Función para decodificar base64Url (como en JWT)
function base64UrlDecode(str) {
    str = str.replace(/-/g, '+').replace(/_/g, '/'); 
    while (str.length % 4 !== 0) {
        str += "="; 
    }
    return atob(str);
}

async function borrarSonido(id) {
    const confirmar = confirm("¿Estás seguro de que deseas eliminar este sonido?");
    if (!confirmar) return;

    const token = localStorage.getItem("jwtToken");
    if (!token) {
        alert("No hay sesión iniciada. Inicia sesión para borrar música.");
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/sonidos/borrar/${id}`, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (response.ok) {
            alert("Sonido eliminado correctamente.");
            cargarSonidos(); // Recargar lista de sonidos
        } else if (response.status === 404) {
            alert("El sonido no existe.");
        } else {
            alert("Error al borrar el sonido.");
        }
    } catch (error) {
        console.error("Error al borrar sonido:", error);
        alert("Error al conectar con el servidor.");
    }
}

document.getElementById("deleteAllBtn").addEventListener("click", async function () {
    const confirmar = confirm("¿Estás seguro de que deseas borrar todos tus sonidos?");
    if (!confirmar) return;

    const token = localStorage.getItem("jwtToken");
    if (!token) {
        alert("No hay sesión iniciada. Inicia sesión para borrar todos los sonidos.");
        return;
    }

    try {
        const parts = token.split('.');
        if (parts.length !== 3) throw new Error("Token JWT no válido");

        const payload = JSON.parse(base64UrlDecode(parts[1]));
        const usuarioId = payload.Usuario;
        if (!usuarioId) throw new Error("No se pudo obtener el usuario del token");

        // Realizar la petición al backend para borrar todos los sonidos
        const response = await fetch(`http://localhost:8080/sonidos/borrarTodo/${usuarioId}`, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (response.ok) {
            alert("Todos los sonidos han sido eliminados.");
            cargarSonidos(); // Recargar lista de sonidos
        } else {
            alert("Error al eliminar los sonidos.");
        }
    } catch (error) {
        console.error("Error al borrar todos los sonidos:", error);
        alert("Error al conectar con el servidor.");
    }
});

