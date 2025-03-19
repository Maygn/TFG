package musica;

import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class ComunesService {

	 @Autowired
	    private ComunesRepository comunesRepository;
//RECIBIR DE PUBLICO
	 public ResponseEntity<Musica> obtenerMusicaPublica(String token) {
		 // validar token 
		    String usuario = extraerUsuarioDesdeJWT(token); 
		    if (usuario == null) {
		        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN); //fallo en token
		    }

		    // buscar Comunes con id 1, que es el unico que debe existir
		    Comunes comunes = comunesRepository.findById(1L).orElse(null);

		    if (comunes == null || comunes.getMusicaPublica() == null) {
		        //no hay música, devolver  error
		        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		    }

		    // devolver música y status ok
		    return new ResponseEntity<>(comunes.getMusicaPublica(), HttpStatus.OK);
		}
	    
	    // AÑADIR A PUBLICO
	    public ResponseEntity<String> agregarMusicaPublica(String token, String cancion) {
	        // validar token 
	        String usuario = extraerUsuarioDesdeJWT(token);
	        if (usuario == null) {
	            return new ResponseEntity<>("Token inválido o no se pudo extraer el usuario.", HttpStatus.FORBIDDEN);
	        }

	        // Recuperar música pública desde base 
	        Comunes comunes = comunesRepository.findById(1L).orElse(null); // Suponemos que solo hay un "Comunes" en base
	        if (comunes == null) {
	            return new ResponseEntity<>("No se encontró el repositorio de música pública.", HttpStatus.NOT_FOUND);
	        }

	        // Obtener la música pública
	        Musica musicaPublica = comunes.getMusicaPublica();
	        if (musicaPublica == null) {
	            return new ResponseEntity<>("No hay música pública disponible.", HttpStatus.NOT_FOUND);
	        }

	        // Convertir el JSON de la música pública en un objeto
	        ObjectMapper mapper = new ObjectMapper();
	        JsonNode root;
	        try {
	            root = mapper.readTree(musicaPublica.getMusica());
	        } catch (IOException e) {
	            return new ResponseEntity<>("Error al procesar el JSON de música pública.", HttpStatus.INTERNAL_SERVER_ERROR);
	        }

	        // Agregar la canción al JSON de música pública
	        if (root.isArray()) {
	            ((ArrayNode) root).add(cancion); // Añadimos la canción al array de la música pública
	        } else {
	            return new ResponseEntity<>("La estructura de música pública no es válida.", HttpStatus.INTERNAL_SERVER_ERROR);
	        }

	        // Guardar la música pública actualizada en la entidad
	        try {
	            musicaPublica.setMusica(mapper.writeValueAsString(root));
	            comunesRepository.save(comunes); // Guardamos los cambios
	        } catch (IOException e) {
	            return new ResponseEntity<>("Error al actualizar el repositorio de música pública.", HttpStatus.INTERNAL_SERVER_ERROR);
	        }

	        return new ResponseEntity<>("Canción añadida correctamente.", HttpStatus.OK);
	    }
	    
	    
	    // BORRAR DE PUBLICO/ADMINS
	    public ResponseEntity<String> borrarMusicaPublica(String token, String cancion) {
	        // Primero, validamos el token y comprobamos que el usuario sea válido
	        String usuario = extraerUsuarioDesdeJWT(token);
	        if (usuario == null) {
	            return new ResponseEntity<>("Token inválido o no se pudo extraer el usuario.", HttpStatus.FORBIDDEN);
	        }

	        // Verificamos si el usuario tiene permisos de administrador
	        if (!esAdmin(token)) {
	            return new ResponseEntity<>("El usuario no tiene permisos de administrador para borrar música.", HttpStatus.FORBIDDEN);
	        }

	        // Recuperar la entidad de música pública desde la base de datos
	        Comunes comunes = comunesRepository.findById(1L).orElse(null); // Suponemos que solo hay una entidad "Comunes" en la BD
	        if (comunes == null) {
	            return new ResponseEntity<>("No se encontró el repositorio de música pública.", HttpStatus.NOT_FOUND);
	        }

	        // Obtener la música pública
	        Musica musicaPublica = comunes.getMusicaPublica();
	        if (musicaPublica == null) {
	            return new ResponseEntity<>("No hay música pública disponible.", HttpStatus.NOT_FOUND);
	        }

	        // Convertir el JSON de la música pública en un objeto
	        ObjectMapper mapper = new ObjectMapper();
	        JsonNode root;
	        try {
	            root = mapper.readTree(musicaPublica.getMusica());
	        } catch (IOException e) {
	            return new ResponseEntity<>("Error al procesar el JSON de música pública.", HttpStatus.INTERNAL_SERVER_ERROR);
	        }

	        // Borrar la canción del JSON de música pública
	        if (root.isArray()) {
	            boolean songRemoved = false;
	            ArrayNode arrayNode = (ArrayNode) root;
	            Iterator<JsonNode> iterator = arrayNode.iterator();
	            while (iterator.hasNext()) {
	                JsonNode song = iterator.next();
	                if (song.asText().equals(cancion)) {
	                    iterator.remove(); // Eliminamos la canción
	                    songRemoved = true;
	                    break;
	                }
	            }
	            if (!songRemoved) {
	                return new ResponseEntity<>("La canción no fue encontrada en la música pública.", HttpStatus.NOT_FOUND);
	            }
	        } else {
	            return new ResponseEntity<>("La estructura de música pública no es válida.", HttpStatus.INTERNAL_SERVER_ERROR);
	        }

	        // Guardar la música pública actualizada en la entidad
	        try {
	            musicaPublica.setMusica(mapper.writeValueAsString(root));
	            comunesRepository.save(comunes); // Guardamos los cambios
	        } catch (IOException e) {
	            return new ResponseEntity<>("Error al actualizar el repositorio de música pública.", HttpStatus.INTERNAL_SERVER_ERROR);
	        }

	        return new ResponseEntity<>("Canción borrada correctamente.", HttpStatus.OK);
	    }

	    
	    
	  
	    //     Método para comprobar si el token pertenece a un usuario admin.
	    // Se valida el token con el método decoder y luego se extrae el campo "Admin" del payload.
	     
	    private static final String SECRET = "BoqueronesConVinagre";
	    public boolean esAdmin(String token) {
	        // Primero, validamos la firma del token.
	        if (!decoder(token, SECRET)) {
	            return false;
	        }
	        // Decodificamos el payload para extraer el campo "Admin".
	        String[] partes = token.split("\\.");
	        if (partes.length != 3) {
	            return false;
	        }
	        String payloadJson = new String(Base64.getUrlDecoder().decode(partes[1]));
	        Pattern pattern = Pattern.compile("\"Admin\":\\s*(true|false)");
	        Matcher matcher = pattern.matcher(payloadJson);
	        if (matcher.find()) {
	            return Boolean.parseBoolean(matcher.group(1));
	        }
	        return false;
	    }
	    
	    // Método para validar la firma del token
	    public boolean decoder(String recibido, String secret) {
	        String[] partes = recibido.split("\\.");
	        if (partes.length != 3) {
	            return false;
	        }
	        String header = new String(Base64.getUrlDecoder().decode(partes[0]));
	        String payload = new String(Base64.getUrlDecoder().decode(partes[1]));
	        String firmaRehecha = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret)
	                .hmacHex(partes[0] + "." + partes[1]);
	        String firmaOriginal = partes[2];
	        return firmaOriginal.equals(firmaRehecha);
	    }
	    // Método para extraer el usuario desde el token
	    public String extraerUsuarioDesdeJWT(String token) {
	        try {
	            String[] partes = token.split("\\.");
	            if (partes.length != 3) {
	                return null;
	            }
	            String payloadJson = new String(Base64.getUrlDecoder().decode(partes[1]));
	            ObjectMapper objectMapper = new ObjectMapper();
	            JsonNode payloadNode = objectMapper.readTree(payloadJson);
	            return payloadNode.get("Usuario").asText();
	        } catch (Exception e) {
	            return null;
	        }
	    }
}
