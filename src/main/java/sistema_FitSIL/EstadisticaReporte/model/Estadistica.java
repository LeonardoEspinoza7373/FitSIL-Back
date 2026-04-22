package sistema_FitSIL.EstadisticaReporte.model;

import jakarta.persistence.*;

@Entity
@Table(name = "estadisticas")
public class Estadistica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private sistema_FitSIL.GestionUsuarios.model.Usuario usuario;

    private String fecha;
    private double caloriasQuemadas;
    private int minutosEjercicio;
    private double nivelEstres;

    public Estadistica() {}

    public Estadistica(sistema_FitSIL.GestionUsuarios.model.Usuario usuario,
                       String fecha, double caloriasQuemadas, int minutosEjercicio, double nivelEstres) {
        this.usuario = usuario;
        this.fecha = fecha;
        this.caloriasQuemadas = caloriasQuemadas;
        this.minutosEjercicio = minutosEjercicio;
        this.nivelEstres = nivelEstres;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public sistema_FitSIL.GestionUsuarios.model.Usuario getUsuario() { return usuario; }
    public void setUsuario(sistema_FitSIL.GestionUsuarios.model.Usuario usuario) { this.usuario = usuario; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public double getCaloriasQuemadas() { return caloriasQuemadas; }
    public void setCaloriasQuemadas(double caloriasQuemadas) { this.caloriasQuemadas = caloriasQuemadas; }

    public int getMinutosEjercicio() { return minutosEjercicio; }
    public void setMinutosEjercicio(int minutosEjercicio) { this.minutosEjercicio = minutosEjercicio; }

    public double getNivelEstres() { return nivelEstres; }
    public void setNivelEstres(double nivelEstres) { this.nivelEstres = nivelEstres; }
}
