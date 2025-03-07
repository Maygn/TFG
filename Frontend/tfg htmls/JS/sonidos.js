//Hacer un boton bonito para el fileinput que sube los archivos mp3
document.getElementById("addMusicBtn").addEventListener("click", function () {
    document.getElementById("fileInput").click();
});

//SUBIR ARCHIVOS

document.getElementById("fileInput").addEventListener("change", async function (event) {
    const file = event.target.files[0]; // Obtener el archivo seleccionado

    if (!file) return; //cortar proceso si se intenta subir vacio

    let nombreArchivo = prompt("Introduce el nombre del archivo:", file.name);
    if (!nombreArchivo) {
        nombreArchivo = file.name; // Si no se pone un nombre, usar el original
    }
//comprobar si hay sesion iniciada
    const token = localStorage.getItem("jwtToken");
    if (!token) {
        alert("No hay sesión iniciada. Inicia sesión para subir música.");
        return;
    }

    try {//Crea un objeto formdata con el archivo y su nombre
        const formData = new FormData();
        formData.append("file", file);
        formData.append("nombre", nombreArchivo);
//manda peticion al servidor: en header lleva el token de usuario y en body el archivo
        const response = await fetch("http://localhost:8080/ /subir", {
            method: "POST",
            body: formData,
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

      //mostrar mensaje al usuario con colores según si ha habido error o no
        document.getElementById("mensaje").innerText = mensaje;
        document.getElementById("mensaje").style.color = response.ok ? "green" : "red";

        // Recargar la lista de sonidos después de subir uno nuevo
        if (response.ok) {
            cargarSonidos();
        }
//si ha fallado algo se manda error a la consola y al usuario
    } catch (error) {
        console.error("Error al conectar con el servidor:", error);
        document.getElementById("mensaje").innerText = "Error al conectar con el servidor";
        document.getElementById("mensaje").style.color = "red";
    }
});

// MOSTRAR LOS SONIDOS DEL USUARIO PARA MODIFICAR LISTA DE ELLOS
document.addEventListener("DOMContentLoaded", function () {
    const usuarioId = obtenerUsuarioDesdeToken();
    if (!usuarioId) return;

    document.getElementById("userInfo").innerText = `Puedes subir música a la cuenta de: ${usuarioId}`;
    cargarSonidos(usuarioId);  // Llamar a cargarSonidos pasando el usuarioId
});

// METER SONIDOS Y BOTONES EN LA TABLA
async function cargarSonidos(usuarioId) {
    try {
        // Obtener el token JWT del almacenamiento local del navegador
        const token = localStorage.getItem("jwtToken");

        //  obtener la lista de sonidos del usuario
        const response = await fetch(`http://localhost:8080/sonidos/buscar/${usuarioId}`, {
            method: "GET",
            headers: {
                // Incluir el token JWT en el encabezado de autorización
                "Authorization": `Bearer ${token}`
            }
        });

        // poner respuesta en JSON
        const sonidos = await response.json();

        // obtener la tabla donde se mostrarán los sonidos
        const tablaSonidos = document.getElementById("tablaSonidos");

        // limpiar  tabla y poner encabezado
        tablaSonidos.innerHTML = "<tr><th>Nombre</th><th>Reproducir</th><th>Acción</th></tr>";

        // iterar por la lista de sonidos 
        sonidos.forEach((sonido) => {
            // crear nueva fila 
            const row = document.createElement("tr");

            // crear celda para el nombre 
            const nombreCell = document.createElement("td");
            nombreCell.textContent = sonido.nombre; // poner nombre del sonido en la celda
            row.appendChild(nombreCell); // añadir la celda a la fila

            // crear botón de reproducir
            const reproducirBtn = document.createElement("button");
            reproducirBtn.textContent = "Reproducir";

            // añadir evento  en el botón de reproducir
            reproducirBtn.addEventListener("click", function () {
                // crear objeto Audio con la URL del archivo de sonido
                const audio = new Audio(`http://localhost:8080/sonidos/descargar/${sonido.id}`); // usar endpoint /descargar/{id}
                audio.play(); // reproducir el archivo de audio
            });

            // crear  celda para el botón de reproducir
            const reproducirCell = document.createElement("td");
            reproducirCell.appendChild(reproducirBtn); // añadir botón a la celda
            row.appendChild(reproducirCell); // añadir celda a la fila

            // crear botón de borrar
            const eliminarBtn = document.createElement("button");
            eliminarBtn.textContent = "🗑️ Borrar";

            // añadir  evento para el clic en el botón de borrar
            eliminarBtn.addEventListener("click", function () {
                borrarSonido(sonido.id); // llamar función para eliminar el sonido
            });

            // crear celda para el botón de borrar
            const eliminarCell = document.createElement("td");
            eliminarCell.appendChild(eliminarBtn); // añadir botón a la celda
            row.appendChild(eliminarCell); // añadir celda a la fila

            // añadir fila a la tabla
            tablaSonidos.appendChild(row);
        });
    } catch (error) {
        // avisar si hay error
        console.error("Error al cargar los sonidos:", error);
    }
}


//  MOSTRAR EN LA TABLA
function mostrarSonidos(sonidos) {
    // sacar tabla donde van los sonidos del html
    const tabla = document.getElementById("tablaSonidos");

    // Limpiar tabla y añadir encabezado
    tabla.innerHTML = `
        <tr>
            <th>Nombre</th>
            <th>Reproducir</th>
            <th>Acción</th>
        </tr>
    `;

    // iterar por los sonidos
    sonidos.forEach(sonido => {
        // crear nueva fila (<tr>) para cada sonido
        const fila = document.createElement("tr");

        // meter  contenido en la fila
        //162 usa /buscar/{id} como src del audio
        //167 boton para eliminar sonido con un evento onclick que llama a la funcion borrarSonido
        fila.innerHTML = `
            <td>${sonido.nombre}</td>
            <td>
                <audio controls>
                                        <source src="http://localhost:8080/sonidos/buscar/${sonido.id}" type="audio/mpeg">
                    Tu navegador no soporta el elemento de audio.
                </audio>
            </td>
            <td>
                <button class="delete-btn" onclick="borrarSonido(${sonido.id})">️ Borrar</button>
            </td>
        `;

        // añadir fila creada a la tabla
        tabla.appendChild(fila);
    });
}


//BORRAR UN SOLO SONIDO
async function borrarSonido(id) {
    const confirmar = confirm("¿Estás seguro de que deseas eliminar este sonido?");
    if (!confirmar) return;
//Comprobar sesion
    const token = localStorage.getItem("jwtToken");
    if (!token) {
        alert("No hay sesión iniciada. Inicia sesión para borrar música.");
        return;
    }

    try { //solicitud al endpoint /sonidos/borrar/id
        const response = await fetch(`http://localhost:8080/sonidos/borrar/${id}`, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (response.ok) {
            alert("Sonido eliminado correctamente.");
            cargarSonidos(); // recarga lista de sonidos
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
//BORRAR TODOS LOS SONIDOS
document.getElementById("deleteAllBtn").addEventListener("click", async function () {
    //añadimos evento al boton de borrartodos y confirmamos
    const confirmar = confirm("¿Estás seguro de que deseas borrar todos tus sonidos?");
    if (!confirmar) return;
//Comprobar sesion
    const token = localStorage.getItem("jwtToken");
    if (!token) {
        alert("No hay sesión iniciada. Inicia sesión para borrar todos los sonidos.");
        return;
    }

    try {
        //sacar usuario del token
        const usuarioId = extraerUsuarioDesdeJWT(token);
    if (!usuarioId) {
        alert("No se pudo obtener el usuario del token.");
        return;
    }

        // peticion al endpoint de borrartodo
        const response = await fetch(`http://localhost:8080/sonidos/borrarTodo/${usuarioId}`, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (response.ok) {
            alert("Todos los sonidos han sido eliminados.");
            cargarSonidos(); // recargar lista de sonidos
        } else {
            alert("Error al eliminar los sonidos.");
        }
    } catch (error) {
        console.error("Error al borrar todos los sonidos:", error);
        alert("Error al conectar con el servidor.");
    }
});


// OBTENER USUARIO DESDE JWT
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
        return usuarioId;  // Retorna el usuario si todo es correcto
    } catch (error) {
        console.error("Error al decodificar el token JWT:", error);
        return null;
    }
}
// 🔹 DEVOLVER USUARIO DEL TOKEN
function extraerUsuarioDesdeJWT(token) {
    const usuarioId = obtenerUsuarioDesdeToken();
    if (!usuarioId) return null;
    return usuarioId;
}

// 🔹 DESCODIFICAR BASE 64 DEL TOKEN 
function base64UrlDecode(str) {
    str = str.replace(/-/g, '+').replace(/_/g, '/'); 
    while (str.length % 4 !== 0) {
        str += "="; 
    }
    return atob(str);
}