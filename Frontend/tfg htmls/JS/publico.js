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


document.addEventListener("DOMContentLoaded", function () {
    const toggleModoBtn = document.getElementById("toggleModo");
    const modoVisualizacion = document.getElementById("modoVisualizacion");
    const modoEdicion = document.getElementById("modoEdicion");
    const listaCanciones = document.getElementById("listaCanciones");
    const agregarCancionBtn = document.getElementById("agregarCancion");
    const enviarCancionBtn = document.getElementById("enviarCancion");
    
    toggleModoBtn.addEventListener("click", function () {
        if (modoVisualizacion.style.display === "none") {
            modoVisualizacion.style.display = "block";
            modoEdicion.style.display = "none";
            toggleModoBtn.textContent = "Cambiar a Edición";
        } else {
            modoVisualizacion.style.display = "none";
            modoEdicion.style.display = "block";
            toggleModoBtn.textContent = "Cambiar a Visualización";
        }
    });

    const token = localStorage.getItem("jwtToken");
    
    function obtenerCanciones() {
        fetch("http://localhost:8090/publico/obtener", {
            method: "GET",
            headers: {
                "Authorization": token
            }
        })
        .then(response => response.json())
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
        .catch(error => console.error("Error al obtener canciones:", error));
    }
    
    function agregarCancion() {
        const cancion = document.getElementById("cancion").value;
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
        .catch(error => console.error("Error al agregar canción:", error));
    }
    
    const esAdmin = localStorage.getItem("esAdmin") === "true"; // Obtener si es admin
    const botonEliminar = document.getElementById("eliminarTodas");

    if (esAdmin) {
        botonEliminar.style.display = "block"; // Mostrar el botón si es admin
    }

    botonEliminar.addEventListener("click", function () {
        if (confirm("¿Estás seguro de que quieres eliminar todas las canciones?")) {
            fetch(`http://localhost:8090/publico/borrar?token=${token}`, {
                method: "DELETE"
            })
            .then(response => response.text())
            .then(message => {
                alert(message);
                obtenerCanciones(); // Actualiza la lista después de borrar
            })
            .catch(error => console.error("Error al borrar canciones:", error));
        }
    });
    
    agregarCancionBtn.addEventListener("click", agregarCancion);
    obtenerCanciones();
});



