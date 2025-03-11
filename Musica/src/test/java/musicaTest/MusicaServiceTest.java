package musicaTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import musica.Musica;
import musica.MusicaRepository;
import musica.MusicaService;

@ExtendWith(MockitoExtension.class)
public class MusicaServiceTest {

    @InjectMocks
    private MusicaService musicaService;

    @Mock
    private MusicaRepository musicaRepository;

    private Musica musica;

    @BeforeEach
    void setUp() {
        musica = new Musica();
        musica.setUsuario("testUser");
        musica.setMusica(musicaService.defaultJson());
    }

    @Test
    public void testBuscarPorNombre_UsuarioExiste() {
        when(musicaRepository.findByNombre("testUser")).thenReturn(musica);

        Musica resultado = musicaService.buscarPorNombre("testUser");

        assertNotNull(resultado);
        assertEquals("testUser", resultado.getUsuario());
    }

    @Test
    public void testBuscarPorNombre_UsuarioNoExiste() {
        when(musicaRepository.findByNombre("nonExistentUser")).thenReturn(null);

        Musica resultado = musicaService.buscarPorNombre("nonExistentUser");

        assertNull(resultado);
    }

    @Test
    public void testGuardarCuenta_Exito() {
        when(musicaRepository.save(any(Musica.class))).thenReturn(musica);

        Musica resultado = musicaService.guardarCuenta("testUser", "{}");

        assertNotNull(resultado);
        assertEquals("testUser", resultado.getUsuario());
        assertEquals(musicaService.defaultJson(), resultado.getMusica());
    }

    @Test
    public void testGuardarCuenta_Error() {
        when(musicaRepository.save(any(Musica.class))).thenThrow(new DataIntegrityViolationException("Error"));

        assertThrows(DataIntegrityViolationException.class, () -> {
            musicaService.guardarCuenta("testUser", "{}");
        });
    }

    @Test
    public void testExisteUsuario_True() {
        when(musicaRepository.findByNombre("testUser")).thenReturn(musica);

        boolean existe = musicaService.existeUsuario("testUser");

        assertTrue(existe);
    }

    @Test
    public void testExisteUsuario_False() {
        when(musicaRepository.findByNombre("testUser")).thenReturn(null);

        boolean existe = musicaService.existeUsuario("testUser");

        assertFalse(existe);
    }

    @Test
    public void testBorrarUsuario_Exitoso() {
        when(musicaRepository.findByNombre("testUser")).thenReturn(musica);
        doNothing().when(musicaRepository).delete(musica);

        musicaService.borrarUsuario("testUser");

        verify(musicaRepository, times(1)).delete(musica);
    }

    @Test
    public void testBorrarUsuario_NoExiste() {
        when(musicaRepository.findByNombre("nonExistentUser")).thenReturn(null);

        musicaService.borrarUsuario("nonExistentUser");

        verify(musicaRepository, never()).delete(any(Musica.class));
    }
}
