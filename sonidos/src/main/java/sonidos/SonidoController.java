package sonidos;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
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
	        //ver si existe, sino manda unauthorized
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


	
	@GetMapping("/buscarLista/{token}")
	public ResponseEntity<List<Sonido>> getSonidosPorUsuario(@PathVariable String token) {
	    try {
	        String usuario = extraerUsuarioDesdeJWT(token);
	        if (usuario == null || usuario.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	        }
	        List<Sonido> sonidos = sonidoRepository.findByUsuario(usuario);
	        if (sonidos.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	        }
	        return ResponseEntity.ok(sonidos);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}

	
	
	 
	 @DeleteMapping("/borrarTodo/{token}")
	 public ResponseEntity<Void> borrarSonidosPorUsuario(@PathVariable String token) {
	     try {
	         String usuario = extraerUsuarioDesdeJWT(token);
	         if (usuario == null || usuario.isEmpty()) {
	             return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	         }
	         if (!sonidoRepository.existsByUsuario(usuario)) {
	             return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	         }
	         sonidoRepository.deleteByUsuario(usuario);
	         return ResponseEntity.noContent().build();
	     } catch (Exception e) {
	         return ResponseEntity.status(HttpStatus.OK).build();
	     }
	 }

	 @DeleteMapping("/borrar/{token}/{id}")
	 public ResponseEntity<Void> borrarSonido(@PathVariable String token, @PathVariable Long id) {
	     try {
	         String usuario = extraerUsuarioDesdeJWT(token);
	         if (usuario == null || usuario.isEmpty()) {
	             return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	         }
	         Optional<Sonido> sonidoOpt = sonidoRepository.findById(id);
	         if (sonidoOpt.isEmpty()) {
	             return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	         }
	         Sonido sonido = sonidoOpt.get();
	         if (!sonido.getUsuario().equals(usuario)) {
	             return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	         }
	         sonidoRepository.deleteById(id);
	         return ResponseEntity.status(HttpStatus.OK).build();
	     } catch (Exception e) {
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	     }
	 }

	 @GetMapping("/descargar/{id}")
	 public ResponseEntity<Resource> descargarSonido(@PathVariable Long id, @RequestHeader("Authorization") String token) {
	     String usuario = extraerUsuarioDesdeJWT(token);

	     // Verificar si el usuario es válido
	     if (usuario == null || !esUsuarioAutorizado(id, usuario)) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	     }

	     try {
	         // Obtener el archivo como recurso
	         Resource resource = SonidoService.getFileAsResource(id);

	         // Configurar las cabeceras para la descarga del archivo
	         HttpHeaders headers = crearCabecerasParaDescarga(id);

	         // Retornar el archivo como respuesta
	         return ResponseEntity.ok()
	                 .headers(headers)
	                 .contentType(MediaType.valueOf("audio/mpeg"))
	                 .body(resource);
	     } catch (IOException e) {
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	     }
	 }

	 /**
	  * Verifica si el usuario está autorizado para descargar el sonido.
	  */
	 private boolean esUsuarioAutorizado(Long id, String usuario) {
		    Optional<Sonido> sonido = SonidoService.getSonido(id);
		    return sonido.isPresent() && sonido.get().getUsuario().equals(usuario);
		}

	 /**
	  * Crea las cabeceras necesarias para la descarga del archivo.
	  */
	 private HttpHeaders crearCabecerasParaDescarga(Long id) {
	     String contentDisposition = "inline; filename=sonido_" + id + ".mp3";
	     HttpHeaders headers = new HttpHeaders();
	     headers.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
	     return headers;
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

	 }
