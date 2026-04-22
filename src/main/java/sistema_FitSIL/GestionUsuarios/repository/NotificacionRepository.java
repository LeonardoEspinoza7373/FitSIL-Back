// ========== NotificacionRepository.java ==========
package sistema_FitSIL.GestionUsuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sistema_FitSIL.GestionUsuarios.model.Notificacion;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    
    // Obtener todas ordenadas por fecha descendente
    List<Notificacion> findAllByOrderByFechaDesc();
    
    // Obtener por estado de lectura
    List<Notificacion> findByLeida(Boolean leida);
    
    // Obtener no leídas ordenadas por fecha
    List<Notificacion> findByLeidaOrderByFechaDesc(Boolean leida);
    
    // Contar no leídas
    long countByLeida(Boolean leida);
    
    // Obtener por tipo
    List<Notificacion> findByTipo(String tipo);
    
    // Eliminar notificaciones antiguas (más de 30 días)
    @Modifying
    @Transactional
    @Query("DELETE FROM Notificacion n WHERE n.fecha < :fechaLimite")
    void eliminarNotificacionesAntiguas(@Param("fechaLimite") LocalDateTime fechaLimite);
}

