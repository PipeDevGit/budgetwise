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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Lo que se prueba aca no es Spring Data, es el aislamiento entre usuarios:
 * que nadie pueda ver los movimientos de otro. Es la regla que mas facil se
 * rompe cuando alguien agrega una consulta nueva.
 */
@DataJpaTest
@ActiveProfiles("test")
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User ana;
    private User beto;

    @BeforeEach
    void prepararDatos() {
        ana = userRepository.save(new User("ana@invenio.edu", "hash", "Ana"));
        beto = userRepository.save(new User("beto@invenio.edu", "hash", "Beto"));
        Category comida = categoryRepository.save(Category.predefinida("Comida"));

        transactionRepository.save(new Transaction(
                new BigDecimal("15000.00"), TransactionType.GASTO,
                LocalDate.of(2026, 9, 1), "Almuerzo de Ana", ana, comida));
        transactionRepository.save(new Transaction(
                new BigDecimal("9000.00"), TransactionType.GASTO,
                LocalDate.of(2026, 9, 2), "Cena de Beto", beto, comida));
    }

    @Test
    void cadaUsuarioVeSoloSusPropiosMovimientos() {
        assertThat(transactionRepository.findByUserIdOrderByDateDesc(ana.getId()))
                .extracting(Transaction::getDescription)
                .containsExactly("Almuerzo de Ana");
    }

    @Test
    void noDevuelveUnMovimientoAjenoAunqueSeSepaElId() {
        Long idDeBeto = transactionRepository.findByUserIdOrderByDateDesc(beto.getId())
                .getFirst().getId();

        assertThat(transactionRepository.findByIdAndUserId(idDeBeto, ana.getId())).isEmpty();
    }

    @Test
    void guardaLosMontosSinPerderPrecision() {
        assertThat(transactionRepository.findByUserIdOrderByDateDesc(ana.getId()).getFirst().getAmount())
                .isEqualByComparingTo(new BigDecimal("15000.00"));
    }
}
