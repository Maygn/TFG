package trabajo.tfg;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;


@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {


	    @InjectMocks
	    private UsuarioController usuarioController;
	    @Mock
	    private UsuarioService usuarioService;
	    @Mock
	    private UsuarioRepository usuarioRepository;
	    @Test
	    public void testGuardarUsuario_Exitoso() {
	        String usuario = "testUser";
	        String contrasena = "password123";

	        Usuario usuarioMock = new Usuario();
	        usuarioMock.setUsuario(usuario);

	        Mockito.when(usuarioService.guardarUsuario(usuario, contrasena)).thenReturn(usuarioMock);

	        ResponseEntity<String> response = usuarioController.guardarUsuario(usuario, contrasena);

	        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
	        Assertions.assertEquals(usuario + " guardado en BDD", response.getBody());
	    }

	    @Test
	    public void testGuardarUsuario_Error() {
	        String usuario = "testUser";
	        String contrasena = "password123";

	        Mockito.when(usuarioService.guardarUsuario(usuario, contrasena))
	               .thenThrow(DataIntegrityViolationException.class);

	        ResponseEntity<String> response = usuarioController.guardarUsuario(usuario, contrasena);

	        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	        Assertions.assertEquals("Ya tenemos ese usuario", response.getBody());
	    }

	    @Test
	    public void testObtenerUsuario_Exitoso() {
	        String nombreUsuario = "testUser";
	        Usuario usuarioMock = new Usuario();
	        usuarioMock.setUsuario(nombreUsuario);

	        Mockito.when(usuarioService.obtenerUsuarioPorNombre(nombreUsuario)).thenReturn(usuarioMock);

	        ResponseEntity<String> response = usuarioController.obtenerUsuario(nombreUsuario);

	        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
	        Assertions.assertNotNull(response.getBody()); // Verifica que el cuerpo no es nulo
	        Assertions.assertTrue(response.getBody().matches("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$")); // Verifica que el body tiene formato JWT
	    }

	        @Test
	        public void testObtenerUsuario_NoExistente() {
	            String nombreUsuario = "nonExistingUser";

	            Mockito.when(usuarioService.obtenerUsuarioPorNombre(nombreUsuario)).thenReturn(null);

	            ResponseEntity<String> response = usuarioController.obtenerUsuario(nombreUsuario);

	            Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	            Assertions.assertEquals("Usuario no encontrado", response.getBody());
	        }
	        @Test
	        public void testVerificarUsuario_Exitoso() {
	            String usuario = "testUser";
	            String contrasena = "password123";
	            Usuario usuarioMock = new Usuario();
	            usuarioMock.setUsuario(usuario);

	            Mockito.when(usuarioRepository.findByUsuario(usuario)).thenReturn(usuarioMock);
	            Mockito.when(usuarioService.contrasenaCorrecta(usuario, contrasena)).thenReturn(true);

	            ResponseEntity<String> response = usuarioController.verificarUsuario(usuario, contrasena);

	            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
	            Assertions.assertEquals("Bienvenido.", response.getBody());
	        }

	        @Test
	        public void testVerificarUsuario_Fallido() {
	            String usuario = "testUser";
	            String contrasena = "wrongPassword";
	            Usuario usuarioMock = new Usuario();
	            usuarioMock.setUsuario(usuario);

	            Mockito.when(usuarioRepository.findByUsuario(usuario)).thenReturn(usuarioMock);
	            Mockito.when(usuarioService.contrasenaCorrecta(usuario, contrasena)).thenReturn(false);

	            ResponseEntity<String> response = usuarioController.verificarUsuario(usuario, contrasena);

	            Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	            Assertions.assertEquals("Contraseña incorrecta.", response.getBody());
	        }

	        @Test
	        public void testVerificarUsuario_NoExistente() {
	            String usuario = "nonExistingUser";
	            String contrasena = "password123";

	            Mockito.when(usuarioRepository.findByUsuario(usuario)).thenReturn(null);

	            ResponseEntity<String> response = usuarioController.verificarUsuario(usuario, contrasena);

	            Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	            Assertions.assertEquals("El usuario introducido no existe.", response.getBody());
	        }

	        
}