package musica;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="Musica")
public class Musica implements Serializable{

    @Id
    @Column(name="usuario")
    private String usuario;
    @Column(name="musica",length = 2000)
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