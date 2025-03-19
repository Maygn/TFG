package Bot.Discord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
public class DiscordController {

    @Autowired
    private DiscordBot discordBot;

    @PostMapping("/enviarMensaje")
    public void enviarMensaje(@RequestBody Peticion request) {
        discordBot.enviarMensaje(request.getIdCanal(), request.getMensaje());
    }
}


    