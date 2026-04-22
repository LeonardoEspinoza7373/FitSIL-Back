package sistema_FitSIL.GestionUsuarios.controller;

import sistema_FitSIL.GestionUsuarios.model.Usuario;
import sistema_FitSIL.GestionUsuarios.service.UsuarioService;
import sistema_FitSIL.GestionUsuarios.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Registro de usuario
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(
            @Valid @RequestBody Usuario usuario,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String mensaje = bindingResult.getAllErrors()
                    .get(0)
                    .getDefaultMessage();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", mensaje));
        }

        // ✅ Encriptar AQUÍ en el controller
        usuario.setContrasenia(
                passwordEncoder.encode(usuario.getContrasenia())
        );

        Usuario nuevo = usuarioService.registrarUsuario(usuario);
        return ResponseEntity.status(201).body(nuevo);
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Usuario usuario) {
        Optional<Usuario> logeado = usuarioService.login(
                usuario.getCorreo(), 
                usuario.getContrasenia()
        );
        
        if (logeado.isPresent()) {
            Usuario u = logeado.get();
            String token = jwtService.generarToken(
                    u.getCorreo(), 
                    u.getRol().toString()
            );
            
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("usuario", u);
            respuesta.put("correo", u.getCorreo());
            respuesta.put("rol", u.getRol().name());
            respuesta.put("token", token);
            
            return ResponseEntity.ok(respuesta);
        } else {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Credenciales inválidas"));
        }
    }

    // Obtener perfil (solo el mismo usuario)
    @GetMapping("/perfil/{email}")
    public ResponseEntity<Usuario> perfil(
            @PathVariable String email,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String correoToken = jwtService.obtenerCorreoDesdeToken(token);

        if (!correoToken.equals(email)) {
            return ResponseEntity.status(403).build();
        }

        return usuarioService.obtenerPerfil(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Actualizar perfil
    @PutMapping("/perfil")
    public ResponseEntity<Usuario> actualizar(
            @RequestParam String email,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Usuario datos) {

        String token = authHeader.replace("Bearer ", "");
        String correoToken = jwtService.obtenerCorreoDesdeToken(token);

        if (!correoToken.equals(email)) {
            return ResponseEntity.status(403).build();
        }

        Usuario actualizado = usuarioService.actualizarPerfil(email, datos);
        return ResponseEntity.ok(actualizado);
    }

    // Eliminar perfil
    @DeleteMapping("/perfil")
    public ResponseEntity<String> eliminar(
            @RequestParam String email,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String correoToken = jwtService.obtenerCorreoDesdeToken(token);

        if (!correoToken.equals(email)) {
            return ResponseEntity.status(403).build();
        }

        try {
            usuarioService.eliminarUsuario(email);
            return ResponseEntity.ok("Usuario eliminado: " + email);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Agregar este método al UsuarioController.java

@PutMapping("/cambiar-contrasena")
public ResponseEntity<?> cambiarContrasena(
        @RequestParam String email,
        @RequestHeader("Authorization") String authHeader,
        @RequestBody Map<String, String> passwords) {
    
    try {
        String token = authHeader.replace("Bearer ", "");
        String correoToken = jwtService.obtenerCorreoDesdeToken(token);

        // Validar que el usuario solo pueda cambiar su propia contraseña
        if (!correoToken.equals(email)) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "No tienes permisos para cambiar esta contraseña"));
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

        // Buscar usuario
        Usuario usuario = usuarioService.obtenerPerfil(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasenia())) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "La contraseña actual es incorrecta"));
        }

        // Actualizar contraseña
        usuario.setContrasenia(passwordEncoder.encode(contrasenaNueva));
        usuarioService.actualizarPerfil(email, usuario);

        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada exitosamente"));
        
    } catch (Exception e) {
        return ResponseEntity.status(500)
                .body(Map.of("error", "Error al cambiar contraseña: " + e.getMessage()));
    }
}
}