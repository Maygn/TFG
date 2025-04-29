const categorias = {
    "Boss": ["Malvado", "Antiheroe", "Asesino"],
    "Ciudad": ["Pobre", "Desierto", "Ladrones", "Steampunk"],
    "Exploracion": ["Ruinas", "Bosque", "Mazmorra"],
    "Combate": ["Taberna", "Campamento", "Plano Astral"],
    "Descanso": ["Nubes", "Sol", "Noche"]
};

// Verificar si los elementos existen en el DOM
const categoriaSelect = document.getElementById("categoria");
const subcategoriaSelect = document.getElementById("subcategoria");

if (!categoriaSelect || !subcategoriaSelect) {
    alert("Error: No se encontraron los elementos del formulario.");
    console.error("No se pudo encontrar 'categoria' o 'subcategoria' en el DOM.");
}

// Función para actualizar las subcategorías en función de la categoría seleccionada
function actualizarSubcategorias() {
    try {
        const categoriaSeleccionada = categoriaSelect.value;
        const subcategorias = categorias[categoriaSeleccionada];

        if (!subcategorias) {
            alert(`No se encontraron subcategorías para la categoría: ${categoriaSeleccionada}`);
            console.warn(`Categoría no válida: ${categoriaSeleccionada}`);
        }

        // Limpiar las opciones anteriores
        subcategoriaSelect.innerHTML = "";

        // Agregar las nuevas opciones de subcategorías
        (subcategorias || []).forEach(subcategoria => {
            const option = document.createElement("option");
            option.value = subcategoria;
            option.textContent = subcategoria;
            subcategoriaSelect.appendChild(option);
        });
    } catch (error) {
        alert("Error al actualizar las subcategorías. Revisa la consola.");
        console.error("Error en actualizarSubcategorias:", error);
    }
}

// Escuchar cambios en la categoría y actualizar las subcategorías
if (categoriaSelect) {
    categoriaSelect.addEventListener("change", actualizarSubcategorias);
} else {
    alert("Error: no se puede añadir el listener a 'categoriaSelect'");
}

// Inicializar el formulario con las subcategorías correspondientes a la categoría predeterminada
try {
    if (categoriaSelect) {
        actualizarSubcategorias();
    }
} catch (error) {
    alert("Error al inicializar las subcategorías.");
    console.error("Error en la inicialización:", error);
}



document.addEventListener("DOMContentLoaded", function () {
    const toggleModoBtn = document.getElementById("toggleModo");
    const modoVisualizacion = document.getElementById("modoVisualizacion");
    const modoEdicion = document.getElementById("modoEdicion");
    const listaCanciones = document.getElementById("listaCanciones");
    const agregarCancionBtn = document.getElementById("agregarCancion");
    const enviarCancionBtn = document.getElementById("enviarCancion");
    const botonEliminar = document.getElementById("eliminarTodas");

    // Verificación de elementos esenciales
    if (!toggleModoBtn || !modoVisualizacion || !modoEdicion || !listaCanciones || !agregarCancionBtn || !botonEliminar) {
        alert("Error: No se encontraron todos los elementos necesarios del DOM.");
        console.error("Faltan elementos en el DOM:", {
            toggleModoBtn, modoVisualizacion, modoEdicion, listaCanciones, agregarCancionBtn, botonEliminar
        });
        return;
    }

    toggleModoBtn.addEventListener("click", function () {
        try {
            if (modoVisualizacion.style.display === "none") {
                modoVisualizacion.style.display = "block";
                modoEdicion.style.display = "none";
                toggleModoBtn.textContent = "Cambiar a Edición";
            } else {
                modoVisualizacion.style.display = "none";
                modoEdicion.style.display = "block";
                toggleModoBtn.textContent = "Cambiar a Visualización";
            }
        } catch (error) {
            alert("Error al alternar el modo de visualización.");
            console.error("Error en toggleModoBtn click:", error);
        }
    });

    const token = localStorage.getItem("jwtToken");
    if (!token) {
        alert("No se encontró el token JWT. Asegúrate de haber iniciado sesión.");
    }

    function obtenerCanciones() {
        fetch("http://localhost:8090/publico/obtener", {
            method: "GET",
            headers: {
                "Authorization": token
            }
        })
        .then(response => {
            if (!response.ok) {
                alert("Error al obtener canciones. Código de estado: " + response.status);
                throw new Error("HTTP error " + response.status);
            }
            return response.json();
        })
        .then(data => {
            listaCanciones.innerHTML = "";
            data.forEach(cancion => {
                const li = document.createElement("li");
                li.textContent = cancion;

                const borrarBtn = document.createElement("button");
                borrarBtn.textContent = "Borrar";
                borrarBtn.onclick = () => borrarCancion(cancion);

                li.appendChild(borrarBtn);
                listaCanciones.appendChild(li);
            });
        })
        .catch(error => {
            alert("Error al obtener canciones. Revisa la consola.");
            console.error("Error al obtener canciones:", error);
        });
    }

    function agregarCancion() {
        const cancionInput = document.getElementById("cancion");
        if (!cancionInput) {
            alert("No se encontró el input de canción.");
            console.error("Falta el input con id 'cancion'.");
            return;
        }

        const cancion = cancionInput.value;
        if (!cancion.trim()) {
            alert("Por favor, escribe el nombre de una canción.");
            return;
        }
        debugger
        fetch("http://localhost:8090/publico/publica/agregar", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: `token=${token}&cancion=${encodeURIComponent(cancion)}`
        })
        .then(response => response.text())
        .then(message => {
            alert(message);
            obtenerCanciones();
        })
        .catch(error => {
            alert("Error al agregar canción. Revisa la consola.");
            console.error("Error al agregar canción:", error);
        });
    }

    const esAdmin = localStorage.getItem("esAdmin") === "true";
    if (esAdmin) {
        botonEliminar.style.display = "block";
    }

    botonEliminar.addEventListener("click", function () {
        if (confirm("¿Estás seguro de que quieres eliminar todas las canciones?")) {
            fetch(`http://localhost:8090/publico/borrar?token=${token}`, {
                method: "DELETE"
            })
            .then(response => response.text())
            .then(message => {
                alert(message);
                obtenerCanciones();
            })
            .catch(error => {
                alert("Error al borrar canciones. Revisa la consola.");
                console.error("Error al borrar canciones:", error);
            });
        }
    });

    agregarCancionBtn.addEventListener("click", agregarCancion);

    obtenerCanciones();

    // Opcional: botón enviar canción si tienes funcionalidad pendiente
    if (enviarCancionBtn) {
        enviarCancionBtn.addEventListener("click", () => {
            alert("Funcionalidad de enviar canción aún no implementada.");
        });
    }
});




