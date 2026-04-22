package sistema_FitSIL.EstadisticaReporte.dto;

public class ResumenDTO {

    private int entrenamientos;
    private int duracionPromedio;
    private double calorias;

    public ResumenDTO() {}

    public ResumenDTO(int entrenamientos, int duracionPromedio, double calorias) {
        this.entrenamientos = entrenamientos;
        this.duracionPromedio = duracionPromedio;
        this.calorias = calorias;
    }

    // Getters y Setters
    public int getEntrenamientos() {
        return entrenamientos;
    }

    public void setEntrenamientos(int entrenamientos) {
        this.entrenamientos = entrenamientos;
    }

    public int getDuracionPromedio() {
        return duracionPromedio;
    }

    public void setDuracionPromedio(int duracionPromedio) {
        this.duracionPromedio = duracionPromedio;
    }

    public double getCalorias() {
        return calorias;
    }

    public void setCalorias(double calorias) {
        this.calorias = calorias;
    }
}