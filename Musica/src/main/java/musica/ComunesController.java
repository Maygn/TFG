package musica;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/publico")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class ComunesController {

	
	



	    @Autowired
	    private ComunesService comunesService;

	    @GetMapping("/obtener")
	    public ResponseEntity<?> obtenerMusicaPublica(@RequestHeader("Authorization") String token) {
	        try {
	            // Llamamos al servicio pasando el token para validarlo y obtener la música pública
	            ResponseEntity<Musica> response = comunesService.obtenerMusicaPublica(token);

	            // Si no se encontró la música pública, devolvemos un error con el mensaje adecuado
	            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
	                return new ResponseEntity<>("No hay música pública disponible.", HttpStatus.NOT_FOUND);
	            }
	            if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
	                return new ResponseEntity<>("El usuario no es válido.", HttpStatus.FORBIDDEN);
	            }

	            // Si se encuentra la música, devolvemos la respuesta del servicio
	            return response;

	        } catch (Exception e) {
	            // Si ocurre alguna excepción inesperada, devolvemos un error genérico
	            return new ResponseEntity<>("Ocurrió un error al intentar recuperar la música pública.", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	    
	    
	    @PostMapping("/publica/agregar")
	    public ResponseEntity<String> agregarMusicaPublica(
	            @RequestParam String token, 
	            @RequestParam String cancion) {
	        try {
	            // Llamamos al servicio para agregar la música pública
	            ResponseEntity<String> response = comunesService.agregarMusicaPublica(token, cancion);
	            
	            // Si hubo un fallo en el servicio, simplemente pasamos el mensaje y el código de estado
	            if (response.getStatusCode() != HttpStatus.OK) {
	                return response;
	            }
	            
	            // Si la canción se agregó correctamente, devolvemos el mensaje de éxito
	            return new ResponseEntity<>("Canción añadida correctamente.", HttpStatus.OK);
	            
	        } catch (Exception e) {
	            // En caso de error inesperado, devolvemos un mensaje genérico de error
	            return new ResponseEntity<>("Ocurrió un error al intentar agregar la canción.", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	    
	    // Método para borrar una canción de la música pública
	    @DeleteMapping("/borrar")
	    public ResponseEntity<String> borrarMusicaPublica(@RequestParam String token, @RequestParam String cancion) {
	    	System.out.println("entrando en borrar publico");
	        try {
	            // Llamar al servicio para borrar la música pública
	            ResponseEntity<String> response = comunesService.borrarMusicaPublica(token, cancion);

	            // Si la respuesta es FORBIDDEN o NOT_FOUND, devolvemos el mensaje de error adecuado
	            if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
	            	System.out.println("Token inválido o el usuario no tiene permisos de administrador.");
	                return new ResponseEntity<>("Token inválido o el usuario no tiene permisos de administrador.", HttpStatus.FORBIDDEN);
	            }

	            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
	            	System.out.println("No se encontró la música pública o la canción no está en el repositorio.");
	                return new ResponseEntity<>("No se encontró la música pública o la canción no está en el repositorio.", HttpStatus.NOT_FOUND);
	            }

	            // Si todo sale bien, devolvemos el mensaje de éxito
	            return response;

	        } catch (Exception e) {
	            // Si ocurre alguna excepción inesperada, devolvemos un error genérico
	            return new ResponseEntity<>("Ocurrió un error al intentar borrar la música pública.", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
}
