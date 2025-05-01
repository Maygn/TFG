package sonidos;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("sonido")
public class Sonido {
	
    @Id
    private Long id;

    private String nombre;
    private String usuario;

    private byte[] archivo;

    // Getters y Setters
    public byte[] getArchivo() {
        return archivo;
    }

    public void setArchivo(byte[] archivo) {
        this.archivo = archivo;
    }


	public Long getId() {
		return id;
	}

	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       



    
}
