package com.invenio.budgetwise.transaction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.invenio.budgetwise.auth.domain.User;
import com.invenio.budgetwise.auth.repository.UserRepository;
import com.invenio.budgetwise.category.domain.Category;
import com.invenio.budgetwise.category.repository.CategoryRepository;
import com.invenio.budgetwise.transaction.domain.Transaction;
import com.invenio.budgetwise.transaction.domain.TransactionType;
import com.invenio.budgetwise.transaction.dto.TransactionRequest;
import com.invenio.budgetwise.transaction.dto.TransactionResponse;
import com.invenio.budgetwise.transaction.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

/**
 * El aislamiento entre usuarios a nivel de base ya lo prueba
 * TransactionRepositoryTest. Aca se prueba la logica propia del service:
 * que una categoria ajena o inexistente se rechace igual (sin confirmarle a
 * nadie cual de las dos cosas paso), y que tocar una transaccion ajena de
 * 404 en vez de dejar pasar el pedido.
 */
class TransactionServiceTest {

    private TransactionRepository transactionRepository;
    private CategoryRepository categoryRepository;
    private UserRepository userRepository;
    private TransactionService transactionService;

    private User ana;
    private Category comida;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        userRepository = mock(UserRepository.class);
        transactionService = new TransactionService(transactionRepository, categoryRepository, userRepository);

        ana = mock(User.class);
        when(ana.getId()).thenReturn(1L);
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(ana));

        comida = mock(Category.class);
        when(comida.getId()).thenReturn(10L);
        when(comida.getName()).thenReturn("Comida");
        when(comida.esPredefinida()).thenReturn(true);
    }

    @Test
    void crearConCategoriaValidaGuardaLaTransaccion() {
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(comida));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));
        TransactionRequest request = new TransactionRequest(
                new BigDecimal("5000.00"), TransactionType.GASTO, LocalDate.of(2026, 9, 1), 10L, "Almuerzo");

        TransactionResponse response = transactionService.crear("ana@example.com", request);

        assertThat(response.amount()).isEqualByComparingTo("5000.00");
        assertThat(response.type()).isEqualTo(TransactionType.GASTO);
        assertThat(response.categoryId()).isEqualTo(10L);
        assertThat(response.categoryName()).isEqualTo("Comida");
        assertThat(response.description()).isEqualTo("Almuerzo");
    }

    @Test
    void crearConCategoriaInexistenteLanza400() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        TransactionRequest request = new TransactionRequest(
                new BigDecimal("100.00"), TransactionType.GASTO, LocalDate.now(), 99L, null);

        assertThatThrownBy(() -> transactionService.crear("ana@example.com", request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400")
                .hasMessageContaining("no existe o no te pertenece");
    }

    @Test
    void crearConCategoriaDeOtroUsuarioLanza400ConElMismoMensajeQueSiNoExistiera() {
        User beto = mock(User.class);
        when(beto.getId()).thenReturn(2L);
        Category categoriaDeBeto = mock(Category.class);
        when(categoriaDeBeto.esPredefinida()).thenReturn(false);
        when(categoriaDeBeto.getOwner()).thenReturn(beto);
        when(categoryRepository.findById(20L)).thenReturn(Optional.of(categoriaDeBeto));
        TransactionRequest request = new TransactionRequest(
                new BigDecimal("100.00"), TransactionType.GASTO, LocalDate.now(), 20L, null);

        assertThatThrownBy(() -> transactionService.crear("ana@example.com", request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("no existe o no te pertenece");
    }

    @Test
    void listarDevuelveSoloLasDelUsuarioAutenticado() {
        Transaction transaction = new Transaction(
                new BigDecimal("200.00"), TransactionType.INGRESO, LocalDate.now(), "Freelance", ana, comida);
        when(transactionRepository.findByUserIdOrderByDateDesc(1L)).thenReturn(List.of(transaction));

        List<TransactionResponse> respuesta = transactionService.listar("ana@example.com");

        assertThat(respuesta).hasSize(1);
        assertThat(respuesta.getFirst().description()).isEqualTo("Freelance");
    }

    @Test
    void actualizarUnaTransaccionAjenaLanza404() {
        when(transactionRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.empty());
        TransactionRequest request = new TransactionRequest(
                new BigDecimal("100.00"), TransactionType.GASTO, LocalDate.now(), 10L, null);

        assertThatThrownBy(() -> transactionService.actualizar("ana@example.com", 5L, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404")
                .hasMessageContaining("La transaccion no existe");
    }

    @Test
    void actualizarUnaTransaccionPropiaCambiaSusDatos() {
        Transaction existente = new Transaction(
                new BigDecimal("100.00"), TransactionType.GASTO, LocalDate.of(2026, 1, 1), "Vieja", ana, comida);
        when(transactionRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(existente));
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(comida));
        TransactionRequest request = new TransactionRequest(
                new BigDecimal("300.00"), TransactionType.INGRESO, LocalDate.of(2026, 9, 5), 10L, "Nueva");

        TransactionResponse response = transactionService.actualizar("ana@example.com", 5L, request);

        assertThat(response.amount()).isEqualByComparingTo("300.00");
        assertThat(response.type()).isEqualTo(TransactionType.INGRESO);
        assertThat(response.date()).isEqualTo(LocalDate.of(2026, 9, 5));
        assertThat(response.description()).isEqualTo("Nueva");
    }

    @Test
    void eliminarUnaTransaccionAjenaLanza404YNoBorraNada() {
        when(transactionRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.eliminar("ana@example.com", 5L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
        verify(transactionRepository, never()).delete(any());
    }

    @Test
    void eliminarUnaTransaccionPropiaLaBorra() {
        Transaction existente = new Transaction(
                new BigDecimal("100.00"), TransactionType.GASTO, LocalDate.now(), "Para borrar", ana, comida);
        when(transactionRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(existente));

        transactionService.eliminar("ana@example.com", 5L);

        verify(transactionRepository).delete(existente);
    }
}
