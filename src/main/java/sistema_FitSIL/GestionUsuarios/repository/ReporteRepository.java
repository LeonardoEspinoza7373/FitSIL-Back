// ========== ReporteRepository.java ==========
package sistema_FitSIL.GestionUsuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sistema_FitSIL.GestionUsuarios.model.Reporte;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    
    // Obtener todos ordenados por fecha de creación descendente
    List<Reporte> findAllByOrderByFechaCreacionDesc();
    
    // Obtener por tipo
    List<Reporte> findByTipo(String tipo);
    
    // Obtener por estado
    List<Reporte> findByEstado(String estado);
    
    // Obtener por rango de fechas de creación
    List<Reporte> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
    
    // Obtener por tipo y estado
    List<Reporte> findByTipoAndEstado(String tipo, String estado);
}