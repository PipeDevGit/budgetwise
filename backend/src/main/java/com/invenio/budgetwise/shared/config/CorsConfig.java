package com.invenio.budgetwise.shared.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Permite que el frontend de Vite consuma la API en desarrollo.
 * El origen se configura por variable de entorno para no hardcodear puertos.
 *
 * Antes esto vivia en un WebMvcConfigurer (capa de Spring MVC), que corre
 * DESPUES de la cadena de seguridad. Un preflight OPTIONS a una ruta
 * protegida nunca llegaba a esa capa: Spring Security lo rechazaba primero
 * con 403, porque SecurityConfig no sabia nada de CORS (issue #41). Por eso
 * ahora este bean se conecta directo en SecurityConfig via http.cors(...).
 */
@Configuration
public class CorsConfig {

    private final String allowedOrigin;

    public CorsConfig(@Value("${budgetwise.cors.allowed-origin}") String allowedOrigin) {
        this.allowedOrigin = allowedOrigin;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigin));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
