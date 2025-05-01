package musica;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ComunesRepository extends MongoRepository<Comunes, Long> {
    Optional<Comunes> findById(Long id);
}
