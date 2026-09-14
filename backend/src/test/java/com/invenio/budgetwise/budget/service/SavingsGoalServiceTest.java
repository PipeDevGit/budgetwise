package com.invenio.budgetwise.budget.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.invenio.budgetwise.auth.domain.User;
import com.invenio.budgetwise.auth.repository.UserRepository;
import com.invenio.budgetwise.budget.domain.SavingsGoal;
import com.invenio.budgetwise.budget.dto.SavingsGoalRequest;
import com.invenio.budgetwise.budget.dto.SavingsGoalResponse;
import com.invenio.budgetwise.budget.dto.SavingsUpdateRequest;
import com.invenio.budgetwise.budget.repository.SavingsGoalRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class SavingsGoalServiceTest {

    private SavingsGoalRepository savingsGoalRepository;
    private SavingsGoalService savingsGoalService;
    private User ana;

    @BeforeEach
    void setUp() {
        savingsGoalRepository = mock(SavingsGoalRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        savingsGoalService = new SavingsGoalService(savingsGoalRepository, userRepository);

        ana = mock(User.class);
        when(ana.getId()).thenReturn(1L);
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(ana));
    }

    @Test
    void sinAhorroElProgresoEsCero() {
        assertThat(SavingsGoalService.progreso(BigDecimal.ZERO, new BigDecimal("1000.00"))).isZero();
    }

    @Test
    void aLaMitadDelObjetivoElProgresoEs50() {
        assertThat(SavingsGoalService.progreso(new BigDecimal("500.00"), new BigDecimal("1000.00"))).isEqualTo(50);
    }

    @Test
    void unaDivisionNoExactaRedondeaHaciaAbajoSinLanzarExcepcion() {
        // 100 / 300 no es exacto: sin RoundingMode, BigDecimal.divide() lanza ArithmeticException.
        assertThat(SavingsGoalService.progreso(new BigDecimal("100.00"), new BigDecimal("300.00"))).isEqualTo(33);
    }

    @Test
    void ahorrarDeMasTopaElProgresoEn100() {
        assertThat(SavingsGoalService.progreso(new BigDecimal("1500.00"), new BigDecimal("1000.00"))).isEqualTo(100);
    }

    @Test
    void actualizarElAhorroRecalculaElProgreso() {
        SavingsGoal meta = new SavingsGoal("Viaje", new BigDecimal("200000.00"), LocalDate.of(2026, 12, 20), ana);
        when(savingsGoalRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(meta));

        SavingsGoalResponse respuesta = savingsGoalService.actualizarAhorro(
                "ana@example.com", 5L, new SavingsUpdateRequest(new BigDecimal("50000.00")));

        assertThat(respuesta.savedAmount()).isEqualByComparingTo("50000.00");
        assertThat(respuesta.progressPercent()).isEqualTo(25);
    }

    @Test
    void actualizarElAhorroDeUnaMetaAjenaLanza404() {
        when(savingsGoalRepository.findByIdAndUserId(9L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> savingsGoalService.actualizarAhorro(
                        "ana@example.com", 9L, new SavingsUpdateRequest(new BigDecimal("10.00"))))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
    }

    @Test
    void crearDevuelveLaMetaConProgresoCero() {
        when(savingsGoalRepository.save(any(SavingsGoal.class))).thenAnswer(inv -> inv.getArgument(0));

        SavingsGoalResponse creada = savingsGoalService.crear("ana@example.com",
                new SavingsGoalRequest("  Viaje  ", new BigDecimal("200000.00"), LocalDate.of(2026, 12, 20)));

        assertThat(creada.name()).isEqualTo("Viaje");
        assertThat(creada.progressPercent()).isZero();
    }
}
