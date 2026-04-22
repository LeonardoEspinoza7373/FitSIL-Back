package sistema_FitSIL.EstadisticaReporte.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sistema_FitSIL.GestionUsuarios.model.Notificacion;
import sistema_FitSIL.GestionUsuarios.model.Reporte;
import sistema_FitSIL.GestionUsuarios.service.NotificacionService;
import sistema_FitSIL.GestionUsuarios.service.ReporteService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ReportePublicoController {

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private NotificacionService notificacionService;

    /**
     * ✅ Obtener reportes públicos generados por admin
     * GET /api/reportes-admin/publicos
     */
    @GetMapping("/reportes-admin/publicos")
    public ResponseEntity<?> obtenerReportesPublicos(Authentication auth) {
        try {
            List<Reporte> reportes = reporteService.obtenerActivos();
            
            // Filtrar solo los reportes que son públicos (puedes agregar un campo isPublico)
            List<Map<String, Object>> reportesPublicos = reportes.stream()
                    .map(r -> {
                        Map<String, Object> map = new java.util.HashMap<>();
                        map.put("id", r.getId());
                        map.put("nombre", r.getNombre());
                        map.put("tipo", r.getTipo());
                        map.put("fechaCreacion", r.getFechaCreacion().toString());
                        map.put("esPublico", true);
                        return map;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(reportesPublicos);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al obtener reportes públicos"));
        }
    }

    /**
     * ✅ Descargar reporte del admin
     * GET /api/reportes-admin/{id}/descargar?formato=JSON
     */
    @GetMapping("/reportes-admin/{id}/descargar")
    public ResponseEntity<?> descargarReporteAdmin(
            @PathVariable Long id,
            @RequestParam(defaultValue = "JSON") String formato,
            Authentication auth
    ) {
        try {
            Reporte reporte = reporteService.obtenerPorId(id);
            
            if ("JSON".equalsIgnoreCase(formato)) {
                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=reporte_" + id + ".json")
                        .body(reporte);
            } else if ("CSV".equalsIgnoreCase(formato)) {
                // Convertir a CSV (simplificado)
                String csv = "ID,Nombre,Tipo,Fecha\n";
                csv += id + "," + reporte.getNombre() + "," + reporte.getTipo() + "," + reporte.getFechaCreacion();
                
                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=reporte_" + id + ".csv")
                        .body(csv);
            }
            
            return ResponseEntity.badRequest().body(Map.of("error", "Formato no válido"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al descargar reporte"));
        }
    }

    /**
     * ✅ Obtener notificaciones de reportes para usuario
     * GET /api/notificaciones-usuario
     */
    @GetMapping("/notificaciones-usuario")
    public ResponseEntity<?> obtenerNotificacionesUsuario(Authentication auth) {
        try {
            // Obtener solo notificaciones de tipo NUEVO_REPORTE
            List<Notificacion> notificaciones = notificacionService.obtenerTodas()
                    .stream()
                    .filter(n -> "NUEVO_REPORTE".equals(n.getTipo()) || "ACTUALIZACION".equals(n.getTipo()))
                    .limit(10)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(notificaciones);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al obtener notificaciones"));
        }
    }

    /**
     * ✅ Marcar notificación como leída
     * PUT /api/notificaciones-usuario/{id}/leer
     */
    @PutMapping("/notificaciones-usuario/{id}/leer")
    public ResponseEntity<?> marcarNotificacionLeida(
            @PathVariable Long id,
            Authentication auth
    ) {
        try {
            notificacionService.marcarComoLeida(id);
            return ResponseEntity.ok(Map.of("mensaje", "Notificación marcada como leída"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al marcar notificación"));
        }
    }
}