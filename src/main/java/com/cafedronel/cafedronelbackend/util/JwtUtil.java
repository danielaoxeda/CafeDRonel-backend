package com.cafedronel.cafedronelbackend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final String SECRET_KEY = "cafedronel2025claveultrasecretaparajwtseguro"; // mínimo 32 caracteres
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 horas

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String correo) {
        return Jwts.builder()
                .setSubject(correo)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractCorreo(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String correo) {
        String correoExtraido = extractCorreo(token);
        return (correoExtraido.equals(correo) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
