document.addEventListener("DOMContentLoaded", function () {
    fetch("../jsong/musica.json")
        .then(response => response.json())
        .then(data => cargarCanciones(data))
        .catch(error => console.error("Error cargando el JSON:", error));
});

let selectedSongUrl = ''; // Variable global para almacenar la URL de la canción seleccionada

function cargarCanciones(data) {
    const songList = document.getElementById("songList");
    songList.innerHTML = ""; // Limpiar lista antes de cargar

    Object.keys(data).forEach(genre => {
        if (typeof data[genre] === "object" && !Array.isArray(data[genre])) {
            Object.keys(data[genre]).forEach(subgenre => {
                if (typeof data[genre][subgenre] === "object" && !Array.isArray(data[genre][subgenre])) {
                    Object.keys(data[genre][subgenre]).forEach(song => {
                        agregarCancion(song, data[genre][subgenre][song], songList);
                    });
                } else {
                    agregarCancion(subgenre, data[genre][subgenre], songList); // Manejo del caso especial "Pop"
                }
            });
        }
    });
}

function agregarCancion(nombre, url, songList) {
    let li = document.createElement("li");
    li.classList.add("list-group-item");
    li.textContent = nombre;
    li.dataset.url = url;

    li.addEventListener("click", function () {
        document.querySelectorAll(".list-group-item").forEach(item => item.classList.remove("selected"));
        li.classList.add("selected");
        document.getElementById("selectedSong").textContent = nombre;
        selectedSongUrl = url; // Guardar la URL de la canción seleccionada
    });

    songList.appendChild(li);
}

document.querySelector(".btn").addEventListener("click", function () {
    const channelId = document.getElementById("channelId").value; // Capturar el ID del canal
    if (selectedSongUrl && channelId) {
        const peticion = {
            idCanal: channelId,
            mensaje: selectedSongUrl
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
