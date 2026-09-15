package com.invenio.budgetwise.transaction.service;

import static org.assertj.core.api.Assertions.assertThat;
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
import com.invenio.budgetwise.transaction.dto.TransactionResponse;
import com.invenio.budgetwise.transaction.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * La decision de filtrar o no por categoria (issue #8) vive en el service.
 * TransactionFiltroPorCategoriaTest prueba la consulta contra la base; aca se
 * prueba que el service elija la consulta correcta segun venga o no categoryId.
 */
class TransactionServiceFiltroPorCategoriaTest {

    private TransactionRepository transactionRepository;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        transactionService = new TransactionService(
                transactionRepository, mock(CategoryRepository.class), userRepository);

        User ana = mock(User.class);
        when(ana.getId()).thenReturn(1L);
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(ana));
    }

    @Test
    void sinCategoriaDevuelveElListadoCompletoDelUsuario() {
        when(transactionRepository.findByUserIdOrderByDateDescIdDesc(1L)).thenReturn(List.of());

        transactionService.listar("ana@example.com", null);

        verify(transactionRepository).findByUserIdOrderByDateDescIdDesc(1L);
        verify(transactionRepository, never()).findByUserIdAndCategoryIdOrderByDateDescIdDesc(any(), any());
    }

    @Test
    void conCategoriaUsaLaConsultaQueFiltraPorUsuarioYCategoria() {
        Category comida = mock(Category.class);
        when(comida.getId()).thenReturn(10L);
        when(comida.getName()).thenReturn("Comida");
        Transaction almuerzo = new Transaction(new BigDecimal("5000.00"), TransactionType.GASTO,
                LocalDate.of(2026, 9, 1), "Almuerzo", null, comida);
        when(transactionRepository.findByUserIdAndCategoryIdOrderByDateDescIdDesc(1L, 10L)).thenReturn(List.of(almuerzo));

        List<TransactionResponse> respuesta = transactionService.listar("ana@example.com", 10L);

        assertThat(respuesta).extracting(TransactionResponse::description).containsExactly("Almuerzo");
        verify(transactionRepository, never()).findByUserIdOrderByDateDescIdDesc(any());
    }
}
