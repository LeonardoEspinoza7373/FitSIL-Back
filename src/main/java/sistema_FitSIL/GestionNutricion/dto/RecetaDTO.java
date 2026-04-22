package sistema_FitSIL.GestionNutricion.dto;

public class RecetaDTO {
    
    private long id;
    private String nombre;
    private String descripcion;
    private String imagenUrl;
    private String ingredientes;
    private String instrucciones;

    // Constructor vacío
    public RecetaDTO() {}

    // Constructor completo
    public RecetaDTO(long id, String nombre, String descripcion, String imagenUrl, 
                     String ingredientes, String instrucciones) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.ingredientes = ingredientes;
        this.instrucciones = instrucciones;
    }

    // Getters y Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(String ingredientes) {
        this.ingredientes = ingredientes;
    }

    public String getInstrucciones() {
        return instrucciones;
    }

    public void setInstrucciones(String instrucciones) {
        this.instrucciones = instrucciones;
    }
}