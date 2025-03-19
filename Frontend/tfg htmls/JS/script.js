let out = true;
let json = {};
let mainContainer = document.getElementById("mainContainer");
let timeoutId = null;  // Variable para almacenar el temporizador

// Cargar el JSON al iniciar la página
const token = localStorage.getItem("jwtToken");

if (token) {
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
    .catch(error => console.error("Error:", error));
} else {
    console.error("No hay sesión iniciada. No se puede cargar el JSON del usuario.");
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

function generate(item, container, name, isFinal) {
    let newDiv = document.createElement("div");

    // Si es un array, crear enlaces directamente
    if (Array.isArray(item)) {
        newDiv.classList.add("finalContainer");
        for (let link of item) {
            let newLink = document.createElement("a");
            newLink.textContent = link;
            newLink.href = link;
            newLink.target = "_blank";
            newLink.style.textDecoration = "none";
            newLink.style.color = "black";

            let linkDiv = document.createElement("div");
            linkDiv.appendChild(newLink);
            newDiv.appendChild(linkDiv);
        }
    } 
    // Si es un objeto, se maneja normalmente
    else if (typeof item === "object") {
        newDiv.textContent = name;
        newDiv.classList.add("contContainer");

        newDiv.addEventListener("mouseover", function () {
            clearTimeout(timeoutId);
            out = false;
            removeAndCreate(item, container, true);
        });

        newDiv.addEventListener("mouseleave", function () {
            out = true;
            timeoutId = setTimeout(function () {
                if (out) {
                    removeAbove(document.querySelector(".listContainer"));
                }
            }, 1000);
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

        newDiv.appendChild(newLink);
        newDiv.classList.add("stringContainer");
    }

    container.appendChild(newDiv);
}


function removeAbove(container) {
  while (container && container.nextElementSibling) {
    container.nextElementSibling.remove();
  }
}

function removeAndCreate(item, container, isContainer) {
  removeAbove(container);
  let newDiv = document.createElement("div");
  newDiv.classList.add(isContainer ? "listContainer" : "finalContainer");
  
  newDiv.addEventListener("mouseover", function () {
    out = false;
  });

  mainContainer.appendChild(newDiv);
  iterateGenerate(newDiv, item, !isContainer);
}


document.addEventListener("DOMContentLoaded", function () {
  const token = localStorage.getItem("jwtToken");

  if (token) {
    try {
      // Verificar que el token tenga el formato adecuado antes de intentar decodificarlo
      const parts = token.split('.');
      if (parts.length === 3) {
        // Decodificar el payload usando base64UrlDecode
        const payload = JSON.parse(base64UrlDecode(parts[1]));
        console.log("Payload decodificado:", payload);

        const usuarioId = payload.Usuario; // Extrae el ID del usuario del campo 'Usuario'

        if (usuarioId) {
          // Construir la URL dinámicamente con el ID del usuario
          const url = `http://localhost:8081/usuarios/obtener/usuario?nombreUsuario=${usuarioId}`;

          // Realizar la petición con la URL construida dinámicamente
          fetch(url)
          .then(response => {
            console.log("Tipo de contenido de la respuesta:", response.headers.get("Content-Type"));
        
            if (response.ok && response.headers.get("Content-Type").includes("application/json")) {
              return response.json();  // Leer como JSON
            } else {
              throw new Error(`Respuesta inesperada o error en el servidor: ${response.statusText}`);
            }
          })
          .then(data => {
            console.log("Datos del usuario:", data);
            document.getElementById("userInfo").innerText = `Puedes subir música a la cuenta de: ${data.usuario}`;
          })
          .catch(error => {
            console.error("Error al obtener los datos del usuario:", error);
          });
        

          document.getElementById("userInfo").innerText = `Puedes subir música a la cuenta de: ${usuarioId}`;
        } else {
          document.getElementById("userInfo").innerText = "No se pudo obtener el usuario.";
        }
      } else {
        throw new Error("Token JWT no válido");
      }
    } catch (error) {
      console.error("Error al decodificar el token JWT:", error);
      document.getElementById("userInfo").innerText = "Error al obtener el usuario.";
    }
  } else {
    document.getElementById("userInfo").innerText = "No hay sesión iniciada.";
  }
});

function base64UrlDecode(str) {
str = str.replace(/-/g, '+').replace(/_/g, '/'); 
while (str.length % 4 !== 0) {
  str += "=";
}
return atob(str);
}

document.getElementById("editarJson").addEventListener("click", async () => {
  const token = localStorage.getItem("jwtToken"); // Obtener el token almacenado en localStorage
  if (!token) {
      alert("No hay sesión iniciada.");
      return;
  }

  // Simulación de JSON actualizado
  const musicaActualizada = JSON.stringify({
    "Ladrido": {
        "muy": {
            "fuerte": "https://www.youtube.com/watch?v=fJ9rUzIMcZQ"
        }
    }
  });

  try {
      const response = await fetch("http://localhost:8090/musica/modificar", {
          method: "PUT",
          headers: { 
              "Content-Type": "application/json",
              "Authorization": `Bearer ${token}` // Se pasa el token en los headers
          }, 
          body: musicaActualizada // Enviar JSON correctamente
      });

      if (response.ok) {
          const data = await response.json();
          console.log("JSON actualizado:", data);
          alert("El JSON del usuario se ha actualizado correctamente.");
      } else {
          alert("Error al actualizar el JSON.");
      }
  } catch (error) {
      console.error("Error en la solicitud:", error);
      alert("Error al actualizar el JSON.");
  }
});


// Datos de ejemplo iniciales (en la realidad se cargarían del servidor)
let musicaJson = {
    "Rock": {
        "Clásico": {
            "Bohemian Rhapsody": "https://www.youtube.com/watch?v=fJ9rUzIMcZQ"
        }
    }
};

// Función para agregar canción
document.getElementById("agregarCancion").addEventListener("click", () => {
    const categoria = document.getElementById("categoria").value;
    const subcategoria = document.getElementById("subcategoria").value;
    const cancion = document.getElementById("cancion").value;
    const enlace = document.getElementById("enlace").value;

    if (cancion && enlace) {
        // Agregar la canción al JSON
        if (!musicaJson[categoria]) {
            musicaJson[categoria] = {};
        }
        if (!musicaJson[categoria][subcategoria]) {
            musicaJson[categoria][subcategoria] = {};
        }

        musicaJson[categoria][subcategoria][cancion] = enlace;

        // Mostrar la canción agregada en la lista
        const li = document.createElement("li");
        li.textContent = `${cancion} - ${enlace}`;
        document.getElementById("listaCanciones").appendChild(li);

        // Limpiar los campos del formulario
        document.getElementById("cancion").value = '';
        document.getElementById("enlace").value = '';
    } else {
        alert("Por favor, complete todos los campos.");
    }
});

// Función para guardar cambios (enviar al servidor)
document.getElementById("guardarCambios").addEventListener("click", async () => {
    const token = localStorage.getItem("jwtToken"); // Obtener el token desde localStorage
    if (!token) {
        alert("No hay sesión iniciada.");
        return;
    }

    try {
        const response = await fetch("http://localhost:8090/musica/modificar", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                token: token,
                musica: musicaJson
            })
        });

        if (response.ok) {
            const data = await response.json();
            alert("El JSON ha sido actualizado correctamente.");
        } else {
            alert("Error al actualizar el JSON.");
        }
    } catch (error) {
        console.error("Error al enviar la solicitud:", error);
        alert("Error al guardar los cambios.");
    }
});





  