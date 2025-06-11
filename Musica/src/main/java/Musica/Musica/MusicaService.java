package Musica.Musica;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

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

    // Agregar música para un usuario
    public Musica agregarMusica(String usuario, String musica) {
        // Buscamos si ya existe una música asociada a este usuario
        Musica musicaExistente = buscarPorUsuario(usuario);

        if (musicaExistente == null) {
            // Si no existe, creamos una nueva entrada
            Musica nuevaMusica = new Musica(usuario, musica);
            return musicaRepository.save(nuevaMusica); // Guardamos la nueva música
        } else {
            // Si existe, actualizamos la música del usuario
            musicaExistente.setMusica(musica);
            return musicaRepository.save(musicaExistente); // Guardamos la música actualizada
        }
    }

    // Borrar música para un usuario
    public void borrarMusica(String usuario) {
        Musica musica = buscarPorUsuario(usuario);
        if (musica != null) {
            musicaRepository.delete(musica); // Eliminamos la música
        }
    }
    
 // Verificar si el usuario ya existe
    public boolean existeUsuario(String usuario) {
        return musicaRepository.findByUsuario(usuario) != null; 
    }
    
    
    //METODOS PARA EL REPO COMUN
    @PostConstruct
    public Musica inicializarComunes() {
        // Buscar si ya existe el registro "Comunes"
        Optional<Musica> comunesOpt = musicaRepository.findOptionalByUsuario("Comunes");

        if (comunesOpt.isPresent()) {
            return comunesOpt.get(); // Ya existe, lo devolvemos
        }

        // Crear nuevo objeto si no existe
        Musica comunes = new Musica();
        comunes.setUsuario("Comunes");
        // Aquí puedes inicializar otros campos si es necesario

        // Guardar en la base de datos
        comunes = musicaRepository.save(comunes);
        
        // Devolvemos el comunes recien creado
        return comunes;
    }
    
    public Musica actualizarMusicaComunes(String usuario, String nuevoJson) {
        Musica comunes = buscarPorUsuario("Comunes");
        if (comunes == null) {
            comunes = new Musica("Comunes", nuevoJson);
        } else {
            comunes.setMusica(nuevoJson);
        }
        return musicaRepository.save(comunes);
    }

    
}
