package sistema_FitSIL.GestionUsuarios.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import sistema_FitSIL.GestionUsuarios.security.JwtAuthFilter;
import sistema_FitSIL.GestionUsuarios.service.DbUserDetailsService;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private DbUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                // ========================================
                // ✅ RUTAS PÚBLICAS (sin autenticación)
                // ========================================
                .requestMatchers(
                    "/auth/login",
                    "/usuarios/registro",
                    "/usuarios/login",
                    "/administradores/registro",
                    "/administradores/login",
                    "/ejercicios/obtener",
                    "/ejercicios/buscar",
                    "/ejercicios/imagen/**"
                ).permitAll()

                // ========================================
                // ✅ RUTAS DE EJERCICIOS (solo admin puede modificar)
                // ========================================
                .requestMatchers(
                    "/ejercicios/guardar",
                    "/ejercicios/actualizar/**",
                    "/ejercicios/eliminar/**"
                ).hasAuthority("ROLE_ADMINISTRADOR")

                // ========================================
                // ✅ RUTAS DE REPORTES - ADMIN (solo administradores)
                // ========================================
                .requestMatchers(
                    "/api/reportes-admin",
                    "/api/reportes-admin/**"
                ).hasAuthority("ROLE_ADMINISTRADOR")

                // ========================================
                // ✅ RUTAS DE NOTIFICACIONES - ADMIN (solo administradores)
                // ========================================
                .requestMatchers(
                    "/api/admin/notificaciones",          // ✅ CAMBIADO
                    "/api/admin/notificaciones/**"        // ✅ CAMBIADO
                ).hasAuthority("ROLE_ADMINISTRADOR")

                // ========================================
                // ✅ RUTAS DE NOTIFICACIONES - USUARIOS (usuarios autenticados)
                // ========================================
                .requestMatchers(
                    "/api/notificaciones-usuario",
                    "/api/notificaciones-usuario/**"
                ).authenticated()

                // ========================================
                // ✅ RUTAS DE REPORTES PERSONALES (usuarios autenticados)
                // ========================================
                .requestMatchers(
                    "/api/reportes/mensual",
                    "/api/reportes/semanal",
                    "/api/reportes/calorias",
                    "/api/reportes/historial",
                    "/api/reportes/descargar/**"
                ).authenticated()

                // ========================================
                // ✅ RUTAS DE USUARIOS (propietario o admin)
                // ========================================
                .requestMatchers(
                    "/usuarios/**"
                ).authenticated()

                // ========================================
                // ✅ RUTAS DE ESTADÍSTICAS (usuarios autenticados)
                // ========================================
                .requestMatchers(
                    "/estadisticas/**",
                    "/api/estadisticas/**"
                ).authenticated()

                // ========================================
                // ✅ RUTAS DE RUTINAS (usuarios autenticados)
                // ========================================
                .requestMatchers(
                    "/rutinas/**",
                    "/api/rutinas/**"
                ).authenticated()

                // ========================================
                // ✅ RUTAS DE ADMINISTRADOR (solo administradores)
                // Las rutas /administradores/** que YA EXISTEN
                // ========================================
                .requestMatchers(
                    "/administradores/**"
                ).hasAuthority("ROLE_ADMINISTRADOR")

                // ========================================
                // ✅ CUALQUIER OTRA RUTA requiere autenticación
                // ========================================
                .anyRequest().authenticated()
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
            "http://localhost:3000",
            "http://localhost:5173"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}