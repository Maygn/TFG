package musica;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

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
    	String jsonString = "{"
    		    + " \"Boss\": [\"Malvado\", \"Antiheroe\", \"Asesino\"],"
    		    + " \"Ciudad\": [\"Pobre\", \"Desierto\", \"Ladrones\", \"Steampunk\"],"
    		    + " \"Exploracion\": [\"Ruinas\", \"Bosque\", \"Mazmorra\"],"
    		    + " \"Combate\": [\"Taberna\", \"Campamento\", \"Plano Astral\"],"
    		    + " \"Descanso\": [\"Nubes\", \"Sol\", \"Noche\"]"
    		    + "}";     
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
