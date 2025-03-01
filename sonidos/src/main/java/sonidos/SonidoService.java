package sonidos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;





import java.net.MalformedURLException;

@Service
public class SonidoService {
    private static final String UPLOAD_DIR = "C:/uploads/mp3/";



        @Autowired
        private SonidoRepository sonidoRepository;

        public void saveSonido(String fileName, byte[] archivoBytes2, String usuario) throws IOException {
            // archivoBytes2 ya es un array de bytes, por lo que no es necesario llamar a getBytes()
            byte[] archivoBytes = archivoBytes2;

            // Crear y guardar la entidad Sonido en la base de datos
            Sonido sonido = new Sonido();
            sonido.setNombre(fileName);
            sonido.setArchivo(archivoBytes);
            sonido.setUsuario(usuario);

            sonidoRepository.save(sonido);
        }


    public Optional<Sonido> getSonido(Long id) {
        return sonidoRepository.findById(id);
    }
   


    public Resource getFileAsResource(Long id) throws IOException {
        // Buscar el sonido por ID
        Sonido sonido = sonidoRepository.findById(id)
                .orElseThrow(() -> new IOException("No se encontró el sonido con ID: " + id));

        // Obtener el archivo como un array de bytes
        byte[] archivoMp3 = sonido.getArchivo();

        if (archivoMp3 == null) {
            throw new IOException("El archivo MP3 no está disponible para el sonido con ID: " + id);
        }

        // Crear un ByteArrayResource con el archivo MP3
        return new ByteArrayResource(archivoMp3);
    }
}
