package sistema_FitSIL.GestionUsuarios.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipo; // NUEVO_USUARIO, CAMBIO_ROL, ELIMINACION, SISTEMA

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Column(columnDefinition = "TEXT")
    private String datos; // JSON con datos adicionales

    @Column(nullable = false)
    private Boolean leida = false;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    // Constructores
    public Notificacion() {}

    public Notificacion(String tipo, String mensaje, String datos) {
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.datos = datos;
        this.leida = false;
        this.fecha = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getDatos() {
        return datos;
    }

    public void setDatos(String datos) {
        this.datos = datos;
    }

    public Boolean getLeida() {
        return leida;
    }

    public void setLeida(Boolean leida) {
        this.leida = leida;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}