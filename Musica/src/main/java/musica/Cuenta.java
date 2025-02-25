package musica;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Cuenta {

    @Id
    String usuario;
    String musica;

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