package sistema_FitSIL.GestionUsuarios.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class RoleFilter extends OncePerRequestFilter {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/usuarios/registro",
            "/usuarios/login",
            "/auth/login",
            "/administradores/login",
            "/administradores/registro",
            "/ejercicios/obtener",
            "/ejercicios/buscar",
            "/ejercicios/imagen"  // ✅ AGREGAR ESTA LÍNEA
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Permitir acceso libre a rutas públicas
        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Acceso denegado: falta token JWT\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}