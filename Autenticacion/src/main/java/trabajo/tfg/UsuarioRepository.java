package trabajo.tfg;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsuarioRepository extends MongoRepository<Usuario, UUID> {
  
	//el método lo autogenera el jpa
    Usuario findByUsuario(String usuario);
    
    void deleteByUsuario(String usuario);
}