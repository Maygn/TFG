package musica;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/musica") // Ruta base para este controller
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class MusicaController {

    @Autowired
    private MusicaService musicaService;
    @Autowired
    private MusicaRepository musicaRepository;
	
    // Asignar json a nuevo usuario
    @PostMapping("/nuevo")
    public ResponseEntity<String> crearJson(@RequestParam String token, @RequestParam String defJson) {
        // Buscar user en el token
        String usuario = extraerUsuarioDesdeJWT(token);
        // Se usa responseentity porque deja manipular el tipo de error.
        try { // Si todo bien
        	return new ResponseEntity<String>(musicaService.guardarCuenta(usuario, defJson).getUsuario() + " guardado correctamente.", HttpStatus.OK);
        } catch(DataIntegrityViolationException e) { // Si ya está
            return new ResponseEntity<String>("Ya tenemos ese usuario", HttpStatus.BAD_REQUEST);
        }
    }

    // Recuperar json de usuario
    @GetMapping("/buscar")
    public ResponseEntity<String> verJson(@RequestHeader("Authorization") String token) { //Todo metodo DE TOKEN tira de requestheader
        try {
            // Sacar usuario desde el JWT
            String usuario = extraerUsuarioDesdeJWT(token);
            // Buscar cuenta 
            Musica c1 = musicaService.buscarPorNombre(usuario);
            // Si la cuenta existe
            if (c1 == null) {
                return new ResponseEntity<>("No hemos encontrado esa cuenta.", HttpStatus.NOT_FOUND);
            }
            
            // Obtener y devolver la música 
            String musica = c1.getMusica();
            // Si no hay música asociada
            if (musica == null || musica.isEmpty()) {
                return new ResponseEntity<>("No se encontró música para el usuario: " + usuario, HttpStatus.NOT_FOUND);
            }
            // Si todo está bien
            else {
                return new ResponseEntity<>(musica, HttpStatus.OK);
            }
        } catch (IllegalArgumentException e) {
            // Error en el token
            return new ResponseEntity<>("Error con el usuario. Inicia sesión de nuevo", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            // Cualquier otro error
            return new ResponseEntity<>("Error interno en el servidor.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Modificar json de usuario
    @PutMapping("/modificar")
    public ResponseEntity<Musica> actualizarMusica(@RequestHeader("Authorization") String token, @RequestBody Musica cuentaActualizada) {
        try {
            // Extraer usuario del token
            String usuario = extraerUsuarioDesdeJWT(token);
            
            if (usuario == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Verificar si el usuario existe
            if (!musicaRepository.existsById(usuario)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Obtener la cuenta y actualizar la música
            Musica cuenta = musicaRepository.findById(usuario).get();
            cuenta.setMusica(cuentaActualizada.getMusica());
            musicaRepository.save(cuenta);

            return new ResponseEntity<>(cuenta, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Borrar un usuario del todo
    @Transactional
    @DeleteMapping("/borrar")
    public ResponseEntity<String> borrarCuenta(@RequestHeader("Authorization") String token) {
    	System.out.println("entrando en borrar musica");
        try {
            // Extraer usuario del token
            String usuario = extraerUsuarioDesdeJWT(token);
            
            if (usuario == null) {
            	System.out.println("MUSICA: Token inválido o usuario no encontrado");
                return new ResponseEntity<>("Token inválido o usuario no encontrado", HttpStatus.NOT_FOUND);
            }

            // Ver si usuario existe
            if (!musicaService.existeUsuario(usuario)) {
            	System.out.println("MUSICA:El usuario no existe");
                return new ResponseEntity<>("El usuario no existe", HttpStatus.NOT_FOUND);
            }

            // Borrar usuario
            musicaService.borrarUsuario(usuario);
            
            return new ResponseEntity<>("Usuario " + usuario + " eliminado correctamente", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar usuario", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // No necesita un endpoint porque solo lo uso desde otros métodos
    public String extraerUsuarioDesdeJWT(String token) {
        try {
            // Separar JWT en partes
            String[] partes = token.split("\\.");
            if (partes.length != 3) {
                return null; // Token inválido
            }

            // Decodificar el payload
            String payloadJson = new String(Base64.getUrlDecoder().decode(partes[1]));

            // Sacar usuario
            Pattern pattern = Pattern.compile("\"Usuario\":\\s*\"(.*?)\"");
            Matcher matcher = pattern.matcher(payloadJson);

            if (matcher.find()) {
                return matcher.group(1); // Retornar usuario
            } else {
                return null; // No hay usuario en el token
            }
        } catch (Exception e) {
            return null; // Si peta, asumimos token inválido
        }
    }
    
   
}
