let musicaJson = {}; // Inicializar musicaJson como un objeto vacío
let cancionSeleccionada = {
    nombre: "",
    enlace: ""
};

// Verificar que todos los elementos del DOM existen antes de usarlos
const btnAgregar = document.getElementById("agregarCancion");
const inputCategoria = document.getElementById("categoria");
const inputSubcategoria = document.getElementById("subcategoria");
const inputCancion = document.getElementById("cancion");
const inputEnlace = document.getElementById("enlace");
const listaCanciones = document.getElementById("listaCanciones");

if (!btnAgregar || !inputCategoria || !inputSubcategoria || !inputCancion || !inputEnlace || !listaCanciones) {
    alert("Error: Uno o más elementos del DOM no se encontraron. Revisa los IDs en el HTML.");
    console.error("Elementos faltantes:", {
        btnAgregar, inputCategoria, inputSubcategoria, inputCancion, inputEnlace, listaCanciones
    });
} else {
    btnAgregar.addEventListener("click", () => {
        try {
            const categoria = inputCategoria.value.trim();
            const subcategoria = inputSubcategoria.value.trim();
            const cancion = inputCancion.value.trim();
            const enlace = inputEnlace.value.trim();

            if (!categoria || !subcategoria) {
                alert("Por favor, ingresa una categoría y subcategoría válidas.");
                return;
            }

            if (!cancion || !enlace) {
                alert("Por favor, complete todos los campos de canción y enlace.");
                return;
            }

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
            listaCanciones.appendChild(li);

            // Limpiar los campos del formulario
            inputCancion.value = '';
            inputEnlace.value = '';
        } catch (error) {
            alert("Ocurrió un error al intentar agregar la canción. Revisa la consola para más detalles.");
            console.error("Error al agregar canción:", error);
        }
    });
}


// Verificaciones iniciales de elementos del DOM
const btnGuardarCambios = document.getElementById("guardarCambios");
const btnEnviarCancion = document.getElementById("enviarCancion");
const inputIdCanal = document.getElementById("idCanal");
const selectCategoria = document.getElementById("categoria");
const subcategoriaWrapper = document.getElementById("subcategoria");
    // Función para guardar cambios (enviar al servidor)
    btnGuardarCambios.addEventListener("click", async () => {
        const token = localStorage.getItem("jwtToken");
        if (!token) {
            alert("No hay sesión iniciada.");
            return;
        }

        const musicaJsonString = JSON.stringify(musicaJson);

        try {
            const response = await fetch("http://localhost:8090/musica/modificar", {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify({
                    musica: musicaJsonString
                })
            });

            if (response.ok) {
                const data = await response.json();
                alert("El JSON ha sido actualizado correctamente.");
            } else {
                const errorText = await response.text();
                alert("Error al actualizar el JSON. Detalles: " + errorText);
                console.error("Respuesta del servidor:", errorText);
            }
        } catch (error) {
            console.error("Error al enviar la solicitud:", error);
            alert("Error al guardar los cambios. Revisa la consola para más detalles.");
        }
    });

    // Función para enviar una canción al bot de Discord
    // btnEnviarCancion.addEventListener("click", () => {
    //     const idCanal = inputIdCanal.value.trim();

    //     if (cancionSeleccionada.nombre && cancionSeleccionada.enlace && idCanal) {
    //         const requestData = {
    //             idCanal: idCanal,
    //             mensaje: `!play ${cancionSeleccionada.enlace}`
    //         };

    //         fetch("http://localhost:8083/enviarMensaje", {
    //             method: "POST",
    //             headers: {
    //                 "Content-Type": "application/json"
    //             },
    //             body: JSON.stringify(requestData)
    //         })
    //         .then(response => {
    //             if (response.ok) {
    //                 alert("Canción enviada correctamente.");
    //             } else {
    //                 alert("Error al enviar la canción.");
    //                 response.text().then(text => console.error("Respuesta del servidor:", text));
    //             }
    //         })
    //         .catch(error => {
    //             console.error("Error al conectar con el servidor:", error);
    //             alert("Error al conectar con el servidor.");
    //         });
    //     } else {
    //         alert("Por favor, selecciona una canción y un canal de Discord.");
    //     }
    // });

    // Función para manejar el cambio de categorías y subcategorías
    selectCategoria.addEventListener("change", function () {
        const categoriaSeleccionada = this.value.trim();

        if (!categoriaSeleccionada) {
            alert("Categoría no válida.");
            return;
        }

        const nuevaSubcategoria = document.createElement("input");
        nuevaSubcategoria.type = "text";
        nuevaSubcategoria.id = "subcategoria";
        nuevaSubcategoria.classList.add("form-control");
        nuevaSubcategoria.placeholder = "Nueva subcategoría";

        subcategoriaWrapper.appendChild(nuevaSubcategoria);
    });

