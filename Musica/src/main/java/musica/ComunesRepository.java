package musica;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ComunesRepository extends JpaRepository<Comunes, Long>{
	 Optional<Comunes> findById(Long id);

}
