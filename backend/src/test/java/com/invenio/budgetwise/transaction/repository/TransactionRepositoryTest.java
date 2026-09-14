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
        assertThat(transactionRepository.findByUserIdOrderByDateDescIdDesc(ana.getId()))
                .extracting(Transaction::getDescription)
                .containsExactly("Almuerzo de Ana");
    }

    @Test
    void noDevuelveUnMovimientoAjenoAunqueSeSepaElId() {
        Long idDeBeto = transactionRepository.findByUserIdOrderByDateDescIdDesc(beto.getId())
                .getFirst().getId();

        assertThat(transactionRepository.findByIdAndUserId(idDeBeto, ana.getId())).isEmpty();
    }

    @Test
    void guardaLosMontosSinPerderPrecision() {
        assertThat(transactionRepository.findByUserIdOrderByDateDescIdDesc(ana.getId()).getFirst().getAmount())
                .isEqualByComparingTo(new BigDecimal("15000.00"));
    }

    @Test
    void ordenaMovimientosDelMismoDiaPorIdDescendente() {
        // Misma fecha para los dos: sin el desempate por id, el orden entre
        // ellos no seria determinista (issue #7, observacion de Pablo en la
        // review del PR #53).
        Category otros = categoryRepository.save(Category.predefinida("Otros"));
        Transaction primero = transactionRepository.save(new Transaction(
                new BigDecimal("100.00"), TransactionType.GASTO, LocalDate.of(2026, 9, 3), "Primero del dia", ana, otros));
        Transaction segundo = transactionRepository.save(new Transaction(
                new BigDecimal("200.00"), TransactionType.GASTO, LocalDate.of(2026, 9, 3), "Segundo del dia", ana, otros));

        assertThat(transactionRepository.findByUserIdOrderByDateDescIdDesc(ana.getId()))
                .extracting(Transaction::getId)
                .startsWith(segundo.getId(), primero.getId());
    }
}
