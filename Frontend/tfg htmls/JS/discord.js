document.addEventListener("DOMContentLoaded", function () {
    //  el código se ejecute después de que el DOM  se cargue.
    fetch("../jsong/musica.json") // petición del JSON 
        .then(response => response.json()) // convertir en JSON.
        .then(data => cargarCanciones(data)) // Llama a cargarCanciones pasando el JSON.
        .catch(error => console.error("Error cargando el JSON:", error)); // muestra error si algo falla
});

let urlCancion = ''; // variable con la URL de la cancion

function cargarCanciones(data) {
    const ListaCanciones = document.getElementById("ListaCanciones");
    ListaCanciones.innerHTML = ""; // Limpiar lista antes de cargar

    Object.keys(data).forEach(genero => {
        if (typeof data[genero] === "object" && !Array.isArray(data[genero])) {
            Object.keys(data[genero]).forEach(subgenero => {
                if (typeof data[genero][subgenero] === "object" && !Array.isArray(data[genero][subgenero])) {
                    Object.keys(data[genero][subgenero]).forEach(song => {
                        agregarCancion(song, data[genero][subgenero][song], ListaCanciones);
                    });
                } else {
                    agregarCancion(subgenero, data[genero][subgenero], ListaCanciones); // Manejo del caso especial "Pop"
                }
            });
        }
    });
}

function agregarCancion(nombre, url, ListaCanciones) {
    let li = document.createElement("li");
    li.classList.add("list-group-item");
    li.textContent = nombre;
    li.dataset.url = url;

    li.addEventListener("click", function () {
        document.querySelectorAll(".list-group-item").forEach(item => item.classList.remove("elegida"));
        li.classList.add("elegida");
        document.getElementById("cancionElegida").textContent = nombre;
        urlCancion = url; // Guardar la URL de la canción seleccionada
    });

    ListaCanciones.appendChild(li);
}

document.querySelector(".btn").addEventListener("click", function () {
    const idCanal = document.getElementById("idCanal").value; // Capturar el ID del canal
    if (urlCancion && idCanal) {
        const peticion = {
            idCanal: idCanal,
            mensaje: urlCancion
        };

        fetch("/enviarMensaje", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(peticion)
        })
        .then(response => response.json())
        .then(data => {
            console.log("Mensaje enviado correctamente", data);
        })
        .catch(error => console.error("Error enviando mensaje:", error));
    } else {
        alert("Por favor, selecciona una canción y un ID de canal.");
    }
});
