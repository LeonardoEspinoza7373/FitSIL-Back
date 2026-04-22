package sistema_FitSIL.RutinaSemanal;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RutinaSemanalRepository extends JpaRepository<RutinaSemanal, Integer> {
    // Obtener todas las rutinas de un usuario por su ID
    List<RutinaSemanal> findByUsuarioId(Integer usuarioId);

    // Obtener rutinas de un usuario por correo
    List<RutinaSemanal> findByUsuarioCorreo(String correo);

    // Obtener rutinas de un usuario para un día específico
    List<RutinaSemanal> findByUsuarioIdAndDiaSemana(Integer usuarioId, DiaSemana diaSemana);

    // Obtener rutinas por correo y día
    List<RutinaSemanal> findByUsuarioCorreoAndDiaSemana(String correo, DiaSemana diaSemana);

    // Obtener rutinas completadas de un usuario
    List<RutinaSemanal> findByUsuarioIdAndCompletado(Integer usuarioId, boolean completado);

    // Eliminar todas las rutinas de un usuario
    void deleteByUsuarioId(Integer usuarioId);
}
