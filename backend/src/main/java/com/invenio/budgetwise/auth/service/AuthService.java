package com.invenio.budgetwise.auth.service;

import com.invenio.budgetwise.auth.domain.User;
import com.invenio.budgetwise.auth.dto.AuthResponse;
import com.invenio.budgetwise.auth.dto.LoginRequest;
import com.invenio.budgetwise.auth.dto.RegisterRequest;
import com.invenio.budgetwise.auth.repository.UserRepository;
import com.invenio.budgetwise.shared.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Registro e inicio de sesion (issue #4). Alcance minimo del Sprint Planning:
 * sin refresh token, sin recuperacion de contrasena, sin roles.
 *
 * El login usa el mismo mensaje y el mismo 401 tanto si el email no existe
 * como si la contrasena es incorrecta: no hay que darle a quien ataca una
 * forma de saber que emails estan registrados.
 */
@Service
public class AuthService {

    private static final String CREDENCIALES_INVALIDAS = "Email o contrasena incorrectos";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese email ya esta registrado");
        }
        User user = new User(request.email(), passwordEncoder.encode(request.password()), request.name());
        userRepository.save(user);
        return issueToken(user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, CREDENCIALES_INVALIDAS));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, CREDENCIALES_INVALIDAS);
        }
        return issueToken(user.getEmail());
    }

    private AuthResponse issueToken(String email) {
        String token = jwtService.generate(email);
        return new AuthResponse(token, "Bearer", jwtService.expirationSeconds());
    }
}
