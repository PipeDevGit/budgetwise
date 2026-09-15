package com.invenio.budgetwise.budget.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.invenio.budgetwise.auth.domain.User;
import com.invenio.budgetwise.auth.repository.UserRepository;
import com.invenio.budgetwise.budget.domain.Budget;
import com.invenio.budgetwise.budget.dto.BudgetRequest;
import com.invenio.budgetwise.budget.dto.BudgetStatusResponse;
import com.invenio.budgetwise.budget.repository.BudgetRepository;
import com.invenio.budgetwise.category.domain.Category;
import com.invenio.budgetwise.category.repository.CategoryRepository;
import com.invenio.budgetwise.transaction.domain.Transaction;
import com.invenio.budgetwise.transaction.domain.TransactionType;
import com.invenio.budgetwise.transaction.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class BudgetServiceTest {

    private static final YearMonth SEPTIEMBRE = YearMonth.of(2026, 9);

    private BudgetRepository budgetRepository;
    private TransactionRepository transactionRepository;
    private CategoryRepository categoryRepository;
    private BudgetService budgetService;

    private User ana;
    private Category comida;
    private Category transporte;

    @BeforeEach
    void setUp() {
        budgetRepository = mock(BudgetRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        budgetService = new BudgetService(budgetRepository, transactionRepository, categoryRepository, userRepository);

        ana = mock(User.class);
        when(ana.getId()).thenReturn(1L);
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(ana));

        comida = categoria(10L, "Comida");
        transporte = categoria(20L, "Transporte");
    }

    @Test
    void gastarMenosQueElPresupuestoNoExcede() {
        assertThat(BudgetService.excedido(new BigDecimal("50000.00"), new BigDecimal("49999.99"))).isFalse();
    }

    @Test
    void gastarExactamenteElPresupuestoNoExcede() {
        // Caso limite de la issue #13: cumplir el presupuesto no es pasarse.
        assertThat(BudgetService.excedido(new BigDecimal("50000.00"), new BigDecimal("50000.00"))).isFalse();
    }

    @Test
    void unCentavoPorEncimaDelPresupuestoExcede() {
        assertThat(BudgetService.excedido(new BigDecimal("50000.00"), new BigDecimal("50000.01"))).isTrue();
    }

    @Test
    void elEstadoDelMesSumaSoloLosGastosDeLaCategoriaDelPresupuesto() {
        Budget presupuestoComida = new Budget(new BigDecimal("50000.00"), SEPTIEMBRE, ana, comida);
        when(budgetRepository.findByUserIdAndPeriod(1L, "2026-09")).thenReturn(List.of(presupuestoComida));
        when(transactionRepository.findByUserIdAndDateBetween(1L, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30)))
                .thenReturn(List.of(
                        movimiento("30000.00", TransactionType.GASTO, comida),
                        movimiento("25000.00", TransactionType.GASTO, comida),
                        movimiento("90000.00", TransactionType.GASTO, transporte),
                        movimiento("100000.00", TransactionType.INGRESO, comida)));

        List<BudgetStatusResponse> estado = budgetService.estadoDelMes("ana@example.com", SEPTIEMBRE);

        assertThat(estado).hasSize(1);
        assertThat(estado.getFirst().spent()).isEqualByComparingTo("55000.00");
        assertThat(estado.getFirst().exceeded()).isTrue();
        assertThat(estado.getFirst().categoryName()).isEqualTo("Comida");
    }

    @Test
    void definirDosVecesLaMismaCategoriaActualizaElLimiteEnVezDeDuplicar() {
        Budget existente = new Budget(new BigDecimal("50000.00"), SEPTIEMBRE, ana, comida);
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(comida));
        when(budgetRepository.findByUserIdAndCategoryIdAndPeriod(1L, 10L, "2026-09")).thenReturn(Optional.of(existente));
        when(budgetRepository.save(any(Budget.class))).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.findByUserIdAndDateBetween(any(), any(), any())).thenReturn(List.of());

        BudgetStatusResponse estado = budgetService.definir(
                "ana@example.com", new BudgetRequest(10L, new BigDecimal("80000.00")), SEPTIEMBRE);

        assertThat(existente.getMonthlyLimit()).isEqualByComparingTo("80000.00");
        assertThat(estado.monthlyLimit()).isEqualByComparingTo("80000.00");
        assertThat(estado.exceeded()).isFalse();
    }

    @Test
    void definirConUnaCategoriaDeOtroUsuarioLanza400() {
        User beto = mock(User.class);
        when(beto.getId()).thenReturn(2L);
        Category deBeto = mock(Category.class);
        when(deBeto.esPredefinida()).thenReturn(false);
        when(deBeto.getOwner()).thenReturn(beto);
        when(categoryRepository.findById(30L)).thenReturn(Optional.of(deBeto));

        assertThatThrownBy(() -> budgetService.definir(
                        "ana@example.com", new BudgetRequest(30L, new BigDecimal("1000.00")), SEPTIEMBRE))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400");
    }

    private static Category categoria(Long id, String nombre) {
        Category categoria = mock(Category.class);
        when(categoria.getId()).thenReturn(id);
        when(categoria.getName()).thenReturn(nombre);
        when(categoria.esPredefinida()).thenReturn(true);
        return categoria;
    }

    private static Transaction movimiento(String monto, TransactionType tipo, Category categoria) {
        return new Transaction(new BigDecimal(monto), tipo, LocalDate.of(2026, 9, 5), null, null, categoria);
    }
}
