package sistema_FitSIL.GestionUsuarios.model;

import jakarta.persistence.Entity;


@Entity
public class Usuario extends Persona {

    private double peso;
    private double altura;

    // Constructor vacío (importante para Jackson)
    public Usuario() {
        setRol(Rol.USUARIO);
    }

    // Constructor solo para peso y altura (opcional)
    public Usuario(double peso, double altura) {
        this.peso = peso;
        this.altura = altura;
        setRol(Rol.USUARIO);
    }

    // NUEVO constructor completo con nombre y correo
    public Usuario(String nombre, String correo, double peso, double altura) {
    super(); // llama al constructor vacío
    setNombre(nombre);
    setCorreo(correo);
    this.peso = peso;
    this.altura = altura;
    setRol(Rol.USUARIO);
    }

    // Getters y setters
    public double getPeso() { return this.peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public double getAltura() { return this.altura; }
    public void setAltura(double altura) { this.altura = altura; }
}