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
    
    //METODOS PARA MUSICA PUBLICA
    
    public Musica borrarCancionPublica(String token, String ruta, String cancion) {
        // Validar que el token es correcto y que el usuario es admin.
        if (!esAdmin(token)) {
            throw new IllegalArgumentException("No tienes permisos de admin para borrar canciones.");
        }
        
        // Recuperar la instancia estática de música pública.
        Musica musicaPublica = Musica.getMusicaPublica();
        if (musicaPublica == null) {
            throw new IllegalArgumentException("No existe música pública en el sistema.");
        }
        
        String musicaJson = musicaPublica.getMusica();
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Convertir el String en un JsonNode para navegar y modificar el contenido.
            JsonNode root = mapper.readTree(musicaJson);
            
            // Navegar por el JSON utilizando la ruta (por ejemplo: "a1,11" para acceder a root.get("a1").get("11")).
            String[] keys = ruta.split(",");
            JsonNode nodoActual = root;
            for (String key : keys) {
                if (nodoActual.has(key)) {
                    nodoActual = nodoActual.get(key);
                } else {
                    throw new IllegalArgumentException("La ruta especificada no existe en el JSON.");
                }
            }
            
            // Verificar que el nodo obtenido es un array de canciones.
            if (!nodoActual.isArray()) {
                throw new IllegalArgumentException("La ruta indicada no corresponde a una lista de canciones.");
            }
            
            // Convertir el nodo a ArrayNode para poder eliminar la canción.
            ArrayNode cancionesArray = (ArrayNode) nodoActual;
            boolean eliminado = false;
            for (int i = 0; i < cancionesArray.size(); i++) {
                if (cancionesArray.get(i).asText().equals(cancion)) {
                    cancionesArray.remove(i);
                    eliminado = true;
                    break;
                }
            }
            if (!eliminado) {
                throw new IllegalArgumentException("La canción especificada no se encontró en la ruta indicada.");
            }
            
            // Volver a convertir el árbol JSON a String y actualizar la entidad.
            String nuevoJson = mapper.writeValueAsString(root);
            musicaPublica.setMusica(nuevoJson);
            return musicaPublica;
        } catch (IOException e) {
            throw new RuntimeException("Error al procesar el JSON de música.", e);
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
    public Musica agregarCancion(String token, String categoriaPublica, String categoriaPrivada, String cancion) {
        // Extraer el usuario a partir del token (se utiliza el método copiado)
        String usuario = extraerUsuarioDesdeJWT(token);
        if (usuario == null) {
            throw new IllegalArgumentException("Token inválido, no se pudo extraer el usuario.");
        }

        // Recuperar la música privada del usuario
        Musica musicaPrivada = musicaRepository.findByUsuario(usuario);
        if (musicaPrivada == null) {
            throw new IllegalArgumentException("No se encontró música privada para el usuario " + usuario);
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode privateRoot;
        try {
            privateRoot = mapper.readTree(musicaPrivada.getMusica());
        } catch (IOException e) {
            throw new RuntimeException("Error al procesar el JSON de música privada.", e);
        }

        // Navegar en el JSON de música privada usando la categoría privada
        JsonNode nodoPrivado = privateRoot.get(categoriaPrivada);
        if (nodoPrivado == null || !nodoPrivado.isArray()) {
            throw new IllegalArgumentException("La categoría privada especificada no existe o no contiene canciones.");
        }

        // Verificar que la canción existe en la categoría privada
        boolean encontrada = false;
        for (JsonNode n : nodoPrivado) {
            if (n.asText().equals(cancion)) {
                encontrada = true;
                break;
            }
        }
        if (!encontrada) {
            throw new IllegalArgumentException("La canción especificada no se encuentra en tu música privada.");
        }

        // Obtener la música pública (entidad estática)
        Musica musicaPublica = Musica.getMusicaPublica();
        if (musicaPublica == null) {
            throw new IllegalArgumentException("No existe música pública en el sistema.");
        }

        JsonNode publicRoot;
        try {
            publicRoot = mapper.readTree(musicaPublica.getMusica());
        } catch (IOException e) {
            throw new RuntimeException("Error al procesar el JSON de música pública.", e);
        }

        // Se asume que el JSON público es un objeto; se convierte a ObjectNode para poder modificarlo.
        if (!(publicRoot instanceof ObjectNode)) {
            throw new RuntimeException("La estructura de la música pública no es la esperada.");
        }
        ObjectNode publicRootObject = (ObjectNode) publicRoot;
        ArrayNode publicCategory;
        // Verificar si la categoría pública ya existe
        if (publicRootObject.has(categoriaPublica)) {
            JsonNode node = publicRootObject.get(categoriaPublica);
            if (node.isArray()) {
                publicCategory = (ArrayNode) node;
            } else {
                throw new IllegalArgumentException("La categoría pública especificada no es un array.");
            }
        } else {
            // Si no existe, se crea una nueva categoría (array)
            publicCategory = mapper.createArrayNode();
            publicRootObject.set(categoriaPublica, publicCategory);
        }

        // Añadir la canción al array de la categoría pública
        publicCategory.add(cancion);

        try {
            String nuevoJsonPublico = mapper.writeValueAsString(publicRootObject);
            musicaPublica.setMusica(nuevoJsonPublico);
            return musicaPublica;
        } catch (IOException e) {
            throw new RuntimeException("Error al actualizar la música pública.", e);
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
