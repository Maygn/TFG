package musica;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MusicaRepository extends JpaRepository<Musica, String> {

    // Spring Data JPA will generate the method automatically based on the method signature


	Musica findByUsuario(String usuario);
}