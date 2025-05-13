package Musica.Musica;

import org.springframework.beans.factory.annotation.Autowired;

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

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/musica") // Ruta base para este controller
@CrossOrigin(origins = "*")
public class MusicaController {

    @Autowired
    private MusicaService musicaService;

    // Asignar json a nuevo usuario
    @PostMapping("/nuevo")
    public ResponseEntity<String> crearJson(@RequestParam String token, @RequestParam String defJson) {
        // Buscar usuario en el token
        String usuario = extraerUsuarioDesdeJWT(token);
        try {
            // Guardar la música para el usuario
            Musica musicaGuardada = musicaService.agregarMusica(usuario, defJson);
            return new ResponseEntity<>(musicaGuardada.getUsuario() + " guardado correctamente.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al guardar música: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

// Recuperar json de usuario
    @GetMapping("/buscar")
    public ResponseEntity<String> verJson(@RequestHeader("Authorization") String token) {
        try {
            // Sacar usuario desde el JWT
            String usuario = extraerUsuarioDesdeJWT(token);
            // Buscar cuenta
            Musica musica = musicaService.buscarPorUsuario(usuario);
            // Si no encontramos la cuenta
            if (musica == null) {
                return new ResponseEntity<>("No hemos encontrado esa cuenta.", HttpStatus.NOT_FOUND);
            }

            // Obtener y devolver la música
            String musicaJson = musica.getMusica();
            if (musicaJson == null || musicaJson.isEmpty()) {
                return new ResponseEntity<>("No se encontró música para el usuario: " + usuario, HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(musicaJson, HttpStatus.OK);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error con el usuario. Inicia sesión de nuevo", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno en el servidor.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/modificar")
    public ResponseEntity<Musica> actualizarMusica(@RequestHeader("Authorization") String token, @RequestBody Musica cuentaActualizada) {
        try {
            String usuario = extraerUsuarioDesdeJWT(token);
            Musica cuentaActual = musicaService.agregarMusica(usuario, cuentaActualizada.getMusica());
            return new ResponseEntity<>(cuentaActual, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

// Borrar un usuario
    
    @DeleteMapping("/borrar")
    public ResponseEntity<String> borrarCuenta(@RequestHeader("Authorization") String token) {
        try {
            // Extraer usuario del token
            String usuario = extraerUsuarioDesdeJWT(token);
            
            // Verificar si el usuario existe
            if (usuario == null || !musicaService.existeUsuario(usuario)) {
                return new ResponseEntity<>("El usuario no existe", HttpStatus.NOT_FOUND);
            }

            // Borrar usuario
            musicaService.borrarMusica(usuario);
            
            return new ResponseEntity<>("Usuario " + usuario + " eliminado correctamente", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar usuario", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    //METODOS PARA REPO COMUN
    
    
    @GetMapping("/buscar/comunes")
    public ResponseEntity<String> verComunes(@RequestHeader("Authorization") String token) {
        try {
            // Sacar usuario desde el JWT
            String usuario = extraerUsuarioDesdeJWT(token);

            // Obtener o inicializar "Comunes"
            Musica musica = musicaService.inicializarComunes();

            // Obtener y devolver la música
            String musicaJson = musica.getMusica();
            if (musicaJson == null || musicaJson.isEmpty()) {
                return new ResponseEntity<>("No se encontró música para el usuario: " + usuario, HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(musicaJson, HttpStatus.OK);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error con el usuario. Inicia sesión de nuevo", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno en el servidor.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    @PutMapping("/comunes/actualizar")
    public ResponseEntity<Musica> actualizarMusicaComunes(@RequestHeader("Authorization") String token,
                                                          @RequestBody Musica musicaActualizada) {
        try {
            String usuario = extraerUsuarioDesdeJWT(token);
            if (usuario == null || usuario.isBlank()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            Musica actualizada = musicaService.actualizarMusicaComunes(usuario, musicaActualizada.getMusica());
            return new ResponseEntity<>(actualizada, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    

    @PutMapping("/comunes/borrar")
    public ResponseEntity<Void> borrarComunes(
            @RequestHeader("Authorization") String token,
            @RequestBody Musica nuevaMusica) {
        
        if (!extraerAdminDesdeJWT(token)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        musicaService.agregarMusica("comunes", nuevaMusica.getMusica());
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    
    
    
    // Método para extraer usuario del JWT
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
            return null; // Si ocurre un error, asumimos token inválido
        }
    }
    public boolean extraerAdminDesdeJWT(String token) {
        try {
            String[] partes = token.split("\\.");
            if (partes.length != 3) {
                return false;
            }
            String payloadJson = new String(Base64.getUrlDecoder().decode(partes[1]));
            Pattern pattern = Pattern.compile("\"Admin\":\\s*(true|false)");
            Matcher matcher = pattern.matcher(payloadJson);
            return matcher.find() && Boolean.parseBoolean(matcher.group(1));
        } catch (Exception e) {
            return false;
        }
    }
}

