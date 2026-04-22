package sistema_FitSIL.GestionUsuarios.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import sistema_FitSIL.GestionUsuarios.model.Administrador;
import sistema_FitSIL.GestionUsuarios.model.Usuario;
import sistema_FitSIL.GestionUsuarios.repository.AdministradorRepository;
import sistema_FitSIL.GestionUsuarios.repository.UsuarioRepository;

import java.util.Collections;
import java.util.Optional;

@Service
public class DbUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        System.out.println("🔍 Buscando usuario con correo: " + correo);

        // Buscar primero en usuarios
        Optional<Usuario> usuario = usuarioRepository.findByCorreo(correo);
        if (usuario.isPresent()) {
            Usuario u = usuario.get();
            String rol = "ROLE_" + u.getRol().name(); // ✅ Asegurar prefijo ROLE_
            
            System.out.println("✅ Usuario encontrado: " + correo);
            System.out.println("👤 Rol: " + rol);
            
            return User.builder()
                    .username(u.getCorreo())
                    .password(u.getContrasenia())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority(rol)))
                    .build();
        }

        // Si no es usuario, buscar en administradores
        Optional<Administrador> administrador = administradorRepository.findByCorreo(correo);
        if (administrador.isPresent()) {
            Administrador a = administrador.get();
            String rol = "ROLE_" + a.getRol().name(); // ✅ Asegurar prefijo ROLE_
            
            System.out.println("✅ Administrador encontrado: " + correo);
            System.out.println("👤 Rol: " + rol);
            
            return User.builder()
                    .username(a.getCorreo())
                    .password(a.getContrasenia())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority(rol)))
                    .build();
        }

        System.err.println("❌ Usuario no encontrado: " + correo);
        throw new UsernameNotFoundException("Usuario no encontrado: " + correo);
    }
}