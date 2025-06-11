package Musica.Musica;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicaRepository extends MongoRepository<Musica, String> {
	
    Musica findByUsuario(String usuario);
    Optional<Musica> findOptionalByUsuario(String usuario);
}
