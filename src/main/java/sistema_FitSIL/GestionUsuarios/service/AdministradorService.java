package sistema_FitSIL.GestionUsuarios.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import sistema_FitSIL.GestionUsuarios.model.Administrador;
import sistema_FitSIL.GestionUsuarios.model.Rol;
import sistema_FitSIL.GestionUsuarios.model.Usuario;
import sistema_FitSIL.GestionUsuarios.repository.AdministradorRepository;
import sistema_FitSIL.GestionUsuarios.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AdministradorService {

    @Autowired
    private AdministradorRepository adminRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 🔐 LOGIN ADMIN (CORREGIDO)
    public Optional<Administrador> login(String correo, String contrasenia) {
        return adminRepository.findByCorreo(correo)  // ✅ Buscar en adminRepository
                .filter(admin ->
                        passwordEncoder.matches(contrasenia, admin.getContrasenia())
                );
    }

    // ➕ REGISTRAR ADMIN
    public Administrador registrarAdmin(Administrador admin) {
        // Verificar si el correo ya existe
        if (adminRepository.findByCorreo(admin.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }
        
        admin.setRol(Rol.ADMINISTRADOR);
        return adminRepository.save(admin);
    }

    // ✏️ ACTUALIZAR ADMIN
    public Administrador actualizarAdmin(String correo, Administrador datos) {

        Administrador admin = adminRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

        if (datos.getNombre() != null && !datos.getNombre().isEmpty())
            admin.setNombre(datos.getNombre());

        if (datos.getApellido() != null && !datos.getApellido().isEmpty())
            admin.setApellido(datos.getApellido());

        if (datos.getTelefono() != null && !datos.getTelefono().isEmpty())
            admin.setTelefono(datos.getTelefono());

        if (datos.getDepartamento() != null && !datos.getDepartamento().isEmpty())
            admin.setDepartamento(datos.getDepartamento());

        // ✅ Solo encriptar si se proporciona nueva contraseña
        if (datos.getContrasenia() != null && !datos.getContrasenia().isEmpty()) {
            admin.setContrasenia(
                    passwordEncoder.encode(datos.getContrasenia())
            );
        }

        return adminRepository.save(admin);
    }

    // 🗑️ ELIMINAR ADMIN
    public void eliminarAdmin(String correo) {
        Administrador admin = adminRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));
        adminRepository.delete(admin);
    }

    // 👥 LISTAR USUARIOS
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // 🔄 CAMBIAR ROL
    public Usuario cambiarRol(String correo, String rol) {

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setRol(Rol.valueOf(rol));
        return usuarioRepository.save(usuario);
    }

    // 🗑️ ELIMINAR USUARIO
    public void eliminarUsuario(String correo) {

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado: " + correo)
                );

        usuarioRepository.delete(usuario);
    }

    // 📊 ESTADÍSTICAS
    public String estadisticas() {

        long totalUsuarios = usuarioRepository.count();
        long totalAdmins = adminRepository.count();

        return String.format(
                "{\"total\":%d,\"administradores\":%d,\"usuarios\":%d}",
                totalUsuarios + totalAdmins,
                totalAdmins,
                totalUsuarios
        );
    }
}