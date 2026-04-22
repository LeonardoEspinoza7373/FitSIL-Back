package sistema_FitSIL.GestionEjercicios.controller;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import sistema_FitSIL.GestionEjercicios.model.Ejercicio;
import sistema_FitSIL.GestionEjercicios.service.EjercicioService;

@RestController
@RequestMapping("/ejercicios")
@CrossOrigin(origins = "*")
public class EjercicioController {

    private final EjercicioService ejercicioService;
    
    @Value("${app.imagenes.path:imagenes_ejercicios/}")
    private String carpetaImagenes;

    public EjercicioController(EjercicioService ejercicioService) {
        this.ejercicioService = ejercicioService;
    }
    
    private void crearCarpetaSiNoExiste() {
        File carpeta = new File(carpetaImagenes);
        if (!carpeta.exists()) {
            boolean creada = carpeta.mkdirs();
            if (creada) {
                System.out.println("✅ Carpeta creada: " + carpeta.getAbsolutePath());
            } else {
                System.err.println("❌ No se pudo crear la carpeta: " + carpeta.getAbsolutePath());
            }
        } else {
            System.out.println("📁 Usando carpeta: " + carpeta.getAbsolutePath());
        }
    }

    @PostMapping("/guardar")
    public ResponseEntity<?> guardarEjercicio(
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam String musculoTrabajado,
            @RequestParam(required = false) MultipartFile imagen,
            Authentication auth) {

        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(r -> r.equals("ROLE_ADMINISTRADOR"));

        if (!isAdmin) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Acceso denegado"));
        }

        // Asegurar que la carpeta existe
        crearCarpetaSiNoExiste();

        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setNombre(nombre);
        ejercicio.setDescripcion(descripcion);
        ejercicio.setMusculoTrabajado(musculoTrabajado);

        if (imagen != null && !imagen.isEmpty()) {
            try {
                String extension = obtenerExtension(imagen.getOriginalFilename());
                String nombreArchivo = System.currentTimeMillis() + "_" + 
                                     nombre.replaceAll("[^a-zA-Z0-9]", "_") + extension;
                
                File carpeta = new File(carpetaImagenes);
                File archivoDestino = new File(carpeta, nombreArchivo);
                
                // Guardar el archivo
                imagen.transferTo(archivoDestino);

                // Guardar solo el nombre del archivo en la BD
                ejercicio.setImagenUrl(nombreArchivo);
                
                System.out.println("✅ Imagen guardada en: " + archivoDestino.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500)
                        .body(Map.of("error", "Error al guardar imagen: " + e.getMessage()));
            }
        }

        Ejercicio guardado = ejercicioService.guardarEjercicio(ejercicio);
        return ResponseEntity.ok(guardado);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarEjercicio(
            @PathVariable Integer id,
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam String musculoTrabajado,
            @RequestParam(required = false) MultipartFile imagen,
            Authentication auth) {

        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(r -> r.equals("ROLE_ADMINISTRADOR"));

        if (!isAdmin) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Acceso denegado"));
        }

        Ejercicio ejercicio = ejercicioService.obtenerEjercicioPorId(id);
        if (ejercicio == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Ejercicio no encontrado"));
        }

        ejercicio.setNombre(nombre);
        ejercicio.setDescripcion(descripcion);
        ejercicio.setMusculoTrabajado(musculoTrabajado);

        if (imagen != null && !imagen.isEmpty()) {
            try {
                // Eliminar imagen anterior
                if (ejercicio.getImagenUrl() != null) {
                    eliminarImagenAnterior(ejercicio.getImagenUrl());
                }

                // Asegurar que la carpeta existe
                crearCarpetaSiNoExiste();

                // Guardar nueva imagen
                String extension = obtenerExtension(imagen.getOriginalFilename());
                String nombreArchivo = System.currentTimeMillis() + "_" + 
                                     nombre.replaceAll("[^a-zA-Z0-9]", "_") + extension;
                
                File carpeta = new File(carpetaImagenes);
                File archivoDestino = new File(carpeta, nombreArchivo);
                
                imagen.transferTo(archivoDestino);
                ejercicio.setImagenUrl(nombreArchivo);
                
                System.out.println("✅ Imagen actualizada: " + archivoDestino.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500)
                        .body(Map.of("error", "Error al actualizar imagen: " + e.getMessage()));
            }
        }

        Ejercicio actualizado = ejercicioService.guardarEjercicio(ejercicio);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarEjercicio(
            @PathVariable Integer id,
            Authentication auth) {

        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(r -> r.equals("ROLE_ADMINISTRADOR"));

        if (!isAdmin) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Acceso denegado"));
        }

        Ejercicio ejercicio = ejercicioService.obtenerEjercicioPorId(id);
        if (ejercicio == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Ejercicio no encontrado"));
        }

        if (ejercicio.getImagenUrl() != null) {
            eliminarImagenAnterior(ejercicio.getImagenUrl());
        }

        ejercicioService.eliminarEjercicio(id);
        
        return ResponseEntity.ok(Map.of(
            "mensaje", "Ejercicio eliminado exitosamente",
            "id", id
        ));
    }

    @GetMapping("/obtener")
    public List<Ejercicio> obtenerTodosLosEjercicios() {
        return ejercicioService.obtenerTodosLosEjercicios();
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorNombre(@RequestParam String nombre) {
        Ejercicio ejercicio = ejercicioService.buscarPorNombre(nombre);
        if (ejercicio == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Ejercicio no encontrado"));
        }
        return ResponseEntity.ok(ejercicio);
    }
    
    @GetMapping("/imagen/{nombreArchivo}")
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable String nombreArchivo) {
        try {
            File archivo = new File(carpetaImagenes, nombreArchivo);
            
            if (!archivo.exists()) {
                System.err.println("❌ Imagen no encontrada: " + archivo.getAbsolutePath());
                return ResponseEntity.notFound().build();
            }
            
            byte[] imagen = Files.readAllBytes(archivo.toPath());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(determinarContentType(nombreArchivo)));
            
            return new ResponseEntity<>(imagen, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return ".jpg";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf("."));
    }
    
    private String determinarContentType(String nombreArchivo) {
        String nombre = nombreArchivo.toLowerCase();
        if (nombre.endsWith(".png")) return "image/png";
        if (nombre.endsWith(".webp")) return "image/webp";
        if (nombre.endsWith(".gif")) return "image/gif";
        return "image/jpeg";
    }

    private void eliminarImagenAnterior(String nombreArchivo) {
        try {
            File archivo = new File(carpetaImagenes, nombreArchivo);
            if (archivo.exists() && archivo.delete()) {
                System.out.println("🗑️ Imagen eliminada: " + nombreArchivo);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error al eliminar: " + e.getMessage());
        }
    }
}