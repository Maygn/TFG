
document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem("jwtToken");
    const usuarioId = extraerUsuarioDesdeJWT(token); // Si necesitas pasar el usuarioId, agrega aquí la lógica para obtenerlo.
    cargarSonidos(usuarioId);  // Llamar a la función para cargar los sonidos al cargar la página
});


document.getElementById("addMusicBtn").addEventListener("click", function () {
    document.getElementById("fileInput").click();
});

document.getElementById("fileInput").addEventListener("change", async function (event) {
    const file = event.target.files[0]; 
    if (!file) return; 

    let nombreArchivo = prompt("Introduce el nombre del archivo:", file.name);
    if (!nombreArchivo) {
        nombreArchivo = file.name; 
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
        const mensaje = await response.text();

        document.getElementById("mensaje").innerText = mensaje;
        document.getElementById("mensaje").style.color = response.ok ? "green" : "red";

        if (response.ok) {
            cargarSonidos();
        }

    } catch (error) {
        console.error("Error al conectar con el servidor:", error);
        document.getElementById("mensaje").innerText = "Error al conectar con el servidor";
        document.getElementById("mensaje").style.color = "red";
    }
});

async function cargarSonidos() {
    debugger
    try {
        const token = localStorage.getItem("jwtToken");

        if (!token) {
            alert("No hay sesión iniciada. Inicia sesión para ver los sonidos.");
            return;
        }

        const response = await fetch(`http://localhost:8080/sonidos/buscarLista`, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        const sonidos = await response.json();

        const tablaSonidos = document.getElementById("tablaSonidos");

        tablaSonidos.innerHTML = "<tr><th>Nombre</th><th>Reproducir</th><th>Acción</th></tr>";

        sonidos.forEach((sonido) => {
            const row = document.createElement("tr");

            const nombreCell = document.createElement("td");
            nombreCell.textContent = sonido.nombre; 
            row.appendChild(nombreCell); 

            const reproducirBtn = document.createElement("button");
            reproducirBtn.textContent = "Reproducir";

            reproducirBtn.addEventListener("click", function () {
                // Reproducir el sonido con el token
                const token = localStorage.getItem("jwtToken");
                if (!token) {
                    alert("No hay sesión iniciada. Inicia sesión para escuchar el sonido.");
                    return;
                }

                const audioUrl = `http://localhost:8080/sonidos/descargar/${sonido.id}`;
                fetch(audioUrl, {
                    method: "GET",
                    headers: {
                        "Authorization": `Bearer ${token}`
                    }
                })
                .then(response => {
                    if (response.ok) {
                        response.blob().then(blob => {
                            const audio = new Audio(URL.createObjectURL(blob));
                            audio.play();
                        });
                    } else {
                        alert("No tienes permiso para reproducir este sonido.");
                    }
                })
                .catch(error => {
                    console.error("Error al cargar el audio:", error);
                });
            });

            const reproducirCell = document.createElement("td");
            reproducirCell.appendChild(reproducirBtn);
            row.appendChild(reproducirCell);

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

async function borrarSonido(id) {
    const confirmar = confirm("¿Estás seguro de que deseas eliminar este sonido?");
    if (!confirmar) return;
    const token = localStorage.getItem("jwtToken");
    if (!token) {
        alert("No hay sesión iniciada. Inicia sesión para borrar música.");
        return;
    }

    try {
        debugger 
        const response = await fetch(`http://localhost:8080/sonidos/borrar/${id}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                "Authorization": token
            }
        });
        
        if (response.ok) {
            alert("Sonido eliminado correctamente.");
            cargarSonidos(); 
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
        const usuarioId = extraerUsuarioDesdeJWT(token);
    if (!usuarioId) {
        alert("No se pudo obtener el usuario del token.");
        return;
    }

        const response = await fetch("http://localhost:8080/sonidos/borrarTodo", {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (response.ok) {
            alert("Todos los sonidos han sido eliminados.");
            cargarSonidos();
        } else {
            alert("Error al eliminar los sonidos.");
        }
    } catch (error) {
        console.error("Error al borrar todos los sonidos:", error);
        alert("Error al conectar con el servidor.");
    }
});

function obtenerUsuarioDesdeToken() {
    const token = localStorage.getItem("jwtToken");
    if (!token) {
        alert("No hay sesión iniciada.");
        return null;
    }

    const parts = token.split('.');
    if (parts.length !== 3) {
        console.error("Token JWT no válido");
        return null;
    }

    try {
        const payload = JSON.parse(base64UrlDecode(parts[1]));
        const usuarioId = payload.Usuario;
        if (!usuarioId) {
            console.error("No se pudo obtener el usuario del token");
            return null;
        }
        return usuarioId;
    } catch (error) {
        console.error("Error al decodificar el token JWT:", error);
        return null;
    }
}
function extraerUsuarioDesdeJWT(token) {
    const usuarioId = obtenerUsuarioDesdeToken();
    if (!usuarioId) return null;
    return usuarioId;
}

function base64UrlDecode(str) {
    str = str.replace(/-/g, '+').replace(/_/g, '/'); 
    while (str.length % 4 !== 0) {
        str += "="; 
    }
    return atob(str);
}