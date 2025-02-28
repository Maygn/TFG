package sonidos;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;

@RestController
@RequestMapping("/sonidos")
public class SonidoController {

	@Autowired
	private SonidoRepository sonidoRepository; 
	@Autowired
	private SonidoService SonidoService;

	@PostMapping("/subir")
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
	        @RequestParam("nombre") String nombre, @RequestHeader("Authorization") String token) {

	    try {
	        // Extraer usuario desde el token
	        String usuario = extraerUsuarioDesdeJWT(token);
	        if (usuario == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
	        }

	        // Convertir el archivo a un array de bytes
	        byte[] archivoBytes = file.getBytes();

	        // Guardar el sonido con el archivo en la base de datos
	        SonidoService.saveSonido(nombre, archivoBytes, usuario);

	        return ResponseEntity.ok("Archivo subido correctamente con el nombre: " + nombre);
	    } catch (IOException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir el archivo");
	    }
	}


	/**
	 * Método para extraer el usuario desde el token JWT.
	 */
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

	@GetMapping("/buscar/{usuario}")
	public List<Sonido> getSonidosPorUsuario(@PathVariable String usuario) {
		return sonidoRepository.findByUsuario(usuario);
	}
	
	 @DeleteMapping("/borrar/{id}")
	    public ResponseEntity<Void> borrarSonido(@PathVariable Long id) {
	        if (sonidoRepository.existsById(id)) {
	            sonidoRepository.deleteById(id);
	            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	        } else {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	    }
	 @DeleteMapping("/borrarTodo/{usuario}")
	    public ResponseEntity<Void> borrarSonidosPorUsuario(@PathVariable String usuario) {
	        sonidoRepository.deleteByUsuario(usuario);
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	    }
	 
	 
	 @GetMapping("/descargar/{id}")
	 public ResponseEntity<Resource> descargarSonido(@PathVariable Long id) {
	     try {
	         // Obtener el archivo como recurso
	         Resource resource = SonidoService.getFileAsResource(id);

	         // Configurar las cabeceras para la descarga del archivo
	         String contentDisposition = "inline; filename=sonido_" + id + ".mp3"; // "inline" permite la reproducción
	         HttpHeaders headers = new HttpHeaders();
	         headers.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);

	         // Retornar el archivo como respuesta
	         return ResponseEntity.ok()
	                 .headers(headers)
	                 .contentType(MediaType.valueOf("audio/mpeg")) // Asegúrate de que el tipo MIME sea audio/mpeg
	                 .body(resource);
	     } catch (IOException e) {
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	     }
	 }
	 }
