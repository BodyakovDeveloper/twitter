package koval.proxyseller.twitter.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import koval.proxyseller.twitter.exception.AuthenticationException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.function.Function

@Component
class JwtUtil {

    private Key secret;

    @Value('${jwt.expiration}')
    private long expiration;

    JwtUtil(@Value('${jwt.secret}') String secretString) {
        secret = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secret)
                .compact();
    }

    boolean isValidToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parseSignedClaims(token);

            return !claimsJws.getPayload().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthenticationException("Expired or invalid JWT token", e);
        }
    }

    String getUsername(String token) {
        try {
            return getClaimsFromToken(token, Claims::getSubject);
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthenticationException("Invalid or expired JWT token", e);
        }
    }

    private <T> T getClaimsFromToken(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claimsResolver.apply(claims);
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthenticationException("Invalid or expired JWT token", e);
        }
    }
}
