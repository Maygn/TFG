package trabajo.tfg;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
	@Autowired
	private UsuarioRepository usuarioRepository;

	public Usuario guardarUsuario(String usuario, String contrasena, boolean admin, String claveAdminInput)
			throws DataIntegrityViolationException {
		// Si se solicita crear un usuario admin, se verifica que se haya proporcionado
		// la clave correcta
		if (admin) {
			if (claveAdminInput == null || !claveAdminInput.equals(Usuario.getClaveAdmin())) {
				throw new IllegalArgumentException("La clave admin es incorrecta");
			}
		}

		Usuario nuevoUsuario = new Usuario();
		nuevoUsuario.setUsuario(usuario);
		nuevoUsuario.setAdmin(admin);

		// Hashear la contraseña antes de guardarla
		String hashedPassword = DigestUtils.sha256Hex(contrasena);
		nuevoUsuario.setContrasena(hashedPassword);

		return usuarioRepository.save(nuevoUsuario);
	}

	// Obtener un usuario por su nombre
	public Usuario obtenerUsuarioPorNombre(String usuario) {
		return usuarioRepository.findByUsuario(usuario);
	}

	// Cambiar contraseña
	public void cambiarContrasena(String contrasena, String nombreUsuario) {
		Usuario usuario = usuarioRepository.findByUsuario(nombreUsuario);
		if (usuario != null) {
			// Hashear la nueva contraseña antes de guardarla
			usuario.setContrasena(DigestUtils.sha256Hex(contrasena));
			usuarioRepository.save(usuario);
		}
	}

	// Comprobar si la clave es correcta
	public boolean contrasenaCorrecta(String nombreUsuario, String contrasena) {
		Usuario usuario = usuarioRepository.findByUsuario(nombreUsuario);
		if (usuario != null && usuario.getContrasena().equals(DigestUtils.sha256Hex(contrasena))) {
			return true;
		}
		return false;
	}

	// Borrar usuario
	public void borrarUsuario(String usuario) {
		usuarioRepository.deleteByUsuario(usuario);
	}
	
	
	public String generarToken(Usuario usuario) {
		LocalDateTime fechaHora = LocalDateTime.now();
		String jwtHeader = "{\"alg\": \"HS256\", \"typ\": \"JWT\"}";
		String jwtPayload = "{ \"Usuario\": \"" + usuario.getUsuario() + "\", \"Admin\": " + usuario.isAdmin()
				+ ", \"Fecha\": \"" + fechaHora.toString() + "\" }";
		String secret = "BoqueronesConVinagre";
		String jwtHeader64 = Base64.getUrlEncoder().withoutPadding().encodeToString(jwtHeader.getBytes());
		String jwtPayload64 = Base64.getUrlEncoder().withoutPadding().encodeToString(jwtPayload.getBytes());
		String jwtHmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret).hmacHex(jwtHeader + jwtPayload);
		String jwtHmac64 = Base64.getUrlEncoder().withoutPadding().encodeToString(jwtHmac.getBytes());
		return jwtHeader64 + "." + jwtPayload64 + "." + jwtHmac64;
	}

	public String extraerUsuarioDesdeJWT(String token) {
		try {
			String[] partes = token.split("\\.");
			if (partes.length != 3) {
				return null;
			}
			String payloadJson = new String(Base64.getUrlDecoder().decode(partes[1]));
			Pattern pattern = Pattern.compile("\"Usuario\":\\s*\"(.*?)\"");
			Matcher matcher = pattern.matcher(payloadJson);
			return matcher.find() ? matcher.group(1) : null;
		} catch (Exception e) {
			return null;
		}
	}

	public boolean decoder(String recibido, String secret) {

		// separo en cada punto
		String[] partes = recibido.split("\\.");
		// pongo cada cacho en un string distinto
		String header = new String(Base64.getUrlDecoder().decode(partes[0]));
		String payload = new String(Base64.getUrlDecoder().decode(partes[1]));

		// rehago la firma con los datos que he sacado y enviando el secret directamente
		String firmaRehecha = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret).hmacHex(partes[0] + "." + partes[1]);

		// la firma no se decodea.
		String firmaOriginal = partes[2];
		// comparo ambas
		boolean correcto = false;
		if (firmaOriginal.equals(firmaRehecha)) {
			correcto = true;
		}

		return correcto;
	}
}
