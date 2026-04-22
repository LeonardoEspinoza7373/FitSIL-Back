package sistema_FitSIL.GestionNutricion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sistema_FitSIL.GestionNutricion.dto.RecetaDTO;
import sistema_FitSIL.GestionNutricion.service.RecetaClientService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recetas")
@CrossOrigin(origins = "*")
public class RecetaController {

    @Autowired
    private RecetaClientService recetaClientService;

    /**
     *  Obtener todas las recetas
     * GET http://localhost:8081/recetas
     */
    @GetMapping
    public ResponseEntity<?> obtenerRecetas() {
        try {
            List<RecetaDTO> recetas = recetaClientService.obtenerTodasLasRecetas();
            return ResponseEntity.ok(recetas);
        } catch (RuntimeException e) {
            return ResponseEntity.status(503)
                    .body(Map.of(
                        "error", "Microservicio no disponible",
                        "mensaje", e.getMessage()
                    ));
        }
    }

    /**
     *  Obtener una receta específica por ID
     * GET http://localhost:8081/recetas/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerRecetaPorId(@PathVariable Long id) {
        try {
            RecetaDTO receta = recetaClientService.obtenerRecetaPorId(id);
            return ResponseEntity.ok(receta);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrada")) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Receta no encontrada", "id", id));
            }
            return ResponseEntity.status(503)
                    .body(Map.of("error", "Error al conectar con el microservicio"));
        }
    }

    /**
     *  Buscar recetas por nombre
     * GET http://localhost:8081/recetas/buscar/nombre?q=ensalada
     */
    @GetMapping("/buscar/nombre")
    public ResponseEntity<?> buscarPorNombre(@RequestParam String q) {
        try {
            List<RecetaDTO> recetas = recetaClientService.buscarRecetasPorNombre(q);
            return ResponseEntity.ok(Map.of(
                "total", recetas.size(),
                "recetas", recetas
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(503)
                    .body(Map.of("error", "Error al buscar recetas"));
        }
    }

    /**
     * Buscar recetas por ingrediente
     * GET http://localhost:8081/recetas/buscar/ingrediente?q=pollo
     */
    @GetMapping("/buscar/ingrediente")
    public ResponseEntity<?> buscarPorIngrediente(@RequestParam String q) {
        try {
            List<RecetaDTO> recetas = recetaClientService.buscarRecetasPorIngrediente(q);
            return ResponseEntity.ok(Map.of(
                "total", recetas.size(),
                "recetas", recetas
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(503)
                    .body(Map.of("error", "Error al buscar recetas"));
        }
    }

    /**
     *  Filtrar recetas excluyendo ingredientes (para restricciones alimenticias)
     * GET http://localhost:8081/recetas/filtrar?restricciones=lactosa,gluten
     */
    @GetMapping("/filtrar")
    public ResponseEntity<?> filtrarPorRestricciones(@RequestParam String restricciones) {
        try {
            List<RecetaDTO> recetas = recetaClientService.filtrarPorRestricciones(restricciones);
            return ResponseEntity.ok(Map.of(
                "restricciones", restricciones,
                "total", recetas.size(),
                "recetas", recetas
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(503)
                    .body(Map.of("error", "Error al filtrar recetas"));
        }
    }

    /**
     *  Obtener recetas aleatorias (para sugerencias)
     * GET http://localhost:8081/recetas/aleatorias?cantidad=3
     */
    @GetMapping("/aleatorias")
    public ResponseEntity<?> obtenerRecetasAleatorias(
            @RequestParam(defaultValue = "3") int cantidad) {
        try {
            List<RecetaDTO> recetas = recetaClientService.obtenerRecetasAleatorias(cantidad);
            return ResponseEntity.ok(recetas);
        } catch (RuntimeException e) {
            return ResponseEntity.status(503)
                    .body(Map.of("error", "Error al obtener recetas"));
        }
    }

    /**
     *  Obtener estadísticas del catálogo de recetas
     * GET http://localhost:8081/recetas/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> obtenerEstadisticas() {
        try {
            int total = recetaClientService.contarRecetas();
            return ResponseEntity.ok(Map.of(
                "total_recetas", total,
                "microservicio_activo", true,
                "url_microservicio", "http://localhost:8082"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(503)
                    .body(Map.of(
                        "total_recetas", 0,
                        "microservicio_activo", false,
                        "error", e.getMessage()
                    ));
        }
    }
}