async function cambiarClave() {
    const token = localStorage.getItem("jwtToken");
    const contrasenaAct = document.getElementById("contrasenaAct").value;  // Captura el valor actual
    const contrasenaNueva = document.getElementById("contrasenaNueva").value;

    if (!contrasenaAct || !contrasenaNueva) {
        alert("Por favor, llena todos los campos.");
        return;
    }

    const formData = new URLSearchParams();
    formData.append("token", token);
    formData.append("contrasenaAct", contrasenaAct);  // Ahora se envía
    formData.append("contrasenaNueva", contrasenaNueva);

    try {
        const response = await fetch("http://localhost:8081/usuarios/cambiarClave", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: formData
        });

        const result = await response.text();
        alert(result);
    } catch (error) {
        console.error("Error al cambiar la contraseña:", error);
        alert("Ocurrió un error. Inténtalo de nuevo.");
    }
}

function cambiarImagen(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            document.getElementById('profilePic').src = e.target.result;
        }
        reader.readAsDataURL(file);
    }
}

function cambiarClave() {
    alert("Contraseña cambiada correctamente (esto es solo un ejemplo, falta lógica real).");
}
