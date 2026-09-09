package com.invenio.budgetwise.shared.security;

import com.invenio.budgetwise.auth.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Lee el header Authorization, valida el JWT y marca la peticion como
 * autenticada. Si no hay token o es invalido, no marca nada: es Spring
 * Security (via SecurityConfig) quien decide si la ruta lo necesitaba. Este
 * filtro es el "filtro de ruta protegida" que pide el criterio de aceptacion
 * de la issue #4.
 *
 * Deliberadamente no usa UserDetailsService/AuthenticationManager (ver D-08):
 * el alcance minimo no tiene roles ni permisos por endpoint, asi que esa
 * infraestructura seria una abstraccion prematura.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length());
            Optional<String> email = jwtService.validateAndGetEmail(token);
            // Se vuelve a mirar la base, no solo el token: si el usuario se
            // borro, un JWT viejo no debe seguir autenticando.
            if (email.isPresent() && userRepository.existsByEmail(email.get())) {
                var authentication =
                        new UsernamePasswordAuthenticationToken(email.get(), null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
