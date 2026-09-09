package com.invenio.budgetwise.category.service;

import com.invenio.budgetwise.category.domain.Category;
import com.invenio.budgetwise.category.repository.CategoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Crea las categorias predefinidas al arrancar la aplicacion.
 *
 * Es idempotente: solo inserta las que faltan, asi que arrancar la app varias
 * veces contra la misma base no las duplica.
 */
@Service
public class CategorySeeder {

    static final List<String> CATEGORIAS_PREDEFINIDAS =
            List.of("Comida", "Transporte", "Salud", "Ocio", "Otros");

    private final CategoryRepository repository;

    public CategorySeeder(CategoryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public int sembrar() {
        int creadas = 0;
        for (String nombre : CATEGORIAS_PREDEFINIDAS) {
            if (repository.findByNameAndOwnerIsNull(nombre).isEmpty()) {
                repository.save(Category.predefinida(nombre));
                creadas++;
            }
        }
        return creadas;
    }
}
