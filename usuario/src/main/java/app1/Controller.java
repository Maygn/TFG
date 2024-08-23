package app1;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class Controller {
	
	@GetMapping(value="/darUsuario")
		public Usuario hacerUsuario() {
			Usuario u1= new Usuario("nombre", "correo", 1);
			
			return u1;
		
		}
}
