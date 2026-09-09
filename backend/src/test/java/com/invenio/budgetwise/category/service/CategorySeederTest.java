package com.invenio.budgetwise.category.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.invenio.budgetwise.category.domain.Category;
import com.invenio.budgetwise.category.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CategorySeederTest {

    @Autowired
    private CategorySeeder seeder;

    @Autowired
    private CategoryRepository repository;

    @Test
    void creaLasCincoCategoriasPredefinidas() {
        int creadas = seeder.sembrar();

        assertThat(creadas).isEqualTo(5);
        assertThat(repository.findAll())
                .extracting(Category::getName)
                .containsExactlyInAnyOrder("Comida", "Transporte", "Salud", "Ocio", "Otros");
    }

    @Test
    void noDuplicaCategoriasAlSembrarDosVeces() {
        seeder.sembrar();
        int creadasLaSegundaVez = seeder.sembrar();

        assertThat(creadasLaSegundaVez).isZero();
        assertThat(repository.findAll()).hasSize(5);
    }

    @Test
    void lasCategoriasPredefinidasNoTienenDueño() {
        seeder.sembrar();

        assertThat(repository.findAll()).allMatch(Category::esPredefinida);
    }
}
