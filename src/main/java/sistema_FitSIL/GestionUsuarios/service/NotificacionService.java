package sistema_FitSIL.GestionUsuarios.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sistema_FitSIL.GestionUsuarios.model.Notificacion;
import sistema_FitSIL.GestionUsuarios.repository.NotificacionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    // Obtener todas las notificaciones
    public List<Notificacion> obtenerTodas() {
        return notificacionRepository.findAllByOrderByFechaDesc();
    }

    // Crear notificación
    public Notificacion crearNotificacion(String tipo, String mensaje, String datos) {
        Notificacion notificacion = new Notificacion(tipo, mensaje, datos);
        return notificacionRepository.save(notificacion);
    }

    // Marcar como leída
    public Notificacion marcarComoLeida(Long id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        
        notificacion.setLeida(true);
        return notificacionRepository.save(notificacion);
    }

    // Marcar todas como leídas
    public void marcarTodasComoLeidas() {
        List<Notificacion> noLeidas = notificacionRepository.findByLeida(false);
        noLeidas.forEach(n -> n.setLeida(true));
        notificacionRepository.saveAll(noLeidas);
    }

    // Eliminar notificación
    public void eliminarNotificacion(Long id) {
        if (!notificacionRepository.existsById(id)) {
            throw new RuntimeException("Notificación no encontrada");
        }
        notificacionRepository.deleteById(id);
    }

    // Contar no leídas
    public long contarNoLeidas() {
        return notificacionRepository.countByLeida(false);
    }

    // Obtener solo no leídas
    public List<Notificacion> obtenerNoLeidas() {
        return notificacionRepository.findByLeidaOrderByFechaDesc(false);
    }

    // Eliminar notificaciones antiguas (más de 30 días)
    public void limpiarNotificacionesAntiguas() {
        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(30);
        notificacionRepository.eliminarNotificacionesAntiguas(fechaLimite);
    }
}