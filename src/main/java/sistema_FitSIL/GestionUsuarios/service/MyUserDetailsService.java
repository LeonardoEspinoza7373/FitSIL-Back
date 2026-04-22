package sistema_FitSIL.GestionUsuarios.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import sistema_FitSIL.GestionUsuarios.model.Administrador;
import sistema_FitSIL.GestionUsuarios.model.Usuario;
import sistema_FitSIL.GestionUsuarios.repository.AdministradorRepository;
import sistema_FitSIL.GestionUsuarios.repository.UsuarioRepository;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private AdministradorRepository adminRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {

        Optional<Administrador> admin = adminRepo.findByCorreo(correo);
        if (admin.isPresent()) {
            return User.builder()
                    .username(admin.get().getCorreo())
                    .password(admin.get().getContrasenia())
                    .roles(admin.get().getRol().name())
                    .build();
        }

        Optional<Usuario> user = usuarioRepo.findByCorreo(correo);
        if (user.isPresent()) {
            return User.builder()
                    .username(user.get().getCorreo())
                    .password(user.get().getContrasenia())
                    .roles(user.get().getRol().name())
                    .build();
        }

        throw new UsernameNotFoundException("Usuario no encontrado");
    }
}


