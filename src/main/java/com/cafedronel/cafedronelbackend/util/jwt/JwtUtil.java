package com.cafedronel.cafedronelbackend.util.jwt;

import com.cafedronel.cafedronelbackend.data.model.Usuario;
import com.cafedronel.cafedronelbackend.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {


    private final String SECRET;
    private final long EXPIRATION;
    private final UsuarioRepository usuarioRepository;

    public JwtUtil(@Value("${jwt.secret}") String secret, UsuarioRepository usuarioRepository) {
        this.SECRET = secret;
        this.EXPIRATION = 1000 * 60 * 60 * 24;
        this.usuarioRepository = usuarioRepository;
    }

    private Key getSigninKey() {
        return Keys.hmacShaKeyFor(this.SECRET.getBytes());
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + this.EXPIRATION))
                .signWith(this.getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.getSigninKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String authToken, String email) {
        String tokenEmail = extractEmail(authToken);

        Usuario userFound = this.usuarioRepository.getUsuarioByCorreo(tokenEmail).orElseThrow();

        return email.equals(tokenEmail) && !isTokenExpired(authToken);
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigninKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }
}
