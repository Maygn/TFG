package musica;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("comunes")
public class Comunes{
	@Id
	
	private final Long id=1L;

    private Musica musicaPublica;

	public Musica getMusicaPublica() {
		return musicaPublica;
	}

	public Long getId() {
		return id;
	}

	public void setMusicaPublica(Musica musicaPublica) {
		this.musicaPublica = musicaPublica;
	}

	public Comunes() {
		super();
	}
    
}
