package trabajo.tfg;

import java.util.Base64;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
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

	@PostMapping(value = "/guardar", consumes = "application/x-www-form-urlencoded")

	public ResponseEntity<String> guardarUsuario(@RequestParam String usuario, @RequestParam String contrasena) {

		// usa el wrapper para poder mandar las dos cosas, el usuario si todo va bien y
		// el error si no
		try { // se usa responseentity porque deja manipular el tipo de error.
			return new ResponseEntity<String>(
					usuarioService.guardarUsuario(usuario, contrasena).getUsuario() + " guardado en BDD",
					HttpStatus.OK);
		} catch (DataIntegrityViolationException e) {
			return new ResponseEntity<String>("Ya tenemos ese usuario", HttpStatus.BAD_REQUEST);
		}

	}

	
	//crear el token
	private String generarToken(Usuario usuario) {
		LocalDateTime fechaHora = LocalDateTime.now();
		// esto es para comprobar los credenciales, primero hago las tres piezas del jwt

		String jwtHeader = "{\"alg\": \"HS256\", \"typ\": \"JWT\"}";
		String jwtPayload = "{ \"Usuario\": \"" + usuario.getUsuario() + "\", \"Fecha\": \"" + fechaHora.toString()
				+ "\" }";
		String secret = "BoqueronesConVinagre";
		// las pongo en base64 para que no de guerra en otros sistemas
		String jwtHeader64 = Base64.getUrlEncoder().withoutPadding().encodeToString(jwtHeader.getBytes());
		String jwtPayload64 = Base64.getUrlEncoder().withoutPadding().encodeToString(jwtPayload.getBytes());
		// concateno las piezas y las vuelvo a traducir.
		String jwtHmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret).hmacHex(jwtHeader + jwtPayload);
		String jwtHmac64 = Base64.getUrlEncoder().withoutPadding().encodeToString(jwtHmac.getBytes());
		// y las concateno una vez traducidas, y luego las envio
		return jwtHeader64 + "." + jwtPayload64 + "." + jwtHmac64;
	}
	// Endpoint para obtener usuario por nombre //genera el url añadiendo el
	// contenido del pathvariable en lugar del {usuario} Alex
	@GetMapping("/obtener/{nombreUsuario}") 
	public ResponseEntity<String> obtenerUsuario(@PathVariable String nombreUsuario) {
	    try {
	        Usuario usuario = usuarioService.obtenerUsuarioPorNombre(nombreUsuario);
	        if (usuario == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
	        }
	        
	        String token = generarToken(usuario);
	        return ResponseEntity.ok(token);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error procesando la solicitud");
	    }
	}

	public boolean decoder(String recibido, String secret) {

		// separo en cada punto
		String[] partes = recibido.split("\\.");
		// pongo cada cacho en un string distinto
		String header = new String(Base64.getUrlDecoder().decode(partes[0]));
		String payload = new String(Base64.getUrlDecoder().decode(partes[1]));

		// rehago la firma con los datos que he sacado y enviando el secret directamente
		String firmaRehecha = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret).hmacHex(partes[0] + "." + partes[1]);

		// la firma no se decodea.
		String firmaOriginal = partes[2];
		// comparo ambas
		boolean correcto = false;
		if (firmaOriginal.equals(firmaRehecha)) {
			correcto = true;
		}

		return correcto;
	}

	@GetMapping("/verificar")
	public ResponseEntity<String> verificarUsuario(@RequestParam String usuario, @RequestParam String contrasena) {
	    Usuario u = usuarioRepository.findByUsuario(usuario);
	    //si el usuario existe
	    if (u != null) {
	    	//usar el metodo contraseñacorrecta que devuelve un boolean
	        boolean credencialesValidas = usuarioService.contrasenaCorrecta(usuario, contrasena);
	        //si el boolean es true, devuelve ok
	        if (credencialesValidas) {
	            return new ResponseEntity<String>("Bienvenido.", HttpStatus.OK);
	            //si no es correcta, devuelve mensaje y error
	        } else {
	            return new ResponseEntity<String>("Contraseña incorrecta.", HttpStatus.UNAUTHORIZED);
	        }
	        //si no existe
	    } else {
	        return new ResponseEntity<String>("El usuario introducido no existe.", HttpStatus.NOT_FOUND);
	    }
	}


	@PostMapping("/cambiarClave")
	public ResponseEntity<String> cambiarClave(@RequestParam String token, @RequestParam String contrasenaAct,
			@RequestParam String contrasenaNueva) {

		String usuario;
		// Sacar usuario del token
		try {
			usuario = extraerUsuarioDesdeJWT(token);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error en el usuario. Inicia sesión de nuevo.");
		}
		// comprobar contraseña del usuario
		ResponseEntity<String> validar = verificarUsuario(usuario, contrasenaAct);
		// si es valida, cambiar contraseña, sino, mandar error
		if (validar.getStatusCode() == HttpStatus.OK) {
			usuarioService.cambiarContrasena(contrasenaNueva, usuario);
			return ResponseEntity.ok("Contraseña cambiada correctamente.");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta.");
		}
	}

	@DeleteMapping("/borrar/{usuario}")
	public ResponseEntity<String> borrarUsuario(@PathVariable String token, @RequestParam String contrasena) {

		String usuario;
		// Sacar usuario del token
		try {
			usuario = extraerUsuarioDesdeJWT(token);
			//validar credenciales del usuario
			try {
				boolean credencialesValidas = usuarioService.contrasenaCorrecta(usuario, contrasena);
				if (credencialesValidas) {
					usuarioService.borrarUsuario(usuario);
					return new ResponseEntity<>("Usuario " + usuario + " borrado.", HttpStatus.OK);
				} else {
					//error para clave incorrecta
					return new ResponseEntity<>("Contraseña incorrecta.", HttpStatus.UNAUTHORIZED);
				}
				//error de servidor
			} catch (Exception e) {
				return new ResponseEntity<>("Error al borrar usuario: " + e.getMessage(),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
			//error si hay problemas con el token
		} catch (Exception e) {
			return new ResponseEntity<>("Error en el usuario. Inicie sesión de nuevo." + e.getMessage(),
					HttpStatus.NOT_FOUND);
		}
	}
//no private porque sino no lo puedo usar en testing
	protected String extraerUsuarioDesdeJWT(String token) {
		try {
			// Separar el JWT en sus partes
			String[] partes = token.split("\\.");
			if (partes.length != 3) {
				return null; // Token inválido
			}

			// Decodificar el payload
			String payloadJson = new String(Base64.getUrlDecoder().decode(partes[1]));

			// Extraer el campo "Usuario"
			Pattern pattern = Pattern.compile("\"Usuario\":\\s*\"(.*?)\"");
			Matcher matcher = pattern.matcher(payloadJson);

			if (matcher.find()) {
				return matcher.group(1); // Retorna el usuario encontrado en el token
			} else {
				return null; // No se encontró el usuario en el token
			}
		} catch (Exception e) {
			return null; // Si falla algo, asumimos token inválido
		}
	}
}
