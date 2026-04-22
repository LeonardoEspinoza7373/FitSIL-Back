package sistema_FitSIL.GestionUsuarios.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import sistema_FitSIL.GestionUsuarios.model.Administrador;
import sistema_FitSIL.GestionUsuarios.model.Usuario;
import sistema_FitSIL.GestionUsuarios.repository.AdministradorRepository;
import sistema_FitSIL.GestionUsuarios.repository.UsuarioRepository;
import sistema_FitSIL.GestionUsuarios.security.JwtService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    /**
     * ✅ LOGIN UNIFICADO - Busca en usuarios y administradores
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        String contrasenia = body.get("contrasenia");

        System.out.println("==========================================");
        System.out.println("🔐 Intento de login para: " + correo);

        if (correo == null || contrasenia == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Correo y contraseña son obligatorios"));
        }

        // 1️⃣ Buscar primero en USUARIOS
        Optional<Usuario> usuario = usuarioRepository.findByCorreo(correo);
        if (usuario.isPresent()) {
            Usuario u = usuario.get();
            
            if (passwordEncoder.matches(contrasenia, u.getContrasenia())) {
                String rolSinPrefijo = u.getRol().name(); // USUARIO, ADMINISTRADOR
                String token = jwtService.generarToken(u.getCorreo(), rolSinPrefijo);
                
                System.out.println("✅ Login exitoso como USUARIO");
                System.out.println("📧 Email: " + u.getCorreo());
                System.out.println("👤 Rol: " + rolSinPrefijo);
                System.out.println("🔑 Token generado con rol: ROLE_" + rolSinPrefijo);
                System.out.println("==========================================");

                Map<String, Object> respuesta = new HashMap<>();
                respuesta.put("usuario", u);
                respuesta.put("correo", u.getCorreo());
                respuesta.put("rol", rolSinPrefijo); // Sin ROLE_ para el frontend
                respuesta.put("nombre", u.getNombre());
                respuesta.put("token", token);

                return ResponseEntity.ok(respuesta);
            }
        }

        // 2️⃣ Si no es usuario, buscar en ADMINISTRADORES
        Optional<Administrador> administrador = administradorRepository.findByCorreo(correo);
        if (administrador.isPresent()) {
            Administrador a = administrador.get();
            
            if (passwordEncoder.matches(contrasenia, a.getContrasenia())) {
                String rolSinPrefijo = a.getRol().name(); // ADMINISTRADOR
                String token = jwtService.generarToken(a.getCorreo(), rolSinPrefijo);
                
                System.out.println("✅ Login exitoso como ADMINISTRADOR");
                System.out.println("📧 Email: " + a.getCorreo());
                System.out.println("👤 Rol: " + rolSinPrefijo);
                System.out.println("🔑 Token generado con rol: ROLE_" + rolSinPrefijo);
                System.out.println("==========================================");

                Map<String, Object> respuesta = new HashMap<>();
                respuesta.put("usuario", a);
                respuesta.put("correo", a.getCorreo());
                respuesta.put("rol", rolSinPrefijo); // Sin ROLE_ para el frontend
                respuesta.put("nombre", a.getNombre());
                respuesta.put("token", token);

                return ResponseEntity.ok(respuesta);
            }
        }

        System.err.println("❌ Credenciales inválidas para: " + correo);
        System.out.println("==========================================");
        
        return ResponseEntity.status(401)
                .body(Map.of("error", "Credenciales inválidas"));
    }
}