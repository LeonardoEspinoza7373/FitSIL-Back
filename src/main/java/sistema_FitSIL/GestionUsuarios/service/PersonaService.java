package sistema_FitSIL.GestionUsuarios.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sistema_FitSIL.GestionUsuarios.model.Persona;
import sistema_FitSIL.GestionUsuarios.repository.PersonaRepository;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder;

    public PersonaService(PersonaRepository personaRepository,
                          PasswordEncoder passwordEncoder) {
        this.personaRepository = personaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Persona> login(String correo, String contrasenia) {
        return personaRepository.findByCorreo(correo)
                .filter(p ->
                        passwordEncoder.matches(
                                contrasenia,
                                p.getContrasenia()
                        )
                );
    }

    public Persona guardarPersona(Persona persona) {
        // ✅ Encriptar contraseña antes de guardar
        persona.setContrasenia(
                passwordEncoder.encode(persona.getContrasenia())
        );
        return personaRepository.save(persona);
    }

    public List<Persona> obtenerTodosLasPersonas() {
        return personaRepository.findAll();
    }
}