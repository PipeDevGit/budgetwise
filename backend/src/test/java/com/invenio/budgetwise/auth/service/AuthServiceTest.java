package com.invenio.budgetwise.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.invenio.budgetwise.auth.domain.User;
import com.invenio.budgetwise.auth.dto.AuthResponse;
import com.invenio.budgetwise.auth.dto.LoginRequest;
import com.invenio.budgetwise.auth.dto.RegisterRequest;
import com.invenio.budgetwise.auth.repository.UserRepository;
import com.invenio.budgetwise.shared.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

class AuthServiceTest {

    private UserRepository userRepository;
    private JwtService jwtService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        jwtService = mock(JwtService.class);
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void registrarUnEmailNuevoDevuelveUnToken() {
        RegisterRequest request = new RegisterRequest("Ana", "ana@example.com", "clave1234");
        when(userRepository.existsByEmail("ana@example.com")).thenReturn(false);
        when(jwtService.generate("ana@example.com")).thenReturn("token-falso");
        when(jwtService.expirationSeconds()).thenReturn(86400L);

        AuthResponse response = authService.register(request);

        assertThat(response.token()).isEqualTo("token-falso");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresInSeconds()).isEqualTo(86400L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registrarUnEmailYaUsadoLanza409() {
        RegisterRequest request = new RegisterRequest("Ana", "ana@example.com", "clave1234");
        when(userRepository.existsByEmail("ana@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409")
                .hasMessageContaining("ya esta registrado");
    }

    @Test
    void loginConCredencialesCorrectasDevuelveUnToken() {
        String hash = passwordEncoder.encode("clave1234");
        User user = new User("ana@example.com", hash, "Ana");
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generate("ana@example.com")).thenReturn("token-falso");
        when(jwtService.expirationSeconds()).thenReturn(86400L);

        AuthResponse response = authService.login(new LoginRequest("ana@example.com", "clave1234"));

        assertThat(response.token()).isEqualTo("token-falso");
    }

    @Test
    void loginConContrasenaIncorrectaLanza401() {
        String hash = passwordEncoder.encode("clave1234");
        User user = new User("ana@example.com", hash, "Ana");
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(new LoginRequest("ana@example.com", "otra-clave")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("401")
                .hasMessageContaining("Email o contrasena incorrectos");
    }

    @Test
    void loginConEmailInexistenteLanza401ConElMismoMensaje() {
        when(userRepository.findByEmail("nadie@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("nadie@example.com", "clave1234")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("401")
                .hasMessageContaining("Email o contrasena incorrectos");
    }
}
