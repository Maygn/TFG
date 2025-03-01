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
public class CuentaController {


	   @Autowired
	    private CuentaService cuentaService;
	   @Autowired
	    private CuentaRepository cuentaRepository;
	
	//asignar json a nuevo usuario
	@PostMapping("/nuevo")
	public ResponseEntity<String> crearJson(@RequestParam String token, @RequestParam String defJson) {
	
		String usuario= extraerUsuarioDesdeJWT(token);
		
		try { //se usa responseentity porque deja manipular el tipo de error.
	        return new ResponseEntity<String>(cuentaService.guardarCuenta(usuario, defJson).getUsuario()+" guardado en BDD",HttpStatus.OK); 
    	}
    	catch(DataIntegrityViolationException e) {
    		return new ResponseEntity<String>("Ya tenemos ese usuario",HttpStatus.BAD_REQUEST);
    	}
		

	}
	//recuperar json de usuario
	@GetMapping("/buscar/{usuario}")
	public String verJson(@PathVariable String token) {
		
		String usuario= extraerUsuarioDesdeJWT(token);
		
		Cuenta c1= cuentaService.buscarPorNombre(usuario);
		String musica= c1.musica;
		return musica;
	}
	//modificar json de usuario
	@PutMapping("modificar/{usuario}")
	//requestbody convierte los datos a un objeto cuenta
    public ResponseEntity<Cuenta> actualizarMusica(@PathVariable String usuario, @RequestBody Cuenta cuentaActualizada) {

        if (!cuentaRepository.existsById(usuario)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Cuenta cuenta = cuentaRepository.findById(usuario).get();
        cuenta.setMusica(cuentaActualizada.getMusica());
        cuentaRepository.save(cuenta);

        return new ResponseEntity<>(cuenta, HttpStatus.OK);
    }
	
	
	//borrar un usuario del todo
	@DeleteMapping("/borrar")
	public ResponseEntity<String> borrarCuenta(@RequestBody String token) {
	    try {
	        // Extraer usuario del token
	        String usuario = extraerUsuarioDesdeJWT(token);
	        
	        if (usuario == null) {
	            return new ResponseEntity<>("Token inválido o usuario no encontrado", HttpStatus.NOT_FOUND
	            		);
	        }

	        // Verificar si el usuario existe en la base de datos
	        if (!cuentaService.existeUsuario(usuario)) {
	            return new ResponseEntity<>("El usuario no existe", HttpStatus.NOT_FOUND);
	        }

	        // Eliminar usuario
	        cuentaService.borrarUsuario(usuario);
	        
	        return new ResponseEntity<>("Usuario " + usuario + " eliminado correctamente", HttpStatus.OK);
	    } catch (Exception e) {
	        return new ResponseEntity<>("Error al eliminar usuario", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	
	
	private String extraerUsuarioDesdeJWT(String token) {
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
