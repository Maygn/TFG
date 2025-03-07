package trabajo.tfg;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
  
	//el método lo autogenera el jpa
    Usuario findByUsuario(String usuario);
    
    void deleteByUsuario(String usuario);
}