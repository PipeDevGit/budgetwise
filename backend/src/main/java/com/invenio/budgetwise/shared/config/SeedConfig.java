package com.invenio.budgetwise.shared.config;

import com.invenio.budgetwise.category.service.CategorySeeder;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Siembra las categorias predefinidas al arrancar. Se excluye del perfil "test"
 * para que las pruebas controlen su propio estado inicial.
 */
@Configuration
@Profile("!test")
public class SeedConfig {

    @Bean
    ApplicationRunner sembrarCategorias(CategorySeeder seeder) {
        return args -> seeder.sembrar();
    }
}
