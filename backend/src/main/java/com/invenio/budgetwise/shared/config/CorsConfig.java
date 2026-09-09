package com.invenio.budgetwise.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Permite que el frontend de Vite consuma la API en desarrollo.
 * El origen se configura por variable de entorno para no hardcodear puertos.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String allowedOrigin;

    public CorsConfig(@Value("${budgetwise.cors.allowed-origin}") String allowedOrigin) {
        this.allowedOrigin = allowedOrigin;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigin)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
