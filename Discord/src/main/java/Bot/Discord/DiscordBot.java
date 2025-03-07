package Bot.Discord;


	import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
	import discord4j.core.GatewayDiscordClient;
	import discord4j.core.object.entity.channel.TextChannel;
import jakarta.annotation.PostConstruct;
	import org.springframework.beans.factory.annotation.Value;
	import org.springframework.stereotype.Component;

	@Component
	public class DiscordBot {
		
	    @Value("${tokenDiscord}")
	    private String token;

	    private GatewayDiscordClient gateway;

	    @PostConstruct
	    public void init() {
	        gateway = DiscordClientBuilder.create(token)
	                .build()
	                .login()
	                .block();

	        
	    }

	    public void enviarMensaje(String idCanal, String enlace) {
	        gateway.getChannelById(Snowflake.of(idCanal))
	                .ofType(TextChannel.class)
	                .flatMap(channel -> channel.createMessage(enlace))
	                .subscribe();
	    }

	  
	}

