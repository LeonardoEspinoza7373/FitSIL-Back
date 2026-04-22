package sistema_FitSIL.GestionEjercicios.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sistema_FitSIL.GestionEjercicios.model.Ejercicio;

@Repository
public interface EjercicioRepository extends JpaRepository<Ejercicio, Integer> {
    // Definir metodos de consulta 
    Ejercicio findByNombre(String nombre);

}
