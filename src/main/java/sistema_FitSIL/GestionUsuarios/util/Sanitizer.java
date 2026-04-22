package sistema_FitSIL.GestionUsuarios.util;

public class Sanitizer {
    // Elimina etiquetas HTML/JS
    public static String sanitize(String input) {
        if (input == null) return null;
        return input.replaceAll("<[^>]*>", "").trim();
    }
}
