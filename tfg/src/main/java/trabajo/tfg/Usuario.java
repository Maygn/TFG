package trabajo.tfg;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Usuario {
		
		@Column
	  @Id
	  @GeneratedValue(strategy = GenerationType.UUID)
	    private UUID codigo;
		@Column(unique = true)
	    private String usuario;
		
		
		@Column
	    private String contrasena;
	    
	    // Getters y setters
	    public UUID getCodigo() {
	        return codigo;
	    }

	    public void setCodigo(UUID codigo) {
	        this.codigo = codigo;
	    }

	    public String getUsuario() {
	        return usuario;
	    }

	    public void setUsuario(String usuario) {
	        this.usuario = usuario;
	    }

	    public String getContrasena() {
	        return contrasena;
	    }

	    public void setContrasena(String contrasena) {
	        this.contrasena = contrasena;
	    }
	}
