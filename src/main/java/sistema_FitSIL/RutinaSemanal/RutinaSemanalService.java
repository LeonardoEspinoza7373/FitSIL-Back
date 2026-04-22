package sistema_FitSIL.RutinaSemanal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sistema_FitSIL.GestionEjercicios.model.Ejercicio;
import sistema_FitSIL.GestionEjercicios.repository.EjercicioRepository;
import sistema_FitSIL.GestionUsuarios.model.Usuario;
import sistema_FitSIL.GestionUsuarios.repository.UsuarioRepository;

@Service
public class RutinaSemanalService {
    @Autowired
    private RutinaSemanalRepository rutinaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EjercicioRepository ejercicioRepository;

    // Agregar ejercicio a la rutina de un usuario
    @Transactional
    public RutinaSemanal agregarEjercicioARutina(String correoUsuario, 
                                                   Integer ejercicioId, 
                                                   DiaSemana dia,
                                                   Integer series,
                                                   Integer repeticiones,
                                                   Double peso) {
        
        // Buscar usuario
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Buscar ejercicio
        Ejercicio ejercicio = ejercicioRepository.findById(ejercicioId)
                .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado"));

        // Crear la rutina
        RutinaSemanal rutina = new RutinaSemanal(usuario, ejercicio, dia, series, repeticiones, peso);
        
        return rutinaRepository.save(rutina);
    }

    // Obtener todas las rutinas de un usuario
    public List<RutinaSemanal> obtenerRutinasPorUsuario(String correoUsuario) {
        return rutinaRepository.findByUsuarioCorreo(correoUsuario);
    }

    // Obtener rutinas de un usuario para un día específico
    public List<RutinaSemanal> obtenerRutinasPorDia(String correoUsuario, DiaSemana dia) {
        return rutinaRepository.findByUsuarioCorreoAndDiaSemana(correoUsuario, dia);
    }

    // Marcar rutina como completada
    @Transactional
    public RutinaSemanal marcarComoCompletada(Integer rutinaId) {
        RutinaSemanal rutina = rutinaRepository.findById(rutinaId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));
        
        rutina.setCompletado(true);
        return rutinaRepository.save(rutina);
    }

    // Actualizar una rutina
    @Transactional
    public RutinaSemanal actualizarRutina(Integer rutinaId, 
                                          DiaSemana nuevoDia,
                                          Integer series,
                                          Integer repeticiones,
                                          Double peso,
                                          String notas) {
        
        RutinaSemanal rutina = rutinaRepository.findById(rutinaId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        if (nuevoDia != null) rutina.setDiaSemana(nuevoDia);
        if (series != null) rutina.setSeries(series);
        if (repeticiones != null) rutina.setRepeticiones(repeticiones);
        if (peso != null) rutina.setPeso(peso);
        if (notas != null) rutina.setNotas(notas);

        return rutinaRepository.save(rutina);
    }

    // Eliminar una rutina
    @Transactional
    public void eliminarRutina(Integer rutinaId, String correoUsuario) {
        RutinaSemanal rutina = rutinaRepository.findById(rutinaId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        // Verificar que la rutina pertenece al usuario
        if (!rutina.getUsuario().getCorreo().equals(correoUsuario)) {
            throw new RuntimeException("No tienes permiso para eliminar esta rutina");
        }

        rutinaRepository.delete(rutina);
    }

    // Eliminar todas las rutinas de un usuario
    @Transactional
    public void eliminarTodasLasRutinas(String correoUsuario) {
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        rutinaRepository.deleteByUsuarioId(usuario.getId());
    }

    // Obtener rutinas completadas
    public List<RutinaSemanal> obtenerRutinasCompletadas(String correoUsuario) {
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return rutinaRepository.findByUsuarioIdAndCompletado(usuario.getId(), true);
    }

    // Obtener estadísticas de rutina
    public String obtenerEstadisticas(String correoUsuario) {
        List<RutinaSemanal> rutinas = rutinaRepository.findByUsuarioCorreo(correoUsuario);
        
        long totalRutinas = rutinas.size();
        long completadas = rutinas.stream().filter(RutinaSemanal::isCompletado).count();
        long pendientes = totalRutinas - completadas;
        
        double porcentajeCompletado = totalRutinas > 0 ? (completadas * 100.0 / totalRutinas) : 0;

        return String.format(
            "{\"totalRutinas\":%d,\"completadas\":%d,\"pendientes\":%d,\"porcentajeCompletado\":%.2f}",
            totalRutinas, completadas, pendientes, porcentajeCompletado
        );
    }
}
