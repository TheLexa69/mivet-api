package mivet.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.Date;

public class JwtUtil {

    private static final String SECRET_KEY = "MiVeT2302551550231246983-LuisGuillermoAndreGarrido!FPDAM";

    public static Long extractUserId(String token) {
        return getClaims(token).get("user_id", Integer.class).longValue();
    }

    public static String extractRol(String token) {
        return getClaims(token).get("rol", String.class);
    }

    public static boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (io.jsonwebtoken.security.SignatureException e) {
            System.err.println("Error: La firma del token no coincide. El token no es confiable.");
            return false;
        } catch (Exception e) {
            e.printStackTrace(); // <-- imprime por qué falla
            return false;
        }
    }

    private static Claims getClaims(String token) {
        String cleanToken = token.replace("Bearer ", "").trim();
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes())
                .build()
                .parseClaimsJws(cleanToken)
                .getBody();

    }
}
