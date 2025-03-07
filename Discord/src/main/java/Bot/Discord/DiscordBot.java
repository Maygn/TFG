package Bot.Discord;


	import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
	import discord4j.core.GatewayDiscordClient;
	import discord4j.core.object.entity.channel.TextChannel;
import jakarta.annotation.PostConstruct;
	import org.springframework.beans.factory.annotation.Value;
	import org.springframework.stereotype.Component;

	@Component  //esta clase es un bean
	public class DiscordBot {
		//recoge info de variale de entorno y la guarda en variable
	    @Value("${tokenDiscord}")
	    private String token;
	    //conexion con discord
	    private GatewayDiscordClient cliente;
	    
	    

	    @PostConstruct //Ejecuta al crear la instancia del bean
	    public void iniciarUsuario() {
	        cliente = DiscordClientBuilder.create(token) //crea cliente usando el token
	                .build() //construye el cliente
	                .login() //inicia sesion en discord usando el token
	                .block(); //esperar a que se inicie sesion correctamente

	        
	    }

	    public void enviarMensaje(String idCanal, String enlace) {
	    	//busca canal por la id, pero para usar esa id la pone como objeto snowflake porque discord lo requiere
	        cliente.getChannelById(Snowflake.of(idCanal))
	                .ofType(TextChannel.class) //asegurarse que es un canal de texto
	                .flatMap(channel -> channel.createMessage(enlace)) //si es de texto crea y manda el enlace
	                .subscribe();  //deja el envio en cola para hacerlo cuando esté listo
	    }
 
	}

