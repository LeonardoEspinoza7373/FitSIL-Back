// ========== AdminReporteController.java (CORREGIDO) ==========
package sistema_FitSIL.GestionUsuarios.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sistema_FitSIL.GestionUsuarios.model.Notificacion;
import sistema_FitSIL.GestionUsuarios.model.Reporte;
import sistema_FitSIL.GestionUsuarios.service.NotificacionService;
import sistema_FitSIL.GestionUsuarios.service.ReporteService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes-admin")  // ✅ CAMBIADO: ahora coincide con frontend
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminReporteController {

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private NotificacionService notificacionService;

    /**
     * ✅ Obtener todos los reportes (Admin)
     * GET /api/reportes-admin
     */
    @GetMapping
    public ResponseEntity<?> obtenerReportes() {
        try {
            List<Reporte> reportes = reporteService.obtenerTodos();
            return ResponseEntity.ok(reportes);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al obtener reportes"));
        }
    }

    /**
     * ✅ Crear reporte (Admin)
     * POST /api/reportes-admin
     */
    @PostMapping
    public ResponseEntity<?> crearReporte(@RequestBody Reporte reporte) {
        try {
            Reporte nuevo = reporteService.crearReporte(reporte);
            
            // Crear notificación para todos los usuarios
            notificacionService.crearNotificacion(
                    "NUEVO_REPORTE",
                    "Nuevo reporte disponible: " + reporte.getNombre(),
                    "{\"reporteId\":" + nuevo.getId() + "}"
            );
            
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al crear reporte: " + e.getMessage()));
        }
    }

    /**
     * ✅ Actualizar reporte (Admin)
     * PUT /api/reportes-admin/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarReporte(
            @PathVariable Long id,
            @RequestBody Reporte datos
    ) {
        try {
            Reporte actualizado = reporteService.actualizarReporte(id, datos);
            
            // Notificar actualización
            notificacionService.crearNotificacion(
                    "ACTUALIZACION",
                    "Reporte actualizado: " + actualizado.getNombre(),
                    "{\"reporteId\":" + id + "}"
            );
            
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al actualizar reporte"));
        }
    }

    /**
     * ✅ Eliminar reporte (Admin)
     * DELETE /api/reportes-admin/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarReporte(@PathVariable Long id) {
        try {
            reporteService.eliminarReporte(id);
            return ResponseEntity.ok(Map.of("mensaje", "Reporte eliminado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al eliminar reporte"));
        }
    }

    /**
     * ✅ Generar reporte personalizado (Admin)
     * POST /api/reportes-admin/generar
     */
    @PostMapping("/generar")
    public ResponseEntity<?> generarReportePersonalizado(@RequestBody Map<String, Object> config) {
        try {
            // Crear el reporte basado en la configuración
            Reporte reporte = new Reporte();
            reporte.setNombre((String) config.get("nombre"));
            reporte.setTipo((String) config.get("tipo"));
            
            // Aquí puedes generar datos según el tipo de reporte
            String datosGenerados = generarDatosReporte(
                (String) config.get("tipo"),
                config
            );
            reporte.setDatos(datosGenerados);
            
            // Guardar filtros si existen
            if (config.containsKey("filtros")) {
                reporte.setFiltros(config.get("filtros").toString());
            }
            
            Reporte nuevo = reporteService.crearReporte(reporte);
            
            // Notificar
            notificacionService.crearNotificacion(
                    "NUEVO_REPORTE",
                    "Nuevo reporte personalizado: " + reporte.getNombre(),
                    "{\"reporteId\":" + nuevo.getId() + "}"
            );
            
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al generar reporte: " + e.getMessage()));
        }
    }

    // Método auxiliar para generar datos según tipo de reporte
    private String generarDatosReporte(String tipo, Map<String, Object> config) {
        // Aquí puedes implementar lógica específica según el tipo
        // Por ahora, retornamos un JSON básico
        return "{\"tipo\":\"" + tipo + "\",\"generado\":\"" + 
               java.time.LocalDateTime.now() + "\"}";
    }
}