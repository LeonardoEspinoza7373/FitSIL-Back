package sistema_FitSIL.GestionUsuarios.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sistema_FitSIL.GestionUsuarios.model.Reporte;
import sistema_FitSIL.GestionUsuarios.repository.ReporteRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepository;

    // Obtener todos los reportes
    public List<Reporte> obtenerTodos() {
        return reporteRepository.findAllByOrderByFechaCreacionDesc();
    }

    // Crear reporte
    public Reporte crearReporte(Reporte reporte) {
        if (reporte.getFechaCreacion() == null) {
            reporte.setFechaCreacion(LocalDateTime.now());
        }
        if (reporte.getEstado() == null) {
            reporte.setEstado("ACTIVO");
        }
        return reporteRepository.save(reporte);
    }

    // Obtener por ID
    public Reporte obtenerPorId(Long id) {
        return reporteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
    }

    // Actualizar reporte
    public Reporte actualizarReporte(Long id, Reporte datos) {
        Reporte reporte = obtenerPorId(id);
        
        if (datos.getNombre() != null) {
            reporte.setNombre(datos.getNombre());
        }
        if (datos.getTipo() != null) {
            reporte.setTipo(datos.getTipo());
        }
        if (datos.getDatos() != null) {
            reporte.setDatos(datos.getDatos());
        }
        if (datos.getFechaInicio() != null) {
            reporte.setFechaInicio(datos.getFechaInicio());
        }
        if (datos.getFechaFin() != null) {
            reporte.setFechaFin(datos.getFechaFin());
        }
        if (datos.getEstado() != null) {
            reporte.setEstado(datos.getEstado());
        }
        if (datos.getFiltros() != null) {
            reporte.setFiltros(datos.getFiltros());
        }
        
        return reporteRepository.save(reporte);
    }

    // Eliminar reporte
    public void eliminarReporte(Long id) {
        if (!reporteRepository.existsById(id)) {
            throw new RuntimeException("Reporte no encontrado");
        }
        reporteRepository.deleteById(id);
    }

    // Obtener por tipo
    public List<Reporte> obtenerPorTipo(String tipo) {
        return reporteRepository.findByTipo(tipo);
    }

    // Obtener reportes activos
    public List<Reporte> obtenerActivos() {
        return reporteRepository.findByEstado("ACTIVO");
    }

    // Archivar reporte
    public Reporte archivarReporte(Long id) {
        Reporte reporte = obtenerPorId(id);
        reporte.setEstado("ARCHIVADO");
        return reporteRepository.save(reporte);
    }

    // Obtener reportes por rango de fechas
    public List<Reporte> obtenerPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return reporteRepository.findByFechaCreacionBetween(inicio, fin);
    }
}