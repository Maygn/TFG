package trabajo.tfg;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("usuarios")  // Optional: specify collection name
public class Usuario {

    @Id
    private UUID codigo; // MongoDB will store this as a UUID or string

    private String usuario;

    private boolean admin;

    private static final String CLAVE_ADMIN = "PatatasConAtun";

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

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public static String getClaveAdmin() {
        return CLAVE_ADMIN;
    }
}
