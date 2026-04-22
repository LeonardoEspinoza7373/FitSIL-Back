package sistema_FitSIL.GestionUsuarios.service;

import jakarta.validation.Validator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sistema_FitSIL.GestionUsuarios.model.Rol;
import sistema_FitSIL.GestionUsuarios.model.Usuario;
import sistema_FitSIL.GestionUsuarios.repository.UsuarioRepository;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    public UsuarioService(UsuarioRepository usuarioRepo,
                          PasswordEncoder passwordEncoder,
                          Validator validator) {
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    public Usuario registrarUsuario(Usuario usuario) {
        // ❌ QUITÉ ESTO - Ya viene encriptada del Controller
        // usuario.setContrasenia(passwordEncoder.encode(usuario.getContrasenia()));
        
        // Verificar si el correo ya existe
        if (usuarioRepo.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }
        
        usuario.setRol(Rol.USUARIO); // Asegurar que sea USUARIO
        return usuarioRepo.save(usuario);
    }

    public Optional<Usuario> login(String correo, String contrasenia) {
        return usuarioRepo.findByCorreo(correo)
                .filter(u -> passwordEncoder.matches(contrasenia, u.getContrasenia()));
    }

    public Optional<Usuario> obtenerPerfil(String correo) {
        return usuarioRepo.findByCorreo(correo);
    }

    public Usuario actualizarPerfil(String correo, Usuario datos) {
        Usuario u = usuarioRepo.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (datos.getNombre() != null)
            u.setNombre(datos.getNombre());
        
        if (datos.getApellido() != null)
            u.setApellido(datos.getApellido());
        
        if (datos.getTelefono() != null)
            u.setTelefono(datos.getTelefono());
        
        if (datos.getPeso() > 0)
            u.setPeso(datos.getPeso());
        
        if (datos.getAltura() > 0)
            u.setAltura(datos.getAltura());

        // ✅ Solo encriptar si se proporciona nueva contraseña
        if (datos.getContrasenia() != null && !datos.getContrasenia().isEmpty()) {
            u.setContrasenia(passwordEncoder.encode(datos.getContrasenia()));
        }

        return usuarioRepo.save(u);
    }

    public void eliminarUsuario(String correo) {
        Usuario u = usuarioRepo.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuarioRepo.delete(u);
    }
}