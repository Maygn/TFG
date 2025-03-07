package sonidos;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;



public interface SonidoRepository extends JpaRepository<Sonido, Long>{

	

	 
	 @Transactional  //o se borran todos o se cancela todo, pero no se borran la mitad si peta parte del proceso
	    void deleteByUsuario(String usuario);
	 
	 	List<Sonido> findByUsuario(String usuario);
	    boolean existsByUsuario(String usuario);
	}


