package musica;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/musica") // Ruta base para este controlador
public class MusicaController {


	   @Autowired
	    private MusicaService cuentaService;
	   @Autowired
	    private MusicaRepository cuentaRepository;
	
	//asignar json a nuevo usuario
	@PostMapping("/nuevo")
	public ResponseEntity<String> crearJson(@RequestParam String token, @RequestParam String defJson) {
	//buscar user en el token
		String usuario= extraerUsuarioDesdeJWT(token);
		//se usa responseentity porque deja manipular el tipo de error.
		try { //si todo bien
	        return new ResponseEntity<String>(cuentaService.guardarCuenta(usuario, defJson).getUsuario()+" guardado correctamente.",HttpStatus.OK); 
    	}
    	catch(DataIntegrityViolationException e) {// si ya esta
    		return new ResponseEntity<String>("Ya tenemos ese usuario",HttpStatus.BAD_REQUEST);
    	}
		

	}
	//recuperar json de usuario
	@GetMapping("/buscar/{token}")
	public ResponseEntity<String> verJson(@PathVariable String token) {
	    try {
	        //sacar usuario desde el JWT
	        String usuario = extraerUsuarioDesdeJWT(token);
	        // buscar cuenta 
	        Musica c1 = cuentaService.buscarPorNombre(usuario);
	        // si la cuenta existe
	        if (c1 == null) {
	            return new ResponseEntity<>("No hemos encontrado esa cuenta.", HttpStatus.NOT_FOUND);
	        }
	        
	        // obtener y devolver la música 
	        String musica = c1.getMusica();
	        //si no hay musica asociada
	        if (musica == null || musica.isEmpty()) {
	            return new ResponseEntity<>("No se encontró música para el usuario: " + usuario, HttpStatus.NOT_FOUND);
	        }
	        //si todo esta bien
	        else {
	        return new ResponseEntity<>(musica, HttpStatus.OK);
	        }
	    } catch (IllegalArgumentException e) {
	        // error en el token
	        return new ResponseEntity<>("Error con el usuario. Inicia sesión de nuevo", HttpStatus.UNAUTHORIZED);
	    } catch (Exception e) {
	        // cualquier otro error
	        return new ResponseEntity<>("Error interno en el servidor.", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	//modificar json de usuario
	@PutMapping("modificar")
	public ResponseEntity<Musica> actualizarMusica(@RequestParam String token, @RequestBody Musica cuentaActualizada) {
	    try {
	        // Extraer usuario del token
	        String usuario = extraerUsuarioDesdeJWT(token);
	        
	        if (usuario == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }

	        // Verificar si el usuario existe
	        if (!cuentaRepository.existsById(usuario)) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }

	        // Obtener la cuenta y actualizar la música
	        Musica cuenta = cuentaRepository.findById(usuario).get();
	        cuenta.setMusica(cuentaActualizada.getMusica());
	        cuentaRepository.save(cuenta);

	        return new ResponseEntity<>(cuenta, HttpStatus.OK);
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	//borrar un usuario del todo
	@DeleteMapping("/borrar")
	public ResponseEntity<String> borrarCuenta(@RequestParam String token) {
	    try {
	        // Extraer usuario del token
	        String usuario = extraerUsuarioDesdeJWT(token);
	        
	        if (usuario == null) {
	            return new ResponseEntity<>("Token inválido o usuario no encontrado", HttpStatus.NOT_FOUND
	            		);
	        }

	        // ver si usuario existe
	        if (!cuentaService.existeUsuario(usuario)) {
	            return new ResponseEntity<>("El usuario no existe", HttpStatus.NOT_FOUND);
	        }

	        // borrar usuario
	        cuentaService.borrarUsuario(usuario);
	        
	        return new ResponseEntity<>("Usuario " + usuario + " eliminado correctamente", HttpStatus.OK);
	    } catch (Exception e) {
	        return new ResponseEntity<>("Error al eliminar usuario", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	
	//no necesita un endpoint porque solo lo uso desde otros metodos
	private String extraerUsuarioDesdeJWT(String token) {
		try {
			// separa JWT en partes
			String[] partes = token.split("\\.");
			if (partes.length != 3) {
				return null; // Token inválido
			}

			//decodificar el payload
			String payloadJson = new String(Base64.getUrlDecoder().decode(partes[1]));

			// sacar usuario
			Pattern pattern = Pattern.compile("\"Usuario\":\\s*\"(.*?)\"");
			Matcher matcher = pattern.matcher(payloadJson);

			if (matcher.find()) {
				return matcher.group(1); // retornar usuario
			} else {
				return null; // no hay usuario en token
			}
		} catch (Exception e) {
			return null; // si peta, asumimos token inválido
		}
	}
}
