package sistema_FitSIL.GestionUsuarios.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import sistema_FitSIL.GestionUsuarios.model.Administrador;
import sistema_FitSIL.GestionUsuarios.model.Usuario;
import sistema_FitSIL.GestionUsuarios.model.Notificacion;
import sistema_FitSIL.GestionUsuarios.model.Reporte;
import sistema_FitSIL.GestionUsuarios.service.AdministradorService;
import sistema_FitSIL.GestionUsuarios.service.NotificacionService;
import sistema_FitSIL.GestionUsuarios.service.ReporteService;
import sistema_FitSIL.GestionUsuarios.security.JwtService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/administradores")
@CrossOrigin(origins = "http://localhost:3000")
public class AdministradorController {

    @Autowired
    private AdministradorService adminService;

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    // ========== AUTENTICACIÓN ==========
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        String contrasenia = body.get("contrasenia");

        if (correo == null || contrasenia == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Correo y contraseña son obligatorios"));
        }

        try {
            Optional<Administrador> logeado = adminService.login(correo, contrasenia);

            if (logeado.isEmpty()) {
                return ResponseEntity.status(401)
                        .body(Map.of("error", "Credenciales inválidas"));
            }

            Administrador admin = logeado.get();
            String token = jwtService.generarToken(admin.getCorreo(), admin.getRol().name());

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("usuario", admin);
            respuesta.put("correo", admin.getCorreo());
            respuesta.put("rol", admin.getRol().name());
            respuesta.put("nombre", admin.getNombre());
            respuesta.put("token", token);

            return ResponseEntity.ok(respuesta);
            
        } catch (Exception e) {
            System.err.println("❌ Error en login admin: " + e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Administrador admin) {
        try {
            admin.setContrasenia(passwordEncoder.encode(admin.getContrasenia()));
            Administrador nuevoAdmin = adminService.registrarAdmin(admin);
            return ResponseEntity.status(201).body(nuevoAdmin);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ========== GESTIÓN DE PERFIL ==========
    
    // ✅ MODIFICADO: Ahora valida el token como el UsuarioController
    @PutMapping("/perfil")
    public ResponseEntity<?> actualizar(
            @RequestParam String email, 
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Administrador datos) {
        
        try {
            // Extraer token del header
            String token = authHeader.replace("Bearer ", "");
            String correoToken = jwtService.obtenerCorreoDesdeToken(token);
            String rolToken = jwtService.obtenerRolDesdeToken(token);
            
            System.out.println("==========================================");
            System.out.println("📧 Email solicitado: " + email);
            System.out.println("🔑 Email del token: " + correoToken);
            System.out.println("👤 Rol del token: " + rolToken);
            System.out.println("==========================================");
            
            // ✅ Validar que el email del token coincida con el email a actualizar
            if (!correoToken.equals(email)) {
                System.err.println("❌ El correo del token no coincide con el email a actualizar");
                return ResponseEntity.status(403)
                        .body(Map.of("error", "No tienes permisos para actualizar este perfil"));
            }
            
            // ✅ Validar que sea un administrador (acepta con o sin ROLE_)
            boolean esAdmin = "ROLE_ADMINISTRADOR".equals(rolToken) || "ADMINISTRADOR".equals(rolToken);
            
            if (!esAdmin) {
                System.err.println("❌ El usuario no tiene rol de administrador. Rol actual: " + rolToken);
                return ResponseEntity.status(403)
                        .body(Map.of("error", "No tienes permisos de administrador"));
            }
            
            System.out.println("✅ Validación exitosa. Actualizando perfil...");
            
            // Actualizar el perfil
            Administrador actualizado = adminService.actualizarAdmin(email, datos);
            
            System.out.println("✅ Perfil de administrador actualizado: " + email);
            
            return ResponseEntity.ok(actualizado);
            
        } catch (RuntimeException e) {
            System.err.println("❌ Error al actualizar admin: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(404)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ Error inesperado: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    // ✅ MODIFICADO: Ahora valida el token
    @DeleteMapping("/perfil")
    public ResponseEntity<?> eliminar(
            @RequestParam String email,
            @RequestHeader("Authorization") String authHeader) {
        
        try {
            String token = authHeader.replace("Bearer ", "");
            String correoToken = jwtService.obtenerCorreoDesdeToken(token);
            
            if (!correoToken.equals(email)) {
                return ResponseEntity.status(403)
                        .body(Map.of("error", "No tienes permisos para eliminar este perfil"));
            }
            
            adminService.eliminarAdmin(email);
            return ResponseEntity.ok(Map.of("mensaje", "Administrador eliminado", "email", email));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    // ========== GESTIÓN DE USUARIOS ==========
    
    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(adminService.listarUsuarios());
    }

    @PutMapping("/usuarios/rol")
    public ResponseEntity<?> cambiarRol(@RequestParam String email, @RequestBody Usuario datos) {
        try {
            Usuario actualizado = adminService.cambiarRol(email, datos.getRol().name());
            
            // Crear notificación
            notificacionService.crearNotificacion(
                "CAMBIO_ROL",
                "Rol actualizado para " + email + " a " + datos.getRol().name(),
                "{\"email\":\"" + email + "\",\"nuevoRol\":\"" + datos.getRol().name() + "\"}"
            );
            
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/usuarios/estadisticas")
    public ResponseEntity<String> estadisticas() {
        return ResponseEntity.ok(adminService.estadisticas());
    }

    @DeleteMapping("/usuarios")
    public ResponseEntity<?> eliminarUsuario(@RequestParam String email) {
        try {
            adminService.eliminarUsuario(email);
            
            // Crear notificación
            notificacionService.crearNotificacion(
                "ELIMINACION",
                "Usuario eliminado: " + email,
                "{\"email\":\"" + email + "\"}"
            );
            
            return ResponseEntity.ok(Map.of("mensaje", "Usuario eliminado", "email", email));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    // ========== NOTIFICACIONES ==========
    
    @GetMapping("/notificaciones")
    public ResponseEntity<List<Notificacion>> obtenerNotificaciones() {
        return ResponseEntity.ok(notificacionService.obtenerTodas());
    }

    @PostMapping("/notificaciones")
    public ResponseEntity<Notificacion> crearNotificacion(@RequestBody Notificacion notificacion) {
        Notificacion nueva = notificacionService.crearNotificacion(
            notificacion.getTipo(),
            notificacion.getMensaje(),
            notificacion.getDatos()
        );
        return ResponseEntity.status(201).body(nueva);
    }

    @PutMapping("/notificaciones/{id}/leer")
    public ResponseEntity<Notificacion> marcarComoLeida(@PathVariable Long id) {
        try {
            Notificacion actualizada = notificacionService.marcarComoLeida(id);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @PutMapping("/notificaciones/leer-todas")
    public ResponseEntity<?> marcarTodasComoLeidas() {
        notificacionService.marcarTodasComoLeidas();
        return ResponseEntity.ok(Map.of("mensaje", "Todas las notificaciones marcadas como leídas"));
    }

    @DeleteMapping("/notificaciones/{id}")
    public ResponseEntity<?> eliminarNotificacion(@PathVariable Long id) {
        try {
            notificacionService.eliminarNotificacion(id);
            return ResponseEntity.ok(Map.of("mensaje", "Notificación eliminada"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/notificaciones/no-leidas")
    public ResponseEntity<Long> contarNoLeidas() {
        long count = notificacionService.contarNoLeidas();
        return ResponseEntity.ok(count);
    }

    // ========== REPORTES ==========
    
    @GetMapping("/reportes")
    public ResponseEntity<List<Reporte>> obtenerReportes() {
        return ResponseEntity.ok(reporteService.obtenerTodos());
    }

    @PostMapping("/reportes")
    public ResponseEntity<Reporte> crearReporte(@RequestBody Reporte reporte) {
        Reporte nuevo = reporteService.crearReporte(reporte);
        return ResponseEntity.status(201).body(nuevo);
    }

    @GetMapping("/reportes/{id}")
    public ResponseEntity<Reporte> obtenerReporte(@PathVariable Long id) {
        try {
            Reporte reporte = reporteService.obtenerPorId(id);
            return ResponseEntity.ok(reporte);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @PutMapping("/reportes/{id}")
    public ResponseEntity<Reporte> actualizarReporte(
            @PathVariable Long id, 
            @RequestBody Reporte datos) {
        try {
            Reporte actualizado = reporteService.actualizarReporte(id, datos);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @DeleteMapping("/reportes/{id}")
    public ResponseEntity<?> eliminarReporte(@PathVariable Long id) {
        try {
            reporteService.eliminarReporte(id);
            return ResponseEntity.ok(Map.of("mensaje", "Reporte eliminado"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/reportes/tipo/{tipo}")
    public ResponseEntity<List<Reporte>> obtenerReportesPorTipo(@PathVariable String tipo) {
        List<Reporte> reportes = reporteService.obtenerPorTipo(tipo);
        return ResponseEntity.ok(reportes);
    }

    // Agregar este método al AdministradorController.java

@PutMapping("/cambiar-contrasena")
public ResponseEntity<?> cambiarContrasena(
        @RequestParam String email,
        @RequestHeader("Authorization") String authHeader,
        @RequestBody Map<String, String> passwords) {
    
    try {
        String token = authHeader.replace("Bearer ", "");
        String correoToken = jwtService.obtenerCorreoDesdeToken(token);
        String rolToken = jwtService.obtenerRolDesdeToken(token);

        // Validar que el admin solo pueda cambiar su propia contraseña
        if (!correoToken.equals(email)) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "No tienes permisos para cambiar esta contraseña"));
        }

        // Validar que sea administrador
        boolean esAdmin = "ROLE_ADMINISTRADOR".equals(rolToken) || "ADMINISTRADOR".equals(rolToken);
        if (!esAdmin) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "No tienes permisos de administrador"));
        }

        String contrasenaActual = passwords.get("contrasenaActual");
        String contrasenaNueva = passwords.get("contrasenaNueva");

        if (contrasenaActual == null || contrasenaNueva == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Debe proporcionar contraseña actual y nueva"));
        }

        if (contrasenaNueva.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La contraseña debe tener al menos 6 caracteres"));
        }

        // Buscar y verificar administrador usando el servicio de login
        Optional<Administrador> optAdmin = adminService.login(email, contrasenaActual);
        if (optAdmin.isEmpty()) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "La contraseña actual es incorrecta o el administrador no existe"));
        }
        Administrador admin = optAdmin.get();

        // Actualizar contraseña
        admin.setContrasenia(passwordEncoder.encode(contrasenaNueva));
        adminService.actualizarAdmin(email, admin);

        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada exitosamente"));
        
    } catch (Exception e) {
        return ResponseEntity.status(500)
                .body(Map.of("error", "Error al cambiar contraseña: " + e.getMessage()));
    }
}
}