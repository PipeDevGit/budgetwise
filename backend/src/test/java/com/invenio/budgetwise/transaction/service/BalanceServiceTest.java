package com.invenio.budgetwise.transaction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.invenio.budgetwise.auth.domain.User;
import com.invenio.budgetwise.auth.repository.UserRepository;
import com.invenio.budgetwise.transaction.domain.Transaction;
import com.invenio.budgetwise.transaction.domain.TransactionType;
import com.invenio.budgetwise.transaction.dto.BalanceResponse;
import com.invenio.budgetwise.transaction.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** El calculo se prueba con movimientos armados a mano: sin base y sin Spring. */
class BalanceServiceTest {

    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private BalanceService balanceService;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        userRepository = mock(UserRepository.class);
        balanceService = new BalanceService(transactionRepository, userRepository);
    }

    @Test
    void sinMovimientosTodoEsCero() {
        BalanceResponse saldo = balanceService.calcular(List.of());

        assertThat(saldo.totalIncome()).isEqualByComparingTo("0");
        assertThat(saldo.totalExpenses()).isEqualByComparingTo("0");
        assertThat(saldo.balance()).isEqualByComparingTo("0");
    }

    @Test
    void sumaLosIngresosYRestaLosGastos() {
        BalanceResponse saldo = balanceService.calcular(List.of(
                movimiento("450000.00", TransactionType.INGRESO),
                movimiento("25000.50", TransactionType.GASTO),
                movimiento("4999.50", TransactionType.GASTO)));

        assertThat(saldo.totalIncome()).isEqualByComparingTo("450000.00");
        assertThat(saldo.totalExpenses()).isEqualByComparingTo("30000.00");
        assertThat(saldo.balance()).isEqualByComparingTo("420000.00");
    }

    @Test
    void elSaldoPuedeQuedarNegativo() {
        BalanceResponse saldo = balanceService.calcular(List.of(
                movimiento("1000.00", TransactionType.INGRESO),
                movimiento("1500.00", TransactionType.GASTO)));

        assertThat(saldo.balance()).isEqualByComparingTo("-500.00");
    }

    @Test
    void losCentavosNoPierdenPrecision() {
        // Con double, 0.10 + 0.20 da 0.30000000000000004.
        BalanceResponse saldo = balanceService.calcular(List.of(
                movimiento("0.10", TransactionType.INGRESO),
                movimiento("0.20", TransactionType.INGRESO)));

        assertThat(saldo.totalIncome()).isEqualByComparingTo("0.30");
    }

    @Test
    void consultaSoloLosMovimientosDelUsuarioAutenticado() {
        User ana = mock(User.class);
        when(ana.getId()).thenReturn(7L);
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(ana));
        when(transactionRepository.findByUserIdOrderByDateDescIdDesc(7L))
                .thenReturn(List.of(movimiento("100.00", TransactionType.INGRESO)));

        BalanceResponse saldo = balanceService.saldoDe("ana@example.com");

        verify(transactionRepository).findByUserIdOrderByDateDescIdDesc(7L);
        assertThat(saldo.balance()).isEqualByComparingTo("100.00");
    }

    private static Transaction movimiento(String monto, TransactionType tipo) {
        // El usuario y la categoria no participan del calculo del saldo.
        return new Transaction(new BigDecimal(monto), tipo, LocalDate.of(2026, 9, 1), null, null, null);
    }
}
