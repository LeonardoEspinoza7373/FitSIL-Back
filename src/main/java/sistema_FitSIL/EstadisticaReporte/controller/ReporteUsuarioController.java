package sistema_FitSIL.EstadisticaReporte.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sistema_FitSIL.EstadisticaReporte.model.Estadistica;
import sistema_FitSIL.EstadisticaReporte.service.EstadisticaService;
import sistema_FitSIL.GestionUsuarios.model.Usuario;
import sistema_FitSIL.GestionUsuarios.service.UsuarioService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ✅ Controlador de reportes PERSONALES para usuarios regulares
 * Rutas: /api/reportes/**
 */
@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteUsuarioController {

    @Autowired
    private EstadisticaService estadisticaService;

    @Autowired
    private UsuarioService usuarioService;

    private static final DateTimeFormatter FECHA_FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA_FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * ✅ Generar reporte mensual
     * GET /api/reportes/mensual
     */
    @GetMapping("/mensual")
    public ResponseEntity<?> generarReporteMensual(Authentication auth) {
        try {
            String email = auth.getName();
            Usuario usuario = usuarioService.obtenerPerfil(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Obtener todas las estadísticas del usuario
            List<Estadistica> estadisticas = estadisticaService.buscarPorUsuario(usuario);

            Map<String, Object> reporte = new HashMap<>();
            reporte.put("titulo", "Reporte Mensual");
            reporte.put("periodo", "Últimos 30 días");
            reporte.put("fechaGeneracion", LocalDateTime.now().format(FECHA_HORA_FORMATO));

            // Resumen
            Map<String, Object> resumen = new HashMap<>();
            resumen.put("entrenamientos", estadisticas.size());
            resumen.put("duracionPromedio", calcularPromedioMinutos(estadisticas));
            resumen.put("calorias", calcularTotalCalorias(estadisticas));
            reporte.put("resumen", resumen);

            // Detalles
            List<Map<String, Object>> detalles = estadisticas.stream()
                    .map(e -> {
                        Map<String, Object> det = new HashMap<>();
                        det.put("fecha", e.getFecha()); // Ya es String en tu modelo
                        det.put("minutos", e.getMinutosEjercicio());
                        det.put("calorias", e.getCaloriasQuemadas());
                        det.put("estres", e.getNivelEstres());
                        return det;
                    })
                    .collect(Collectors.toList());
            reporte.put("detalles", detalles);

            return ResponseEntity.ok(reporte);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al generar reporte mensual: " + e.getMessage()));
        }
    }

    /**
     * ✅ Generar reporte semanal
     * GET /api/reportes/semanal
     */
    @GetMapping("/semanal")
    public ResponseEntity<?> generarReporteSemanal(Authentication auth) {
        try {
            String email = auth.getName();
            Usuario usuario = usuarioService.obtenerPerfil(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Obtener todas las estadísticas del usuario
            List<Estadistica> estadisticas = estadisticaService.buscarPorUsuario(usuario);

            Map<String, Object> reporte = new HashMap<>();
            reporte.put("titulo", "Reporte Semanal");
            reporte.put("periodo", "Últimos 7 días");
            reporte.put("fechaGeneracion", LocalDateTime.now().format(FECHA_HORA_FORMATO));

            // Resumen
            Map<String, Object> resumen = new HashMap<>();
            resumen.put("entrenamientos", estadisticas.size());
            resumen.put("duracionPromedio", calcularPromedioMinutos(estadisticas));
            resumen.put("calorias", calcularTotalCalorias(estadisticas));
            reporte.put("resumen", resumen);

            // Detalles
            List<Map<String, Object>> detalles = estadisticas.stream()
                    .map(e -> {
                        Map<String, Object> det = new HashMap<>();
                        det.put("fecha", e.getFecha());
                        det.put("minutos", e.getMinutosEjercicio());
                        det.put("calorias", e.getCaloriasQuemadas());
                        det.put("estres", e.getNivelEstres());
                        return det;
                    })
                    .collect(Collectors.toList());
            reporte.put("detalles", detalles);

            return ResponseEntity.ok(reporte);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al generar reporte semanal: " + e.getMessage()));
        }
    }

    /**
     * ✅ Generar reporte de calorías
     * GET /api/reportes/calorias?rango=1M
     */
    @GetMapping("/calorias")
    public ResponseEntity<?> generarReporteCalorias(
            @RequestParam(defaultValue = "1M") String rango,
            Authentication auth
    ) {
        try {
            String email = auth.getName();
            Usuario usuario = usuarioService.obtenerPerfil(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<Estadistica> estadisticas = estadisticaService.buscarPorUsuario(usuario);

            Map<String, Object> reporte = new HashMap<>();
            reporte.put("titulo", "Reporte de Calorías");
            reporte.put("periodo", obtenerPeriodoTexto(rango));
            reporte.put("fechaGeneracion", LocalDateTime.now().format(FECHA_HORA_FORMATO));

            // Resumen
            Map<String, Object> resumen = new HashMap<>();
            resumen.put("entrenamientos", estadisticas.size());
            resumen.put("duracionPromedio", calcularPromedioMinutos(estadisticas));
            resumen.put("calorias", calcularTotalCalorias(estadisticas));
            reporte.put("resumen", resumen);

            // Análisis de calorías
            Map<String, Object> analisis = new HashMap<>();
            analisis.put("Total quemadas", String.format("%.0f kcal", calcularTotalCalorias(estadisticas)));
            analisis.put("Promedio por sesión", String.format("%.0f kcal", calcularPromedioCalorias(estadisticas)));
            analisis.put("Máximo en un día", String.format("%.0f kcal", calcularMaximoCalorias(estadisticas)));
            reporte.put("analisis", analisis);

            // Detalles
            List<Map<String, Object>> detalles = estadisticas.stream()
                    .map(e -> {
                        Map<String, Object> det = new HashMap<>();
                        det.put("fecha", e.getFecha());
                        det.put("minutos", e.getMinutosEjercicio());
                        det.put("calorias", e.getCaloriasQuemadas());
                        det.put("estres", e.getNivelEstres());
                        return det;
                    })
                    .collect(Collectors.toList());
            reporte.put("detalles", detalles);

            return ResponseEntity.ok(reporte);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al generar reporte de calorías: " + e.getMessage()));
        }
    }

    /**
     * ✅ Generar historial completo
     * GET /api/reportes/historial
     */
    @GetMapping("/historial")
    public ResponseEntity<?> generarHistorialCompleto(Authentication auth) {
        try {
            String email = auth.getName();
            Usuario usuario = usuarioService.obtenerPerfil(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<Estadistica> estadisticas = estadisticaService.buscarPorUsuario(usuario);

            Map<String, Object> reporte = new HashMap<>();
            reporte.put("titulo", "Historial Completo");
            reporte.put("periodo", "Todas las actividades");
            reporte.put("fechaGeneracion", LocalDateTime.now().format(FECHA_HORA_FORMATO));

            // Resumen
            Map<String, Object> resumen = new HashMap<>();
            resumen.put("entrenamientos", estadisticas.size());
            resumen.put("duracionPromedio", calcularPromedioMinutos(estadisticas));
            resumen.put("calorias", calcularTotalCalorias(estadisticas));
            reporte.put("resumen", resumen);

            // Detalles - ordenados por ID descendente (más recientes primero)
            List<Map<String, Object>> detalles = estadisticas.stream()
                    .sorted(Comparator.comparing(Estadistica::getId).reversed())
                    .map(e -> {
                        Map<String, Object> det = new HashMap<>();
                        det.put("fecha", e.getFecha());
                        det.put("minutos", e.getMinutosEjercicio());
                        det.put("calorias", e.getCaloriasQuemadas());
                        det.put("estres", e.getNivelEstres());
                        return det;
                    })
                    .collect(Collectors.toList());
            reporte.put("detalles", detalles);

            return ResponseEntity.ok(reporte);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al generar historial: " + e.getMessage()));
        }
    }

    /**
     * ✅ Descargar reporte como JSON
     * GET /api/reportes/descargar/{tipo}?rango=1M
     */
    @GetMapping("/descargar/{tipo}")
    public ResponseEntity<?> descargarReporte(
            @PathVariable String tipo,
            @RequestParam(defaultValue = "1M") String rango,
            Authentication auth
    ) {
        try {
            Map<String, Object> reporte;

            switch (tipo.toLowerCase()) {
                case "mensual":
                    reporte = (Map<String, Object>) generarReporteMensual(auth).getBody();
                    break;
                case "semanal":
                    reporte = (Map<String, Object>) generarReporteSemanal(auth).getBody();
                    break;
                case "calorias":
                    reporte = (Map<String, Object>) generarReporteCalorias(rango, auth).getBody();
                    break;
                case "historial":
                    reporte = (Map<String, Object>) generarHistorialCompleto(auth).getBody();
                    break;
                default:
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Tipo de reporte no válido"));
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentDispositionFormData("attachment", 
                "reporte_" + tipo + "_" + System.currentTimeMillis() + ".json");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(reporte);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al descargar reporte: " + e.getMessage()));
        }
    }

    // ========================================
    // ✅ MÉTODOS AUXILIARES
    // ========================================

    private double calcularPromedioMinutos(List<Estadistica> estadisticas) {
        if (estadisticas.isEmpty()) return 0.0;
        return estadisticas.stream()
                .mapToInt(Estadistica::getMinutosEjercicio)
                .average()
                .orElse(0.0);
    }

    private double calcularTotalCalorias(List<Estadistica> estadisticas) {
        return estadisticas.stream()
                .mapToDouble(Estadistica::getCaloriasQuemadas)
                .sum();
    }

    private double calcularPromedioCalorias(List<Estadistica> estadisticas) {
        if (estadisticas.isEmpty()) return 0.0;
        return estadisticas.stream()
                .mapToDouble(Estadistica::getCaloriasQuemadas)
                .average()
                .orElse(0.0);
    }

    private double calcularMaximoCalorias(List<Estadistica> estadisticas) {
        return estadisticas.stream()
                .mapToDouble(Estadistica::getCaloriasQuemadas)
                .max()
                .orElse(0.0);
    }

    private String obtenerPeriodoTexto(String rango) {
        switch (rango) {
            case "1W": return "Última semana";
            case "1M": return "Último mes";
            case "3M": return "Últimos 3 meses";
            case "6M": return "Últimos 6 meses";
            case "1Y": return "Último año";
            default: return "Último mes";
        }
    }
}