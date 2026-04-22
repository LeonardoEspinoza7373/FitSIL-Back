package sistema_FitSIL.GestionUsuarios.model;

import jakarta.persistence.Entity;

@Entity
public class Administrador extends Persona {

    private String departamento;
    private int codigoAdmin;

    public Administrador() {
        setRol(Rol.ADMINISTRADOR);
    }

    public Administrador(String departamento, int codigoAdmin) {
        this.departamento = departamento;
        this.codigoAdmin = codigoAdmin;
        setRol(Rol.ADMINISTRADOR);
    }

    public String getDepartamento() {
        return this.departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public int getCodigoAdmin() {
        return this.codigoAdmin;
    }

    public void setCodigoAdmin(int codigoAdmin) {
        this.codigoAdmin = codigoAdmin;
    }

    
}
