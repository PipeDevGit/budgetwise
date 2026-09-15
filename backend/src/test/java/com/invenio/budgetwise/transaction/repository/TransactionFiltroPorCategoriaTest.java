package com.invenio.budgetwise.transaction.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.invenio.budgetwise.auth.domain.User;
import com.invenio.budgetwise.auth.repository.UserRepository;
import com.invenio.budgetwise.category.domain.Category;
import com.invenio.budgetwise.category.repository.CategoryRepository;
import com.invenio.budgetwise.transaction.domain.Transaction;
import com.invenio.budgetwise.transaction.domain.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * El filtro por categoria de la issue #8 no puede abrir una puerta nueva al
 * aislamiento entre usuarios: las predefinidas las comparte todo el mundo, asi
 * que filtrar por Comida no debe traer la comida de otra persona.
 */
@DataJpaTest
@ActiveProfiles("test")
class TransactionFiltroPorCategoriaTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void filtraPorCategoriaSinTraerMovimientosDeOtroUsuario() {
        User ana = userRepository.save(new User("ana@invenio.edu", "hash", "Ana"));
        User beto = userRepository.save(new User("beto@invenio.edu", "hash", "Beto"));
        Category comida = categoryRepository.save(Category.predefinida("Comida"));
        Category transporte = categoryRepository.save(Category.predefinida("Transporte"));

        transactionRepository.save(gasto("Almuerzo de Ana", ana, comida));
        transactionRepository.save(gasto("Bus de Ana", ana, transporte));
        transactionRepository.save(gasto("Cena de Beto", beto, comida));

        assertThat(transactionRepository.findByUserIdAndCategoryIdOrderByDateDescIdDesc(ana.getId(), comida.getId()))
                .extracting(Transaction::getDescription)
                .containsExactly("Almuerzo de Ana");
    }

    @Test
    void alFiltrarOrdenaLosMovimientosDelMismoDiaPorIdDescendente() {
        User ana = userRepository.save(new User("ana@invenio.edu", "hash", "Ana"));
        Category comida = categoryRepository.save(Category.predefinida("Comida"));

        Transaction desayuno = transactionRepository.save(gasto("Desayuno", ana, comida));
        Transaction almuerzo = transactionRepository.save(gasto("Almuerzo", ana, comida));

        assertThat(transactionRepository.findByUserIdAndCategoryIdOrderByDateDescIdDesc(ana.getId(), comida.getId()))
                .extracting(Transaction::getId)
                .containsExactly(almuerzo.getId(), desayuno.getId());
    }

    private static Transaction gasto(String descripcion, User user, Category category) {
        return new Transaction(new BigDecimal("5000.00"), TransactionType.GASTO,
                LocalDate.of(2026, 9, 1), descripcion, user, category);
    }
}
