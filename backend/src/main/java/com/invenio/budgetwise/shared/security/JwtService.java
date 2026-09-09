package com.invenio.budgetwise.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Genera y valida los JWT de sesion (issue #4). Alcance minimo acordado en el
 * Sprint Planning: un solo token de 24 horas, sin refresh token (ver D-08).
 * El "subject" del token es el email del usuario, que es como el resto del
 * sistema lo identifica.
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMillis;

    public JwtService(
            @Value("${budgetwise.jwt.secret}") String secret,
            @Value("${budgetwise.jwt.expiration-hours}") long expirationHours) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = Duration.ofHours(expirationHours).toMillis();
    }

    public String generate(String email) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMillis)))
                .signWith(key)
                .compact();
    }

    public long expirationSeconds() {
        return expirationMillis / 1000;
    }

    /**
     * Devuelve el email (subject) si el token es valido: firma correcta y no
     * vencido. Vacio en cualquier otro caso. No distingue el motivo porque el
     * filtro no necesita saberlo (ver JwtAuthenticationFilter).
     */
    public Optional<String> validateAndGetEmail(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.ofNullable(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
