package sistema_FitSIL.GestionUsuarios.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    // ✅ Clave secreta debe tener al menos 256 bits (32 caracteres)
    private static final String SECRET_KEY =
            "EstaEsUnaClaveMuySeguraQueTieneMasDe32Bytes!";

    // ✅ Tiempo de expiración: 1 hora
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    /**
     * ✅ Generar clave segura usando la nueva API
     */
    private SecretKey obtenerClave() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * ✅ Generar token JWT
     * @param correo Email del usuario
     * @param rol Rol del usuario (USUARIO, ADMINISTRADOR)
     * @return Token JWT
     */
    public String generarToken(String correo, String rol) {
        // ✅ CORRECCIÓN: Asegurar que el rol tenga el prefijo ROLE_
        String rolConPrefijo = rol.startsWith("ROLE_") ? rol : "ROLE_" + rol;
        
        System.out.println("📝 Generando token para: " + correo);
        System.out.println("🔐 Rol en token: " + rolConPrefijo);
        
        return Jwts.builder()
                .subject(correo)
                .claim("rol", rolConPrefijo)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(obtenerClave())
                .compact();
    }

    /**
     * ✅ Extraer correo del token
     * @param token Token JWT
     * @return Correo del usuario
     */
    public String obtenerCorreoDesdeToken(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    /**
     * ✅ Extraer rol del token
     * @param token Token JWT
     * @return Rol del usuario
     */
    public String obtenerRolDesdeToken(String token) {
        String rol = extraerClaim(token, claims -> claims.get("rol", String.class));
        System.out.println("🔍 Rol extraído del token: " + rol);
        return rol;
    }

    /**
     * ✅ Validar token
     * @param token Token JWT
     * @param correo Correo a validar
     * @return true si el token es válido
     */
    public boolean validarToken(String token, String correo) {
        try {
            return obtenerCorreoDesdeToken(token).equals(correo)
                    && !estaExpirado(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * ✅ Verificar si el token está expirado
     * @param token Token JWT
     * @return true si está expirado
     */
    private boolean estaExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration)
                .before(new Date());
    }

    /**
     * ✅ Extraer un claim específico del token
     * @param token Token JWT
     * @param claimsResolver Función para extraer el claim
     * @return Valor del claim
     */
    private <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extraerTodosClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * ✅ Extraer todos los claims del token usando la nueva API
     * @param token Token JWT
     * @return Claims del token
     */
    private Claims extraerTodosClaims(String token) {
        return Jwts.parser()
                .verifyWith(obtenerClave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * ✅ Extraer todos los claims de forma segura (sin lanzar excepción)
     * @param token Token JWT
     * @return Claims o null si hay error
     */
    public Claims extraerClaimsSafe(String token) {
        try {
            return extraerTodosClaims(token);
        } catch (Exception e) {
            System.err.println("❌ Error al extraer claims: " + e.getMessage());
            return null;
        }
    }

    /**
     * ✅ Verificar si el token es válido (sin verificar correo específico)
     * @param token Token JWT
     * @return true si el token es válido
     */
    public boolean esTokenValido(String token) {
        try {
            extraerTodosClaims(token);
            return !estaExpirado(token);
        } catch (Exception e) {
            return false;
        }
    }
}