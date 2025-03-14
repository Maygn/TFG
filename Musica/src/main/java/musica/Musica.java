package musica;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.Id;

@Entity
public class Musica {

    @Id
    @Column
    private String usuario;
    @Column
    private String musica;
    // repositorio compartido (estatico)
    private static Musica musicaPublica;
    
    public static Musica getMusicaPublica() {
        return musicaPublica;
    }

    public static void setMusicaPublica(Musica musicaPublica) {
        Musica.musicaPublica = musicaPublica;
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