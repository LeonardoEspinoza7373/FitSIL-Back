package sistema_FitSIL.EstadisticaReporte.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sistema_FitSIL.EstadisticaReporte.dto.ResumenDTO;
import sistema_FitSIL.EstadisticaReporte.model.Estadistica;
import sistema_FitSIL.EstadisticaReporte.service.EstadisticaService;
import sistema_FitSIL.GestionUsuarios.model.Usuario;
import sistema_FitSIL.GestionUsuarios.service.UsuarioService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estadisticas")
@CrossOrigin(origins = "*")
public class EstadisticaController {

    private final EstadisticaService service;
    private final UsuarioService usuarioService;

    public EstadisticaController(
            EstadisticaService service,
            UsuarioService usuarioService
    ) {
        this.service = service;
        this.usuarioService = usuarioService;
    }

    /**
     * ✅ Obtener todas las estadísticas del usuario
     * GET /api/estadisticas/usuario
     */
    @GetMapping("/usuario")
    public List<Estadistica> estadisticasUsuario(Authentication auth) {
        String email = auth.getName();
        Usuario usuario = usuarioService.obtenerPerfil(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return service.buscarPorUsuario(usuario);
    }

    /**
     * ✅ Obtener resumen por rango de tiempo
     * GET /api/estadisticas/usuario/resumen?rango=1M
     */
    @GetMapping("/usuario/resumen")
    public ResumenDTO resumen(
            @RequestParam(defaultValue = "1M") String rango,
            Authentication auth
    ) {
        String email = auth.getName();
        Usuario usuario = usuarioService.obtenerPerfil(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return service.obtenerResumen(usuario, rango);
    }

    /**
     * ✅ Obtener promedio de estrés
     * GET /api/estadisticas/usuario/promedio-estres
     */
    @GetMapping("/usuario/promedio-estres")
    public Map<String, Double> promedioEstres(Authentication auth) {
        String email = auth.getName();
        Usuario usuario = usuarioService.obtenerPerfil(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        double promedio = service.promedioEstres(usuario);
        return Map.of("promedioEstres", promedio);
    }

    /**
     * ✅ NUEVO: Obtener datos para gráfico semanal
     * GET /api/estadisticas/usuario/semana
     */
    @GetMapping("/usuario/semana")
    public List<Map<String, Object>> datosSemana(Authentication auth) {
        String email = auth.getName();
        Usuario usuario = usuarioService.obtenerPerfil(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return service.obtenerDatosSemana(usuario);
    }

    /**
     * ✅ NUEVO: Obtener datos por categoría
     * GET /api/estadisticas/usuario/categoria?rango=1M
     */
    @GetMapping("/usuario/categoria")
    public List<Map<String, Object>> datosPorCategoria(
            @RequestParam(defaultValue = "1M") String rango,
            Authentication auth
    ) {
        String email = auth.getName();
        Usuario usuario = usuarioService.obtenerPerfil(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return service.obtenerDatosPorCategoria(usuario, rango);
    }

    /**
     * ✅ NUEVO: Obtener dashboard completo
     * GET /api/estadisticas/usuario/dashboard?rango=1M
     */
    @GetMapping("/usuario/dashboard")
    public Map<String, Object> dashboard(
            @RequestParam(defaultValue = "1M") String rango,
            Authentication auth
    ) {
        String email = auth.getName();
        Usuario usuario = usuarioService.obtenerPerfil(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        ResumenDTO resumen = service.obtenerResumen(usuario, rango);
        List<Map<String, Object>> datosSemana = service.obtenerDatosSemana(usuario);
        List<Map<String, Object>> datosCategoria = service.obtenerDatosPorCategoria(usuario, rango);
        int minutosMes = service.obtenerMinutosMes(usuario);
        int racha = service.obtenerRachaActual(usuario);
        
        return Map.of(
                "resumen", resumen,
                "datosSemana", datosSemana,
                "datosCategoria", datosCategoria,
                "minutosMes", minutosMes,
                "rachaActual", racha
        );
    }

    /**
     * ✅ Generar estadística manual (para testing)
     * POST /api/estadisticas/generar?minutos=30
     */
    @PostMapping("/generar")
    public Estadistica generar(
            @RequestParam int minutos,
            Authentication auth
    ) {
        String email = auth.getName();
        Usuario usuario = usuarioService.obtenerPerfil(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return service.generarEstadisticaAutomatica(usuario, minutos);
    }

    /**
     * ✅ NUEVO: Agregar estadística manual completa
     * POST /api/estadisticas/agregar
     */
    @PostMapping("/agregar")
    public Estadistica agregarManual(
            @RequestBody Map<String, Object> datos,
            Authentication auth
    ) {
        String email = auth.getName();
        Usuario usuario = usuarioService.obtenerPerfil(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        int minutos = (Integer) datos.get("minutos");
        double calorias = datos.get("calorias") != null 
                ? ((Number) datos.get("calorias")).doubleValue() 
                : 0.0;
        double estres = datos.get("estres") != null 
                ? ((Number) datos.get("estres")).doubleValue() 
                : 50.0;
        
        return service.agregarManual(usuario, minutos, calorias, estres);
    }

    /**
     * ✅ NUEVO: Eliminar estadística
     * DELETE /api/estadisticas/{id}
     */
    @DeleteMapping("/{id}")
    public Map<String, String> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return Map.of("mensaje", "Estadística eliminada correctamente");
    }
}