package trabajo.tfg;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
	   @Autowired
	    private UsuarioRepository usuarioRepository;
	   
	  

	    // Guardar un nuevo usuario //el codigo uuid se genera automaticamente
	    public Usuario guardarUsuario(String usuario, String contrasena) throws DataIntegrityViolationException {
	        Usuario nuevoUsuario = new Usuario();
	        nuevoUsuario.setUsuario(usuario);


	        // Hashear la contraseña antes de guardarla
	        String hashedPassword = DigestUtils.sha256Hex(contrasena);
	        nuevoUsuario.setContrasena(hashedPassword);


	        return usuarioRepository.save(nuevoUsuario);  

	    }

	    // Obtener un usuario por su nombre
	    public Usuario obtenerUsuarioPorNombre(String usuario) {
	        return usuarioRepository.findByUsuario(usuario);
	    }
	    
	    
	    // comprobar si la clave es correcta
	    public boolean contrasenaCorrecta(String nombreUsuario, String contrasena) {
	        Usuario usuario = usuarioRepository.findByUsuario(nombreUsuario);
	        if (usuario.getContrasena().equals(DigestUtils.sha256Hex(contrasena))) {
	        	return true;
	        }
	        else {
	        	return false;
	        }
	    }
	}