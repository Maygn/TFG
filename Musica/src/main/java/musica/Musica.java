package musica;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "musica")
public class Musica {

    @Id
    @Indexed(unique = true)
    private String usuario;

    private String musica;

    public Musica() {}

    public Musica(String usuario, String musica) {
        this.usuario = usuario;
        this.musica = musica;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getMusica() {
        return musica;
    }

    public void setMusica(String musica) {
        this.musica = musica;
    }

	public void orElseGet(Object object) {
		// TODO Auto-generated method stub
		
	}
}
