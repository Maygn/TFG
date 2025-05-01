package musica;



import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document("musica")
public class Musica{

    @Id
 
    private String usuario;
 
    private String musica;
   
   
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


	public Musica() {
		super();
	}

	public void ifPresent(Musica musica) {
		// TODO Auto-generated method stub
		
	}

	public Musica orElse(Musica musica) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isPresent() {
		// TODO Auto-generated method stub
		return false;
	}

	
}