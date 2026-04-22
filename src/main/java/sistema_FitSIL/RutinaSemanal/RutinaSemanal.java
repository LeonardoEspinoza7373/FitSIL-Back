package sistema_FitSIL.RutinaSemanal;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import sistema_FitSIL.GestionEjercicios.model.Ejercicio;
import sistema_FitSIL.GestionUsuarios.model.Usuario;

@Entity
@Table(name = "rutina_semanal")
public class RutinaSemanal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "ejercicio_id", nullable = false)
    private Ejercicio ejercicio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiaSemana diaSemana;

    private Integer series;
    private Integer repeticiones;
    private Double peso; // peso utilizado para el ejercicio
    private String notas;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "completado")
    private boolean completado = false;

    // Constructor vacío
    public RutinaSemanal() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Constructor completo
    public RutinaSemanal(Usuario usuario, Ejercicio ejercicio, DiaSemana diaSemana, 
                         Integer series, Integer repeticiones, Double peso) {
        this.usuario = usuario;
        this.ejercicio = ejercicio;
        this.diaSemana = diaSemana;
        this.series = series;
        this.repeticiones = repeticiones;
        this.peso = peso;
        this.fechaCreacion = LocalDateTime.now();
        this.completado = false;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Ejercicio getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(Ejercicio ejercicio) {
        this.ejercicio = ejercicio;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }

    public Integer getSeries() {
        return series;
    }

    public void setSeries(Integer series) {
        this.series = series;
    }

    public Integer getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(Integer repeticiones) {
        this.repeticiones = repeticiones;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }
}
