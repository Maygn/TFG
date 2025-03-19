let musicaJson = {}; // Inicializar musicaJson como un objeto vacío
let cancionSeleccionada = {
    nombre: "",
    enlace: ""
};

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

// Función para manejar el cambio de categorías y subcategorías
document.getElementById("categoria").addEventListener("change", function() {
    const categoriaSeleccionada = this.value;
    const subcategoriaSelect = document.getElementById("subcategoria");

    // Limpiar las opciones de subcategorías
    subcategoriaSelect.innerHTML = '';

    if (categoriaSeleccionada) {
        // Crear un nuevo campo para que el usuario añada subcategorías
        const nuevaSubcategoria = document.createElement("input");
        nuevaSubcategoria.type = "text";
        nuevaSubcategoria.id = "subcategoria";
        nuevaSubcategoria.classList.add("form-control");
        nuevaSubcategoria.placeholder = "Nueva subcategoría";
        document.getElementById("subcategoriaWrapper").appendChild(nuevaSubcategoria);
    }
});
