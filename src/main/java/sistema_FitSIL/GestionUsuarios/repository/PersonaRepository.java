package sistema_FitSIL.GestionUsuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sistema_FitSIL.GestionUsuarios.model.Persona;

import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Integer> {

    Optional<Persona> findByCorreo(String correo);

}
