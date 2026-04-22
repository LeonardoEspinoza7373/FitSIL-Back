package sistema_FitSIL.EstadisticaReporte.service;

import org.springframework.stereotype.Service;
import sistema_FitSIL.EstadisticaReporte.dto.ResumenDTO;
import sistema_FitSIL.EstadisticaReporte.model.Estadistica;
import sistema_FitSIL.EstadisticaReporte.repository.EstadisticaRepository;
import sistema_FitSIL.GestionUsuarios.model.Usuario;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class EstadisticaService {

    private final EstadisticaRepository repo;

    public EstadisticaService(EstadisticaRepository repo) {
        this.repo = repo;
    }

    /**
     * ✅ Listar todas las estadísticas
     */
    public List<Estadistica> listar() {
        return repo.findAll();
    }

    /**
     * ✅ Buscar estadísticas por usuario
     */
    public List<Estadistica> buscarPorUsuario(Usuario usuario) {
        return repo.findByUsuario(usuario);
    }

    /**
     * ✅ Promedio de estrés del usuario
     */
    public double promedioEstres(Usuario usuario) {
        return buscarPorUsuario(usuario)
                .stream()
                .mapToDouble(Estadistica::getNivelEstres)
                .average()
                .orElse(0);
    }

    /**
     * ✅ MEJORADO: Generar estadística automática al completar rutina
     * Fórmula calorías: MET * peso (kg) * tiempo (horas)
     * MET para entrenamiento de fuerza: 5.0
     */
    public Estadistica generarEstadisticaAutomatica(Usuario usuario, int minutos) {
        // Validar datos del usuario
        Double peso = usuario.getPeso();
        if (peso == null || peso <= 0) {
            throw new RuntimeException("El usuario debe tener un peso registrado");
        }

        // Calcular calorías usando fórmula MET
        double horasEjercicio = minutos / 60.0;
        double MET = 5.0; // MET promedio para entrenamiento de fuerza
        double calorias = MET * usuario.getPeso() * horasEjercicio;
        
        // Nivel de estrés disminuye con el ejercicio
        double estres = Math.max(0, 100 - (minutos * 1.5));

        LocalDate hoy = LocalDate.now();
        String fechaHoy = hoy.toString();
        
        // Verificar si ya existe una estadística para hoy
        List<Estadistica> estadisticasHoy = repo.findByUsuario(usuario).stream()
                .filter(e -> e.getFecha().equals(fechaHoy))
                .collect(Collectors.toList());
        
        if (!estadisticasHoy.isEmpty()) {
            // Actualizar la estadística existente
            Estadistica existente = estadisticasHoy.get(0);
            existente.setMinutosEjercicio(existente.getMinutosEjercicio() + minutos);
            existente.setCaloriasQuemadas(existente.getCaloriasQuemadas() + calorias);
            
            // Recalcular nivel de estrés (promedio ponderado)
            double nuevoEstres = Math.max(0, existente.getNivelEstres() - (minutos * 0.5));
            existente.setNivelEstres(nuevoEstres);
            
            System.out.println("✅ Estadística actualizada: +" + minutos + " min, +" + Math.round(calorias) + " kcal");
            return repo.save(existente);
        } else {
            // Crear nueva estadística
            Estadistica nueva = new Estadistica(
                    usuario,
                    fechaHoy,
                    calorias,
                    minutos,
                    estres
            );
            
            System.out.println("✅ Nueva estadística creada: " + minutos + " min, " + Math.round(calorias) + " kcal");
            return repo.save(nueva);
        }
    }

    /**
     * ✅ Obtener resumen de estadísticas por rango
     */
    public ResumenDTO obtenerResumen(Usuario usuario, String rango) {
        List<Estadistica> lista = filtrarPorRango(
                buscarPorUsuario(usuario),
                rango
        );

        int totalEntrenamientos = lista.size();

        int promedioMinutos = totalEntrenamientos == 0
                ? 0
                : (int) lista.stream()
                    .mapToInt(Estadistica::getMinutosEjercicio)
                    .average()
                    .orElse(0);

        double totalCalorias = lista.stream()
                .mapToDouble(Estadistica::getCaloriasQuemadas)
                .sum();

        return new ResumenDTO(totalEntrenamientos, promedioMinutos, totalCalorias);
    }

    /**
     * ✅ Filtrar estadísticas por rango de tiempo
     */
    private List<Estadistica> filtrarPorRango(List<Estadistica> lista, String rango) {
        LocalDate hoy = LocalDate.now();
        LocalDate desde;

        switch (rango) {
            case "7D":
                desde = hoy.minusDays(7);
                break;
            case "1M":
                desde = hoy.minusMonths(1);
                break;
            case "6M":
                desde = hoy.minusMonths(6);
                break;
            case "ALL":
                return lista;
            default:
                desde = hoy.minusMonths(1);
        }

        return lista.stream()
                .filter(e -> {
                    try {
                        LocalDate fechaEstadistica = LocalDate.parse(e.getFecha());
                        return fechaEstadistica.isAfter(desde) || fechaEstadistica.isEqual(desde);
                    } catch (Exception ex) {
                        System.err.println("Error al parsear fecha: " + e.getFecha());
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * ✅ NUEVO: Obtener datos para gráfico semanal (últimos 7 días)
     */
    public List<Map<String, Object>> obtenerDatosSemana(Usuario usuario) {
        LocalDate hoy = LocalDate.now();
        String[] diasSemana = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
        
        List<Estadistica> estadisticas = buscarPorUsuario(usuario);
        
        return IntStream.range(0, 7)
                .mapToObj(i -> {
                    LocalDate fecha = hoy.minusDays(6 - i);
                    String fechaStr = fecha.toString();
                    
                    int minutos = estadisticas.stream()
                            .filter(e -> e.getFecha().equals(fechaStr))
                            .mapToInt(Estadistica::getMinutosEjercicio)
                            .sum();
                    
                    int diaSemanaIndex = (fecha.getDayOfWeek().getValue() % 7);
                    
                    Map<String, Object> mapa = new HashMap<>();
                    mapa.put("fecha", diasSemana[diaSemanaIndex]);
                    mapa.put("valor", minutos);
                    mapa.put("dia", fecha.format(DateTimeFormatter.ofPattern("dd/MM")));
                    return mapa;
                })
                .collect(Collectors.toList());
    }

    /**
     * ✅ NUEVO: Obtener datos por categoría de músculo
     */
    public List<Map<String, Object>> obtenerDatosPorCategoria(Usuario usuario, String rango) {
        // Este método necesitará información de las rutinas completadas
        // Por ahora devolvemos datos de ejemplo
        
        List<Estadistica> stats = filtrarPorRango(buscarPorUsuario(usuario), rango);
        int total = stats.stream().mapToInt(Estadistica::getMinutosEjercicio).sum();
        
        // Distribución aproximada
        return Arrays.asList(
                Map.of("nombre", "Fuerza", "valor", (int)(total * 0.4)),
                Map.of("nombre", "Cardio", "valor", (int)(total * 0.3)),
                Map.of("nombre", "Flexibilidad", "valor", (int)(total * 0.3))
        );
    }

    /**
     * ✅ NUEVO: Agregar estadística manualmente
     */
    public Estadistica agregarManual(Usuario usuario, int minutos, double calorias, double estres) {
        Estadistica nueva = new Estadistica(
                usuario,
                LocalDate.now().toString(),
                calorias,
                minutos,
                estres
        );
        
        return repo.save(nueva);
    }

    /**
     * ✅ Eliminar estadística
     */
    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    /**
     * ✅ NUEVO: Obtener total de minutos del mes
     */
    public int obtenerMinutosMes(Usuario usuario) {
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        
        return buscarPorUsuario(usuario).stream()
                .filter(e -> {
                    try {
                        LocalDate fecha = LocalDate.parse(e.getFecha());
                        return fecha.isAfter(inicioMes.minusDays(1));
                    } catch (Exception ex) {
                        return false;
                    }
                })
                .mapToInt(Estadistica::getMinutosEjercicio)
                .sum();
    }

    /**
     * ✅ NUEVO: Obtener racha actual (días consecutivos)
     */
    public int obtenerRachaActual(Usuario usuario) {
        List<Estadistica> stats = buscarPorUsuario(usuario).stream()
                .sorted((a, b) -> LocalDate.parse(b.getFecha()).compareTo(LocalDate.parse(a.getFecha())))
                .collect(Collectors.toList());
        
        if (stats.isEmpty()) return 0;
        
        LocalDate hoy = LocalDate.now();
        LocalDate ultimaFecha = LocalDate.parse(stats.get(0).getFecha());
        
        // Si la última estadística no es de hoy o ayer, la racha se rompió
        if (ultimaFecha.isBefore(hoy.minusDays(1))) {
            return 0;
        }
        
        int racha = 0;
        LocalDate fechaEsperada = hoy;
        
        for (Estadistica stat : stats) {
            LocalDate fecha = LocalDate.parse(stat.getFecha());
            
            if (fecha.isEqual(fechaEsperada) || fecha.isEqual(fechaEsperada.minusDays(1))) {
                racha++;
                fechaEsperada = fecha.minusDays(1);
            } else {
                break;
            }
        }
        
        return racha;
    }
}