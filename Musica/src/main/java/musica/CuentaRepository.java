package musica;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, String> {

    // Spring Data JPA will generate the method automatically based on the method signature
    Cuenta findByNombre(String nombre);
}