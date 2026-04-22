package sistema_FitSIL.GestionUsuarios.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String tipo; // USUARIOS, ACTIVIDAD, EJERCICIOS

    @Column(columnDefinition = "TEXT")
    private String datos; // JSON con los datos del reporte

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(nullable = false)
    private String estado = "ACTIVO"; // ACTIVO, ARCHIVADO

    @Column(columnDefinition = "TEXT")
    private String filtros; // JSON con filtros aplicados

    // ========== CONSTRUCTORES ==========
    
    public Reporte() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = "ACTIVO";
    }

    public Reporte(String nombre, String tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = "ACTIVO";
    }

    public Reporte(String nombre, String tipo, String datos) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.datos = datos;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = "ACTIVO";
    }

    // ========== GETTERS Y SETTERS ==========
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDatos() {
        return datos;
    }

    public void setDatos(String datos) {
        this.datos = datos;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    // ========== MÉTODOS ÚTILES ==========
    
    @Override
    public String toString() {
        return "Reporte{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", estado='" + estado + '\'' +
                '}';
    }

    // Método para verificar si el reporte está activo
    public boolean isActivo() {
        return "ACTIVO".equals(this.estado);
    }

    // Método para verificar si el reporte está archivado
    public boolean isArchivado() {
        return "ARCHIVADO".equals(this.estado);
    }

    // Método para archivar el reporte
    public void archivar() {
        this.estado = "ARCHIVADO";
    }

    // Método para activar el reporte
    public void activar() {
        this.estado = "ACTIVO";
    }
}