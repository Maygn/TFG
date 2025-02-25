package musica;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:5500")
@RestController
@RequestMapping("/musica") // Ruta base para este controlador
public class CuentaController {


	   @Autowired
	    private CuentaService cuentaService;
	   @Autowired
	    private CuentaRepository cuentaRepository;
	
	//asignar json a nuevo usuario
	@PostMapping("/nuevo")
	public ResponseEntity<String> crearJson(@RequestParam String usuario, @RequestParam String defJson) {
	
		 
		try { //se usa responseentity porque deja manipular el tipo de error.
	        return new ResponseEntity<String>(cuentaService.guardarCuenta(usuario, defJson).getUsuario()+" guardado en BDD",HttpStatus.OK); 
    	}
    	catch(DataIntegrityViolationException e) {
    		return new ResponseEntity<String>("Ya tenemos ese usuario",HttpStatus.BAD_REQUEST);
    	}
		

	}
	//recuperar json de usuario
	@GetMapping("/buscar/{usuario}")
	public String verJson(@PathVariable String usuario) {
		Cuenta c1= cuentaService.buscarPorNombre(usuario);
		String musica= c1.musica;
		return musica;
	}
	//modificar json de usuario
	@PutMapping("modificar/{usuario}")
    public ResponseEntity<Cuenta> actualizarMusica(
            @PathVariable String usuario,
            @RequestBody Cuenta cuentaActualizada) {

        if (!cuentaRepository.existsById(usuario)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Cuenta cuenta = cuentaRepository.findById(usuario).get();
        cuenta.setMusica(cuentaActualizada.getMusica());
        cuentaRepository.save(cuenta);

        return new ResponseEntity<>(cuenta, HttpStatus.OK);
    }
	
	
	//borrar un usuario del todo
	@PostMapping("/borrarUsuario")
	
	public String borrarUsuario(String usuario) {
		return "";
	}
}
