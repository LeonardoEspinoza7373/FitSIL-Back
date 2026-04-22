package sistema_FitSIL.GestionUsuarios.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sistema_FitSIL.GestionUsuarios.service.MyUserDetailsService;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MyUserDetailsService userDetailsService;

    // ✅ Rutas públicas (no requieren token)
    private static final List<String> PUBLIC_PATHS = List.of(
        "/usuarios/registro",
        "/usuarios/login",
        "/administradores/registro",
        "/administradores/login",
        "/auth/login",
        "/ejercicios/obtener",
        "/ejercicios/buscar",
        "/ejercicios/imagen"  // ✅ AGREGAR ESTA LÍNEA
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        // ✅ Rutas públicas → no validar JWT
        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            String correo = jwtService.obtenerCorreoDesdeToken(token);

            if (correo != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(correo);

                if (jwtService.validarToken(token, correo)) {
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );

                    authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error en JwtAuthFilter: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}