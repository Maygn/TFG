package trabajo.tfg;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5500")
@RestController
@RequestMapping("/usuarios")

public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private UsuarioRepository usuarioRepository;

	// Endpoint para guardar un nuevo usuario

	@PostMapping("/guardar")
	public ResponseEntity<String> guardarUsuario(@RequestParam String usuario, @RequestParam String contrasena,
			@RequestParam boolean admin, @RequestParam(required = false) String claveAdmin) {
		try {
			Usuario usuarioCreado = usuarioService.guardarUsuario(usuario, contrasena, admin, claveAdmin);
			return new ResponseEntity<>(usuarioCreado.getUsuario() + " guardado en BDD", HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			// Se lanza cuando la clave de admin no es correcta
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (DataIntegrityViolationException e) {
			return new ResponseEntity<>("Ya tenemos ese usuario", HttpStatus.BAD_REQUEST);
		}
	}

	// obtener usuario por nombre
	@GetMapping("/obtener/usuario")
	public ResponseEntity<String> obtenerUsuario(@RequestParam String nombreUsuario) {
		try {
			Usuario usuario = usuarioService.obtenerUsuarioPorNombre(nombreUsuario);
			if (usuario == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
			}
			String token = usuarioService.generarToken(usuario);
			return ResponseEntity.ok(token);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error procesando la solicitud");
		}
	}

	@GetMapping("/verificar")
	public ResponseEntity<String> verificarUsuario(@RequestParam String usuario, @RequestParam String contrasena) {
		Usuario u = usuarioRepository.findByUsuario(usuario);
		// si el usuario existe
		if (u != null) {
			// usar el metodo contraseñacorrecta que devuelve un boolean
			boolean credencialesValidas = usuarioService.contrasenaCorrecta(usuario, contrasena);
			// si el boolean es true, devuelve ok
			if (credencialesValidas) {
				return new ResponseEntity<String>("Bienvenido.", HttpStatus.OK);
				// si no es correcta, devuelve mensaje y error
			} else {
				return new ResponseEntity<String>("Contraseña incorrecta.", HttpStatus.UNAUTHORIZED);
			}
			// si no existe
		} else {
			return new ResponseEntity<String>("El usuario introducido no existe.", HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/cambiarClave")
	public ResponseEntity<String> cambiarClave(@RequestParam String token, @RequestParam String contrasenaAct,
			@RequestParam String contrasenaNueva) {
		String usuario;
		try {
			usuario = usuarioService.extraerUsuarioDesdeJWT(token);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error en el usuario. Inicia sesión de nuevo.");
		}
		ResponseEntity<String> validar = verificarUsuario(usuario, contrasenaAct);
		if (validar.getStatusCode() == HttpStatus.OK) {
			usuarioService.cambiarContrasena(contrasenaNueva, usuario);
			return ResponseEntity.ok("Contraseña cambiada correctamente.");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta.");
		}
	}

	@DeleteMapping("/borrar/usuario")
	public ResponseEntity<String> borrarUsuario(@RequestParam String token, @RequestParam String contrasena) {
		String usuario;
		try {
			usuario = usuarioService.extraerUsuarioDesdeJWT(token);
			Usuario user = usuarioService.obtenerUsuarioPorNombre(usuario);
			if (user != null && user.isAdmin()) {
				return new ResponseEntity<>("Un administrador no puede ser eliminado.", HttpStatus.FORBIDDEN);
			}
			boolean credencialesValidas = usuarioService.contrasenaCorrecta(usuario, contrasena);
			if (credencialesValidas) {
				usuarioService.borrarUsuario(usuario);
				return new ResponseEntity<>("Usuario " + usuario + " borrado.", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Contraseña incorrecta.", HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			return new ResponseEntity<>("Error en el usuario. Inicie sesión de nuevo." + e.getMessage(),
					HttpStatus.NOT_FOUND);
		}
	}

	

}