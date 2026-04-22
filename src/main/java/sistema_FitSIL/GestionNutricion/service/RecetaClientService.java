package sistema_FitSIL.GestionNutricion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sistema_FitSIL.GestionNutricion.dto.RecetaDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecetaClientService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${microservicio.recetas.url:http://localhost:8082}")
    private String microservicioUrl;

    /**
     * 📋 Obtener todas las recetas del microservicio
     */
    public List<RecetaDTO> obtenerTodasLasRecetas() {
        String url = microservicioUrl + "/api/recetas";
        
        try {
            ResponseEntity<List<RecetaDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<RecetaDTO>>() {}
            );
            
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
            
        } catch (Exception e) {
            System.err.println("⚠️ Error al obtener recetas del microservicio: " + e.getMessage());
            throw new RuntimeException("No se pudo conectar con el microservicio de recetas. Verifica que esté corriendo en " + microservicioUrl, e);
        }
    }

    /**
     * 🔍 Obtener una receta específica por ID
     */
    public RecetaDTO obtenerRecetaPorId(Long id) {
        List<RecetaDTO> recetas = obtenerTodasLasRecetas();
        return recetas.stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con ID: " + id));
    }

    /**
     * 🔎 Buscar recetas por nombre
     */
    public List<RecetaDTO> buscarRecetasPorNombre(String nombre) {
        List<RecetaDTO> recetas = obtenerTodasLasRecetas();
        return recetas.stream()
                .filter(r -> r.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * 🔎 Buscar recetas por ingrediente
     */
    public List<RecetaDTO> buscarRecetasPorIngrediente(String ingrediente) {
        List<RecetaDTO> recetas = obtenerTodasLasRecetas();
        return recetas.stream()
                .filter(r -> r.getIngredientes().toLowerCase().contains(ingrediente.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * 🔎 Filtrar recetas que NO contengan ciertos ingredientes (restricciones)
     * Útil para alergias o intolerancias
     */
    public List<RecetaDTO> filtrarPorRestricciones(String restricciones) {
        if (restricciones == null || restricciones.isEmpty()) {
            return obtenerTodasLasRecetas();
        }

        List<RecetaDTO> recetas = obtenerTodasLasRecetas();
        String[] restriccionesArray = restricciones.toLowerCase().split(",");
        
        return recetas.stream()
                .filter(receta -> {
                    String ingredientes = receta.getIngredientes().toLowerCase();
                    for (String restriccion : restriccionesArray) {
                        if (ingredientes.contains(restriccion.trim())) {
                            return false; // Excluir esta receta
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * 📊 Contar total de recetas disponibles
     */
    public int contarRecetas() {
        return obtenerTodasLasRecetas().size();
    }

    /**
     * 🎲 Obtener recetas aleatorias (útil para sugerencias)
     */
    public List<RecetaDTO> obtenerRecetasAleatorias(int cantidad) {
        List<RecetaDTO> todasLasRecetas = obtenerTodasLasRecetas();
        
        if (todasLasRecetas.size() <= cantidad) {
            return todasLasRecetas;
        }
        
        List<RecetaDTO> aleatorias = new ArrayList<>(todasLasRecetas);
        java.util.Collections.shuffle(aleatorias);
        
        return aleatorias.stream()
                .limit(cantidad)
                .collect(Collectors.toList());
    }
}