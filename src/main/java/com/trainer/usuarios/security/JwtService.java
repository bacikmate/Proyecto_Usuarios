package com.trainer.usuarios.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    // Clave secreta parametrizada
    @Value("${jwt.secret}")
    private String secretKey;

    private final long JWT_EXPIRATION = 86400000L;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generarToken(UserDetails usuario) {
        List<String> roles = usuario.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return Jwts.builder()
                .subject(usuario.getUsername()) // sub: nombre de usuario
                .claim("roles", roles) // roles del usuario en formato String plano
                .issuedAt(new Date()) // iat: fecha de emisión
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSigningKey()) // firma con clave secreta
                .compact();
    }

    // Extraer el username del token usando la API estandarizada
    public String extraerUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Nivel 4 CORREGIDO: Mapeo de la lista publica de roles sin usar paquetes '.impl'
    @SuppressWarnings("unchecked")
    public List<String> extraerRoles(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("roles", List.class);
    }
}