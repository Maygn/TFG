package musica;
import java.io.File;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;



@Service
public class CuentaService {
	private static final String UPLOAD_DIR = "C:\\Users\\May\\OneDrive\\Escritorio\\tfg htmls\\SonidosMp3";
	@Autowired
	private Cuenta cuenta;
	@Autowired
	private CuentaRepository cuentaRepository;
	
	// Encontrar una cuenta por su nombre
    public Cuenta buscarPorNombre(String nombre) {
    	return cuentaRepository.findByNombre(nombre);
    }
	
    public String defaultJson() {
    	 String jsonString = "{ \"a1\": { \"11\": [\"string1\", \"string2\", \"string3\"], \"12\": [\"string4\", \"string5\", \"string6\"], \"13\": [\"string7\", \"string8\", \"string9\"], \"14\": [\"string10\", \"string11\", \"string12\"], \"15\": [\"string13\", \"string14\", \"string15\"] }, \"a2\": { \"21\": { \"211\": [\"String extra 1\", \"String extra 2\"], \"212\": [\"String extra doble 1\", \"String extra doble 2\"] }, \"22\": [\"string19\", \"string20\", \"string21\"], \"23\": [\"string22\", \"string23\", \"string24\"], \"24\": [\"string25\", \"string26\", \"string27\"], \"25\": [\"string28\", \"string29\", \"string30\"] }, \"a3\": { \"31\": [\"string31\", \"string32\", \"string33\"], \"32\": [\"string34\", \"string35\", \"string36\"], \"33\": [\"string37\", \"string38\", \"string39\"], \"34\": [\"string40\", \"string41\", \"string42\"], \"35\": [\"string43\", \"string44\", \"string45\"] }, \"a4\": { \"41\": [\"string46\", \"string47\", \"string48\"], \"42\": [\"string49\", \"string50\", \"string51\"], \"43\": [\"string52\", \"string53\", \"string54\"], \"44\": [\"string55\", \"string56\", \"string57\"], \"45\": [\"string58\", \"string59\", \"string60\"] }, \"a5\": [\"string61\", \"string62\", \"string63\"] }";
    	return jsonString;
    }
    
    
    public Cuenta guardarCuenta(String usuario, String jsonString) throws DataIntegrityViolationException {
        Cuenta nuevaCuenta = new Cuenta();
        nuevaCuenta.setUsuario(usuario);
        nuevaCuenta.setMusica(defaultJson());


        return cuentaRepository.save(nuevaCuenta);  

    }
    
    
    
}
