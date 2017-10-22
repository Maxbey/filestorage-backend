package filestorage.services;

import filestorage.models.User;
import io.jsonwebtoken.*;


public class JWTService {
    private static final String JWTSecret = "Kboues0tx1m5k6tfW1eWl8rKhg4KhE4Z";

    public static String createJWT(User user){
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .signWith(SignatureAlgorithm.HS512, JWTSecret.getBytes())
                .compact();
    }

    public static Long authenticateJWT(String JWTToken){
        try {
            String subject = Jwts.parser()
                    .setSigningKey(JWTSecret.getBytes())
                    .parseClaimsJws(JWTToken)
                    .getBody()
                    .getSubject();

            return Long.parseLong(subject);
        }
        catch (JwtException e){
            return null;
        }
    }
}
