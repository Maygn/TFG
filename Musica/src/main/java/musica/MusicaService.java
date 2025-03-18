package musica;

import java.io.IOException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class MusicaService {

    @Autowired
    private MusicaRepository musicaRepository;

  

    // Encontrar una música por el usuario (cambiamos 'nombre' por 'usuario')
    public Musica buscarPorUsuario(String usuario) {
        return musicaRepository.findByUsuario(usuario); // Método que debes tener en el repositorio
    }

    public String defaultJson() {
        // Genera un JSON por defecto para el campo 'musica'
        String jsonString = "{ \"a1\": { \"11\": [\"string1\", \"string2\", \"string3\"], \"12\": [\"string4\", \"string5\", \"string6\"], \"13\": [\"string7\", \"string8\", \"string9\"], \"14\": [\"string10\", \"string11\", \"string12\"], \"15\": [\"string13\", \"string14\", \"string15\"] }, \"a2\": { \"21\": { \"211\": [\"String extra 1\", \"String extra 2\"], \"212\": [\"String extra doble 1\", \"String extra doble 2\"] }, \"22\": [\"string19\", \"string20\", \"string21\"], \"23\": [\"string22\", \"string23\", \"string24\"], \"24\": [\"string25\", \"string26\", \"string27\"], \"25\": [\"string28\", \"string29\", \"string30\"] }, \"a3\": { \"31\": [\"string31\", \"string32\", \"string33\"], \"32\": [\"string34\", \"string35\", \"string36\"], \"33\": [\"string37\", \"string38\", \"string39\"], \"34\": [\"string40\", \"string41\", \"string42\"], \"35\": [\"string43\", \"string44\", \"string45\"] }, \"a4\": { \"41\": [\"string46\", \"string47\", \"string48\"], \"42\": [\"string49\", \"string50\", \"string51\"], \"43\": [\"string52\", \"string53\", \"string54\"], \"44\": [\"string55\", \"string56\", \"string57\"], \"45\": [\"string58\", \"string59\", \"string60\"] }, \"a5\": [\"string61\", \"string62\", \"string63\"] }";
        return jsonString;
    }

    public Musica guardarCuenta(String usuario, String defJson) {
        // Crear una nueva instancia de Musica con los datos proporcionados
        Musica musica = new Musica();
        musica.setUsuario(usuario);
        musica.setMusica(defJson); // Asumiendo que "musica" es el campo donde se guarda el JSON

        try {
            // Guardar la cuenta en la base de datos
            return musicaRepository.save(musica);  // Cambio: usar musicaRepository
        } catch (DataIntegrityViolationException e) {
            throw e; // Propagar excepción si ya existe un usuario con ese nombre
        }
    }

    // Método para buscar música por el nombre de usuario
    public Musica buscarPorNombre(String usuario) {
        // Este método utiliza el repositorio para buscar la cuenta por el nombre de usuario
        return musicaRepository.findByUsuario(usuario);  // Suponiendo que tienes un método findByUsuario en tu repositorio
    }
    
    // Verificar si el usuario ya existe
    public boolean existeUsuario(String usuario) {
        return musicaRepository.findByUsuario(usuario) != null; // Cambié 'nombre' por 'usuario'
    }

    // Borrar una cuenta de música por el usuario
    public void borrarUsuario(String usuario) {
        Musica cuenta = musicaRepository.findByUsuario(usuario); // Cambié 'nombre' por 'usuario'
        if (cuenta != null) {
            musicaRepository.delete(cuenta); // Eliminar del repositorio
        }
    }
   
    
    
   // Método para extraer el usuario desde el token JWT.
     
    public String extraerUsuarioDesdeJWT(String token) {
        try {
            String[] partes = token.split("\\.");
            if (partes.length != 3) {
                return null;
            }
            String payloadJson = new String(Base64.getUrlDecoder().decode(partes[1]));
            Pattern pattern = Pattern.compile("\"Usuario\":\\s*\"(.*?)\"");
            Matcher matcher = pattern.matcher(payloadJson);
            return matcher.find() ? matcher.group(1) : null;
        } catch (Exception e) {
            return null;
        }
    }
  
    

    

     //Método  para validar la firma del token.
    
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
    
    }
