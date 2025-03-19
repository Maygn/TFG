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

let cancionSeleccionada = {
    nombre: "",
    enlace: ""
  };
  
  function generate(item, container, name, isFinal) {
      let newDiv = document.createElement("div");
      
      // Si es un objeto, se maneja normalmente
      if (typeof item === "object") {
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
  
          // Agregar un eventListener para manejar el clic y actualizar el texto
          newLink.addEventListener("click", function(event) {
              event.preventDefault(); // Evitar que el enlace se abra en una nueva pestaña
              document.getElementById("cancionElegida").textContent = name; // Actualizar el contenido del p
              
              // Guardar la canción y el enlace
              cancionSeleccionada.nombre = name;
              cancionSeleccionada.enlace = item;
          });
  
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


document.addEventListener("DOMContentLoaded", async () => {
  const token = localStorage.getItem("jwtToken");
  const userInfoElement = document.getElementById("userInfo");

  if (!token) {
      userInfoElement.innerText = "No hay sesión iniciada.";
      return;
  }

})

let musicaJson = {}; // Inicializar musicaJson como un objeto vacío

// Función para agregar canción
document.getElementById("agregarCancion").addEventListener("click", () => {
    const categoria = document.getElementById("categoria").value;
    const subcategoria = document.getElementById("subcategoria").value;
    const cancion = document.getElementById("cancion").value;
    const enlace = document.getElementById("enlace").value;

    if (cancion && enlace) {
        // Verificar si la categoría y subcategoría ya existen, si no, crear nuevas
        if (!musicaJson[categoria]) {
            musicaJson[categoria] = {};
        }

        if (!musicaJson[categoria][subcategoria]) {
            musicaJson[categoria][subcategoria] = {};
        }

        // Agregar la canción al objeto musicaJson
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

    // Convertir musicaJson a un string JSON
    const musicaJsonString = JSON.stringify(musicaJson); 

    try {
        const response = await fetch("http://localhost:8090/musica/modificar", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({
                musica: musicaJsonString // Pasar el string JSON
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



const categorias = {
    "Boss": ["Malvado", "Antiheroe", "Asesino"],
    "Ciudad": ["Pobre", "Desierto", "Ladrones", "Steampunk"],
    "Exploracion": ["Ruinas", "Bosque", "Mazmorra"],
    "Combate": ["Taberna", "Campamento", "Plano Astral"],
    "Descanso": ["Nubes", "Sol", "Noche"]
};

const categoriaSelect = document.getElementById("categoria");
const subcategoriaSelect = document.getElementById("subcategoria");

// Función para actualizar las subcategorías en función de la categoría seleccionada
function actualizarSubcategorias() {
    const categoriaSeleccionada = categoriaSelect.value;
    const subcategorias = categorias[categoriaSeleccionada] || [];

    // Limpiar las opciones anteriores
    subcategoriaSelect.innerHTML = "";

    // Agregar las nuevas opciones de subcategorías
    subcategorias.forEach(subcategoria => {
        const option = document.createElement("option");
        option.value = subcategoria;
        option.textContent = subcategoria;
        subcategoriaSelect.appendChild(option);
    });
}

// Escuchar cambios en la categoría y actualizar las subcategorías
categoriaSelect.addEventListener("change", actualizarSubcategorias);

// Inicializar el formulario con las subcategorías correspondientes a la categoría predeterminada
actualizarSubcategorias();

document.getElementById("enviarCancion").addEventListener("click", function() {
    const idCanal = document.getElementById("idCanal").value;

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
                alert("Error al enviar la canción.");
            }
        })
        .catch(error => {
            console.error("Error:", error);
            alert("Error al conectar con el servidor.");
        });
    } else {
        alert("Por favor, selecciona una canción y un canal de Discord.");
    }
});
