package trabajo.tfg;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setUsuario("testUser");
        usuario.setContrasena(DigestUtils.sha256Hex("password"));
    }

    @Test
    void testGuardarUsuario() {
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        
        Usuario guardado = usuarioService.guardarUsuario("testUser", "password");
        
        assertNotNull(guardado);
        assertEquals("testUser", guardado.getUsuario());
        assertEquals(DigestUtils.sha256Hex("password"), guardado.getContrasena());
    }

    @Test
    void testObtenerUsuarioPorNombre() {
        when(usuarioRepository.findByUsuario("testUser")).thenReturn(usuario);
        
        Usuario obtenido = usuarioService.obtenerUsuarioPorNombre("testUser");
        
        assertNotNull(obtenido);
        assertEquals("testUser", obtenido.getUsuario());
    }

    @Test
    void testCambiarContrasena() {
        when(usuarioRepository.findByUsuario("testUser")).thenReturn(usuario);

        usuarioService.cambiarContrasena("newPassword", "testUser");

        verify(usuarioRepository).save(usuario);
        assertEquals(DigestUtils.sha256Hex("newPassword"), usuario.getContrasena()); 
    }


    @Test
    void testContrasenaCorrecta() {
        when(usuarioRepository.findByUsuario("testUser")).thenReturn(usuario);
        
        assertTrue(usuarioService.contrasenaCorrecta("testUser", "password"));
        assertFalse(usuarioService.contrasenaCorrecta("testUser", "wrongPassword"));
    }

    @Test
    void testBorrarUsuario() {
        doNothing().when(usuarioRepository).deleteByUsuario("testUser");
        
        usuarioService.borrarUsuario("testUser");
        
        verify(usuarioRepository, times(1)).deleteByUsuario("testUser");
    }
}
