package sistema_FitSIL.EstadisticaReporte.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sistema_FitSIL.EstadisticaReporte.model.Estadistica;
import sistema_FitSIL.GestionUsuarios.model.Usuario;

import java.util.List;

@Repository
public interface EstadisticaRepository extends JpaRepository<Estadistica, Long> {
    List<Estadistica> findByUsuario(Usuario usuario);
}
