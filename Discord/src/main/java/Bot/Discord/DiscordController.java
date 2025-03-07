package Bot.Discord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiscordController {

    @Autowired
    private DiscordBot discordBot;

    @PostMapping("/enviarMensaje")
    public void enviarMensaje(@RequestBody Peticion request) {
        discordBot.enviarMensaje(request.getIdCanal(), request.getMensaje());
    }
}

class Peticion {
    private String idCanal;
    private String mensaje;
    // Getters y setters
	public String getIdCanal() {
		return idCanal;
	}
	public void setIdCanal(String idCanal) {
		this.idCanal = idCanal;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

 
    
}