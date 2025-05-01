package musica;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class ComunesInitializer {

    @Autowired
    private ComunesRepository comunesRepository;

    @PostConstruct
    public void inicializarComunes() {
        comunesRepository.findById(1L).orElseGet(() -> {
            Comunes comunes = new Comunes();
            comunes.setId(1L); // Asegurar que es el único registro
            // Aquí puedes inicializar otros campos si es necesario
            comunesRepository.save(comunes);
            System.out.println("Se ha creado el registro único de Comunes con ID 1.");
            return comunes;
        });
    }
}
