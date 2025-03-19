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

import jakarta.transaction.Transactional;

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
	
	
//subir archivos de sonido
	@PostMapping("/subir")
	public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file,
	        @RequestParam String nombre,@RequestHeader("Authorization") String token) {

	    try {
	        // sacar usuario
	        String usuario = extraerUsuarioDesdeJWT(token);
	        //ver si existe, sino manda unauthorized
	        if (usuario == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
	        }

	        // convertir el archivo a un array de bytes
	        byte[] archivoBytes = file.getBytes();

	        // guarda archivo con nombre y usuario asociado
	        SonidoService.saveSonido(nombre, archivoBytes, usuario);

	        return ResponseEntity.ok("Archivo subido correctamente");
	    } catch (IOException e) {//cualquier fallo imprevisto 
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir el archivo");
	    }
	}


	//recuperar una lista con todos los sonidos del usuario.
	@GetMapping("/buscarLista")
	public ResponseEntity<List<Sonido>> getSonidosPorUsuario(@RequestHeader("Authorization") String token) {
		//si no hay usuario en el token o no hay token, manda error
	    try {
	        String usuario = extraerUsuarioDesdeJWT(token);
	        if (usuario == null || usuario.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	        }
	        //si lo hay, genera una lista con los sonidos del usuario
	        List<Sonido> sonidos = sonidoRepository.findByUsuario(usuario);
	        if (sonidos.isEmpty()) {//si la lista esta vacia, manda error
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	        }
	        //si no esta vacia, enviarla
	        return ResponseEntity.ok(sonidos);
	    } catch (Exception e) {//cualquier fallo imprevisto
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}

	
	
	 //Borrar todos los sonidos del usuario
	 @Transactional
	 @DeleteMapping("/borrarTodo")
	 public ResponseEntity<Void> borrarSonidosPorUsuario(@RequestHeader("Authorization") String token) {
		 System.out.println("Entrando a borrar usuario");
	     try {//si no hay usuario en el token o no hay token, manda error
	         String usuario = extraerUsuarioDesdeJWT(token);
	         if (usuario == null || usuario.isEmpty()) {
	        	 System.out.println("usuario null");
	             return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	         }
	         //si el usuario no tiene sonidos manda error
	         if (!sonidoRepository.existsByUsuario(usuario)) {
	        	 System.out.println("El usuario no tiene sonidos");
	             return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	         }
	         //borra todos los sonidos
	         sonidoRepository.deleteByUsuario(usuario);
	         //si todo bien, devuelve respuesta sin error
	         return ResponseEntity.noContent().build();
	     } catch (Exception e) { //cualquier fallo imprevisto
	         return ResponseEntity.status(HttpStatus.OK).build();
	     } 
	 }
//borrar un solo sonido por su id
	 @DeleteMapping("/borrar/{id}")
	 public ResponseEntity<Void> borrarSonido(@RequestHeader("Authorization") String token, @PathVariable Long id) {
	     try {//si no hay usuario en el token o no hay token, manda error
	         String usuario = extraerUsuarioDesdeJWT(token);
	         if (usuario == null || usuario.isEmpty()) {
	             return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	         }//se usa optional para poder controlar nulls mejor
	         //busco el sonido con la id que se le ha dado
	         Optional<Sonido> sonidoOpt = sonidoRepository.findById(id);
	         if (sonidoOpt.isEmpty()) {// si no hay, mando error
	             return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	         }
	         //recupero el objeto sonido y compruebo que el usuario sea el mismo que el del token
	         Sonido sonido = sonidoOpt.get();
	         if (!sonido.getUsuario().equals(usuario)) {//si no es el mismo, mando error
	             return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	         }
	         //si es el mismo, borro el usuario y mando un ok
	         sonidoRepository.deleteById(id);
	         return ResponseEntity.status(HttpStatus.OK).build();
	     } catch (Exception e) { //resto de errores inesperados
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	     }
	 }

	 @GetMapping("/descargar/{id}")
	 public ResponseEntity<Resource> descargarSonido(@PathVariable Long id, @RequestHeader("Authorization") String token) {
	     String usuario = extraerUsuarioDesdeJWT(token);

	     // //si no hay usuario en el token o no hay token, manda error
	     if (usuario == null || !esUsuarioAutorizado(id, usuario)) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	     }

	     try {
	         // sacar el archivo como recurso
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

	 //comprueba que existe y que el usuario es igual al token
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

	 
	 //sacar usuario del token
		private String extraerUsuarioDesdeJWT(String token) {
			try {
				// separar en partes
				String[] partes = token.split("\\.");
				if (partes.length != 3) {
					return null; // Token inválido
				}

				// decodificar payload
				String payloadJson = new String(Base64.getUrlDecoder().decode(partes[1]));

				// sacar usuario
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
