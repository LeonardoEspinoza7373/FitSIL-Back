// ========== AdminNotificacionController.java (RUTAS CAMBIADAS) ==========
package sistema_FitSIL.GestionUsuarios.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sistema_FitSIL.GestionUsuarios.model.Notificacion;
import sistema_FitSIL.GestionUsuarios.service.NotificacionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/notificaciones")  // ✅ RUTA CAMBIADA
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminNotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    /**
     * ✅ Obtener todas las notificaciones (Admin)
     * GET /api/admin/notificaciones
     */
    @GetMapping
    public ResponseEntity<?> obtenerNotificaciones() {
        try {
            List<Notificacion> notificaciones = notificacionService.obtenerTodas();
            return ResponseEntity.ok(notificaciones);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al obtener notificaciones"));
        }
    }

    /**
     * ✅ Crear notificación manual (Admin)
     * POST /api/admin/notificaciones
     */
    @PostMapping
    public ResponseEntity<?> crearNotificacion(@RequestBody Notificacion notificacion) {
        try {
            Notificacion nueva = notificacionService.crearNotificacion(
                    notificacion.getTipo(),
                    notificacion.getMensaje(),
                    notificacion.getDatos() != null ? notificacion.getDatos() : "{}"
            );
            
            return ResponseEntity.ok(nueva);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al crear notificación"));
        }
    }

    /**
     * ✅ Marcar notificación como leída
     * PUT /api/admin/notificaciones/{id}/leer
     */
    @PutMapping("/{id}/leer")
    public ResponseEntity<?> marcarComoLeida(@PathVariable Long id) {
        try {
            Notificacion actualizada = notificacionService.marcarComoLeida(id);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al marcar como leída"));
        }
    }

    /**
     * ✅ Marcar todas como leídas
     * PUT /api/admin/notificaciones/leer-todas
     */
    @PutMapping("/leer-todas")
    public ResponseEntity<?> marcarTodasComoLeidas() {
        try {
            notificacionService.marcarTodasComoLeidas();
            return ResponseEntity.ok(Map.of("mensaje", "Todas marcadas como leídas"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al marcar todas como leídas"));
        }
    }

    /**
     * ✅ Eliminar notificación (Admin)
     * DELETE /api/admin/notificaciones/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarNotificacion(@PathVariable Long id) {
        try {
            notificacionService.eliminarNotificacion(id);
            return ResponseEntity.ok(Map.of("mensaje", "Notificación eliminada"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al eliminar notificación"));
        }
    }

    /**
     * ✅ Contar notificaciones no leídas
     * GET /api/admin/notificaciones/no-leidas/contar
     */
    @GetMapping("/no-leidas/contar")
    public ResponseEntity<?> contarNoLeidas() {
        try {
            long cantidad = notificacionService.contarNoLeidas();
            return ResponseEntity.ok(Map.of("cantidad", cantidad));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al contar notificaciones"));
        }
    }
}