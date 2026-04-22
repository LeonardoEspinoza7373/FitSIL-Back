package sistema_FitSIL.GestionUsuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sistema_FitSIL.GestionUsuarios.model.Administrador;

import java.util.Optional;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {

    Optional<Administrador> findByCorreo(String correo);

}
