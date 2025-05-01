package sonidos;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;




public interface SonidoRepository extends MongoRepository<Sonido, Long>{

	

	    void deleteByUsuario(String usuario);
	 
	 	List<Sonido> findByUsuario(String usuario);
	    boolean existsByUsuario(String usuario);
	}


