package com.invenio.budgetwise.auth.controller;

import com.invenio.budgetwise.auth.domain.User;
import com.invenio.budgetwise.auth.dto.AuthResponse;
import com.invenio.budgetwise.auth.dto.LoginRequest;
import com.invenio.budgetwise.auth.dto.RegisterRequest;
import com.invenio.budgetwise.auth.dto.UserResponse;
import com.invenio.budgetwise.auth.repository.UserRepository;
import com.invenio.budgetwise.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** El controller solo recibe la peticion y delega en AuthService. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /**
     * Ruta protegida de prueba: el criterio de aceptacion de la issue #4 pide
     * un endpoint que solo responda si el JwtAuthenticationFilter dejo la
     * peticion autenticada. Sirve tambien para que el frontend confirme la
     * sesion al recargar la pagina.
     */
    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return new UserResponse(user.getId(), user.getEmail(), user.getName());
    }
}
