let out = true;
let json = {};
let mainContainer = document.getElementById("mainContainer");
let timeoutId = null;  // Variable para almacenar el temporizador


// Cargar el JSON al iniciar la página
const token = localStorage.getItem("jwtToken");
const usuarioId = extraerUsuarioDesdeJWT(token); // Si necesitas pasar el usuarioId, agrega aquí la lógica para obtenerlo.
cargarSonidos(usuarioId);  // Llamar a la función para cargar los sonidos al cargar la página

if (token) {
    debugger
    fetch("http://localhost:8090/musica/buscar", {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${token}`
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Error al cargar el JSON del usuario");
        }
        return response.json();
    })
    .then(data => {
        json = data; // Guardar datos en la variable json
        iterateGenerate(document.querySelector(".listContainer"), json);
    })
    .catch(error => {
        console.error("Error:", error);
        alert("Error al cargar los datos del usuario. Por favor, inténtalo de nuevo más tarde.");
    });
} else {
    const mensaje = "No hay sesión iniciada. No se puede cargar el JSON del usuario.";
    console.error(mensaje);
    alert(mensaje);
}

mainContainer.addEventListener("mouseover", function (event) {
  if (event.target === mainContainer) {
    out = true;
    clearTimeout(timeoutId);  // Limpiar cualquier temporizador anterior
    timeoutId = setTimeout(function () {
      if (out) {
        removeAbove(document.querySelector(".listContainer"));
      }
    }, 1000); 
  }
});


mainContainer.addEventListener("mouseleave", function () {
  out = true;
  timeoutId = setTimeout(function () {
    if (out) {
      removeAbove(document.querySelector(".listContainer"));
    }
  }, 1000);
});

function iterateGenerate(container, items, isFinal) {
  for (let a of Object.keys(items)) {
    generate(items[a], container, a, isFinal);
  }
}

let cancionSeleccionada = {
    nombre: "",
    enlace: ""
  };
  
  function generate(item, container, name, isFinal) {
    try {
        let newDiv = document.createElement("div");

        // Validar que el contenedor existe
        if (!container) {
            throw new Error("Contenedor no encontrado. No se puede generar el elemento.");
        }

        // Si es un objeto, se maneja normalmente
        if (typeof item === "object" && item !== null) {
            newDiv.textContent = name;
            newDiv.classList.add("contContainer");

            newDiv.addEventListener("mouseover", function () {
                try {
                    clearTimeout(timeoutId);
                    out = false;
                    removeAndCreate(item, container, true);
                } catch (error) {
                    console.error("Error en mouseover:", error);
                    alert("Ocurrió un error al intentar mostrar el contenido.");
                }
            });

            newDiv.addEventListener("mouseleave", function () {
                try {
                    out = true;
                    timeoutId = setTimeout(function () {
                        if (out) {
                            removeAbove(document.querySelector(".listContainer"));
                        }
                    }, 1000);
                } catch (error) {
                    console.error("Error en mouseleave:", error);
                    alert("Ocurrió un error al intentar ocultar el contenido.");
                }
            });

        } 
        // Si es un string, es un enlace individual
        else if (typeof item === "string") {
            let newLink = document.createElement("a");
            newLink.textContent = name;
            newLink.href = item;
            newLink.target = "_blank";
            newLink.style.textDecoration = "none";
            newLink.style.color = "black";

            newLink.addEventListener("click", function (event) {
                try {
                    event.preventDefault(); // Evitar que el enlace se abra en nueva pestaña
                    const cancionEl = document.getElementById("cancionElegida");
                    if (!cancionEl) throw new Error("Elemento 'cancionElegida' no encontrado.");

                    cancionEl.textContent = name;
                    cancionSeleccionada.nombre = name;
                    cancionSeleccionada.enlace = item;
                } catch (error) {
                    console.error("Error al seleccionar la canción:", error);
                    alert("No se pudo seleccionar la canción. Asegúrate de que el elemento exista.");
                }
            });

            newDiv.appendChild(newLink);
            newDiv.classList.add("stringContainer");
        }

        container.appendChild(newDiv);
    } catch (error) {
        console.error("Error en generate:", error);
        alert("Ha ocurrido un error al generar los elementos. Revisa la consola para más detalles.");
    }
}


function removeAbove(container) {
    try {
      while (container && container.nextElementSibling) {
        container.nextElementSibling.remove();
      }
    } catch (error) {
      console.error("Error al eliminar elementos siguientes:", error);
      alert("Ocurrió un error al limpiar los elementos de la interfaz.");
    }
  }
  
  function removeAndCreate(item, container, isContainer) {
    try {
      if (!container) throw new Error("Contenedor no definido en removeAndCreate.");
  
      removeAbove(container);
  
      let newDiv = document.createElement("div");
      newDiv.classList.add(isContainer ? "listContainer" : "finalContainer");
  
      newDiv.addEventListener("mouseover", function () {
        out = false;
      });
  
      if (!mainContainer) throw new Error("mainContainer no está definido.");
      mainContainer.appendChild(newDiv);
  
      iterateGenerate(newDiv, item, !isContainer);
  
    } catch (error) {
      console.error("Error en removeAndCreate:", error);
      alert("Ocurrió un error al generar el nuevo contenido.");
    }
  }

let musicaJson = {}; // Inicializar musicaJson como un objeto vacío

document.getElementById("enviarCancion").addEventListener("click", function () {
    try {
        const idCanal = document.getElementById("idCanal").value;

        // Verificar si cancionSeleccionada está definido correctamente
        if (typeof cancionSeleccionada === "undefined") {
            alert("No hay una canción seleccionada.");
            throw new Error("El objeto cancionSeleccionada no está definido.");
        }

        // Verificar si hay una canción seleccionada y si se ha proporcionado un canal
        if (cancionSeleccionada.nombre && cancionSeleccionada.enlace && idCanal) {
            const requestData = {
                idCanal: idCanal,
                mensaje: `!play ${cancionSeleccionada.enlace}`
            };

            // Enviar la petición al backend
            fetch("http://localhost:8083/enviarMensaje", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(requestData)
            })
                .then(response => {
                    if (response.ok) {
                        alert("Canción enviada correctamente.");
                    } else {
                        response.text().then(text => {
                            console.error("Respuesta del servidor:", text);
                            alert("Error al enviar la canción. Revisa la consola para más detalles.");
                        });
                    }
                })
                .catch(error => {
                    console.error("Error de conexión:", error);
                    alert("No se pudo conectar con el servidor.");
                });
        } else {
            alert("Por favor, selecciona una canción y proporciona un ID de canal de Discord.");
        }
    } catch (error) {
        console.error("Error inesperado:", error);
        alert("Ocurrió un error al intentar enviar la canción.");
    }
});

function extraerUsuarioDesdeJWT(token) {
    const usuarioId = obtenerUsuarioDesdeToken();
    if (!usuarioId) return null;
    return usuarioId;
}
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
function base64UrlDecode(str) {
    str = str.replace(/-/g, '+').replace(/_/g, '/'); 
    while (str.length % 4 !== 0) {
        str += "="; 
    }
    return atob(str);
}
async function cargarSonidos() {
    try {
        const token = localStorage.getItem("jwtToken");

        if (!token) {
            alert("No hay sesión iniciada. Inicia sesión para ver los sonidos.");
            return;
        }

        const response = await fetch("http://localhost:8080/sonidos/buscarLista", {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (response.status === 404) {
            alert("No hay lista creada en la base de datos.");
            return;
        } else if (!response.ok) {
            alert("Error al obtener la lista de sonidos.");
            return;
        }

        const sonidos = await response.json();

        const tablaSonidos = document.getElementById("tablaSonidos");
        tablaSonidos.innerHTML = "<tr><th>Sonidos</th></tr>"; // Ahora solo una columna: Nombre (que será botón)

        sonidos.forEach((sonido) => {
            const row = document.createElement("tr");

            const nombreCell = document.createElement("td");
            const botonSonido = document.createElement("button");
            botonSonido.textContent = sonido.nombre;
            botonSonido.classList.add("boton-sonido"); // Clase opcional por si quieres estilos CSS

            botonSonido.addEventListener("click", function () {
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

            nombreCell.appendChild(botonSonido);
            row.appendChild(nombreCell);

            tablaSonidos.appendChild(row);
        });

    } catch (error) {
        console.error("Error al cargar los sonidos:", error);
        alert("Ocurrió un error al contactar con el servidor.");
    }
}


