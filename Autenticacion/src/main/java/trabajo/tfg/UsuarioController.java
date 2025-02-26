package trabajo.tfg;

import java.util.Base64;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")

public class UsuarioController { 
	
	 @Autowired
	    private UsuarioService usuarioService;
	 @Autowired
	 	private UsuarioRepository usuarioRepository;

	    // Endpoint para guardar un nuevo usuario

	    @PostMapping(value = "/guardar", consumes = "application/x-www-form-urlencoded")
	    
	    public ResponseEntity<String> guardarUsuario(@RequestParam String usuario, @RequestParam String contrasena) {
	    	System.out.println("usando metodo guardar!");
	    		    	//usa el wrapper para poder mandar las dos cosas, el usuario si todo va bien y el error si no
	    	try { //se usa responseentity porque deja manipular el tipo de error.
		        return new ResponseEntity<String>(usuarioService.guardarUsuario(usuario, contrasena).getUsuario()+" guardado en BDD",HttpStatus.OK); 
	    	}
	    	catch(DataIntegrityViolationException e) {
	    		return new ResponseEntity<String>("Ya tenemos ese usuario",HttpStatus.BAD_REQUEST);
	    	}
	    	
	    	
	    }

	    // Endpoint para obtener usuario por nombre //genera el url añadiendo el contenido del pathvariable en lugar del {usuario}
	    @GetMapping("/obtener/{usuario}")
	    public String obtenerUsuario(@PathVariable String usuario) {

	    	
	    	Usuario user=usuarioService.obtenerUsuarioPorNombre(usuario);
	    	
	    	LocalDateTime fechaHora= LocalDateTime.now();
	    //esto es para comprobar los credenciales, primero hago las tres piezas del jwt
	    	
	    	String jwtHeader = "{\"alg\": \"HS256\", \"typ\": \"JWT\"}";   	
	    	String jwtPayload = "{ \"Usuario\": \"" + user.getUsuario() + "\", \"Fecha\": \"" + fechaHora.toString() + "\" }";
	    	String secret="BoqueronesConVinagre";
	    //las pongo en base64 para que no de guerra en otros sistemas
	    	String jwtHeader64=Base64.getUrlEncoder().withoutPadding().encodeToString(jwtHeader.getBytes());
	    	String jwtPayload64=Base64.getUrlEncoder().withoutPadding().encodeToString(jwtPayload.getBytes());
	    //concateno las piezas y las vuelvo a traducir.
	    	String jwtHmac=new HmacUtils(HmacAlgorithms.HMAC_SHA_256,secret).hmacHex(jwtHeader+jwtPayload);
	    	String jwtHmac64=Base64.getUrlEncoder().withoutPadding().encodeToString(jwtHmac.getBytes());
	    //y las concateno una vez traducidas, y luego las envio	
	    	String enviar=jwtHeader64+"."+jwtPayload64+"."+jwtHmac64;
	    	
	        return enviar;
	    }
	    
	    public boolean decoder(String recibido, String secret) {
	    	
	        //separo en cada punto 
	    	String[] partes = recibido.split("\\.");
	    	//pongo cada cacho en un string distinto
	    	String header = new String(Base64.getUrlDecoder().decode(partes[0]));
	    	String payload = new String(Base64.getUrlDecoder().decode(partes[1]));
	    	
	    	//rehago la firma con los datos que he sacado y enviando el secret directamente
	    	String firmaRehecha=new HmacUtils(HmacAlgorithms.HMAC_SHA_256,secret).hmacHex(partes[0]+"."+partes[1]);
	    	
	    	//la firma no se decodea.
	    	String firmaOriginal= partes[2];
	    	//comparo ambas
	    	boolean correcto=false;
	    	if(firmaOriginal.equals(firmaRehecha)) {
	    		 correcto=true;
	    	}
	    	 		    
	   	    	
	    	return correcto;
	    }
	    
	   
	    @GetMapping(value = "/verificar", consumes = "application/x-www-form-urlencoded")
	    public ResponseEntity<String> verificarUsuario(@RequestParam String usuario, @RequestParam String contrasena) {
	    	System.out.println("usando metodo verificar");
	    	Usuario u = usuarioRepository.findByUsuario(usuario);
	    	
	    	
	        if (u != null) {
	        	 boolean credencialesValidas = usuarioService.contrasenaCorrecta(usuario, contrasena);
	 	        if (credencialesValidas) {
	 	            return new ResponseEntity<String>("OK", HttpStatus.OK);
	 	        } else {
	 	            return new ResponseEntity<String>("Clave incorrecta", HttpStatus.UNAUTHORIZED);
	 	        }
	        	
	        }
	        else {
	        	return new ResponseEntity<String>("El usuario introducido no existe", HttpStatus.UNAUTHORIZED);
	        }
	      
	    }
	    
	    
	    

 
}
