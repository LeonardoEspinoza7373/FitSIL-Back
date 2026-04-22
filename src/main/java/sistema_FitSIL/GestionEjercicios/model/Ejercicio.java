package sistema_FitSIL.GestionEjercicios.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private String descripcion;
    private String musculoTrabajado;

    // Nueva propiedad para guardar la ruta de la imagen
    private String imagenUrl;

    public Ejercicio() { }

    public Ejercicio(int id, String nombre, String descripcion, String musculoTrabajado, String imagenUrl) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.musculoTrabajado = musculoTrabajado;
        this.imagenUrl = imagenUrl;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getMusculoTrabajado() { return musculoTrabajado; }
    public void setMusculoTrabajado(String musculoTrabajado) { this.musculoTrabajado = musculoTrabajado; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
}
