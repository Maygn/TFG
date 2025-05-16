package Musica.Musica;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "musica")

//Entiendo que la mierdita que he quitado la pusiste porque GPT te lo dijo? por el error de cors, creo recordar. No hagas cosas que no sabes que hacen
public class Musica {

    @Id
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

	
}
