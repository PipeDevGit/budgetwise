package com.invenio.budgetwise.shared.security;

import com.invenio.budgetwise.auth.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Alcance minimo de la issue #4: sesion sin estado (STATELESS) via JWT,
 * /api/auth/register y /api/auth/login publicos, todo lo demas exige token.
 * Nada de roles todavia (fuera de alcance segun el profesor): eso seria un
 * @PreAuthorize a futuro, no una razon para armar la infraestructura ahora.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http, JwtService jwtService, UserRepository userRepository) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/health", "/error")
                        .permitAll()
                        // "/error" tiene que ser publica: si el JSON de un request viene mal
                        // formado, Spring reenvia internamente a /error para armar el 400. Si
                        // esa ruta pidiera autenticacion, ese reenvio se bloqueaba y el 400 real
                        // se disfrazaba de un 403 vacio y sin explicacion.
                        .anyRequest()
                        .authenticated())
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtService, userRepository),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
