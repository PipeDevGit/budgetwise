package com.invenio.budgetwise.ai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.invenio.budgetwise.ai.dto.RecommendationResponse;
import com.invenio.budgetwise.auth.domain.User;
import com.invenio.budgetwise.auth.repository.UserRepository;
import com.invenio.budgetwise.budget.domain.SavingsGoal;
import com.invenio.budgetwise.budget.repository.SavingsGoalRepository;
import com.invenio.budgetwise.category.domain.Category;
import com.invenio.budgetwise.transaction.domain.Transaction;
import com.invenio.budgetwise.transaction.domain.TransactionType;
import com.invenio.budgetwise.transaction.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Prueba el armado del resumen: que tome el mes en curso y el anterior, que
 * sume solo gastos por categoria y que la respuesta diga de donde salieron los
 * consejos. Las reglas en si ya las cubre RuleBasedRecommenderTest.
 */
class RecommendationServiceTest {

    @Test
    void armaElResumenDelMesYDelAnteriorYRespondeConLasReglas() {
        TransactionRepository transactionRepository = mock(TransactionRepository.class);
        SavingsGoalRepository savingsGoalRepository = mock(SavingsGoalRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        RecommendationService service = new RecommendationService(
                transactionRepository, savingsGoalRepository, userRepository, new RuleBasedRecommender(),
                geminiSinClave());

        User ana = mock(User.class);
        when(ana.getId()).thenReturn(1L);
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(ana));

        Category comida = mock(Category.class);
        when(comida.getName()).thenReturn("Comida");
        Category otros = mock(Category.class);
        when(otros.getName()).thenReturn("Otros");

        when(transactionRepository.findByUserIdAndDateBetween(1L, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30)))
                .thenReturn(List.of(
                        movimiento("400000", TransactionType.INGRESO, otros),
                        movimiento("55000", TransactionType.GASTO, comida)));
        when(transactionRepository.findByUserIdAndDateBetween(1L, LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31)))
                .thenReturn(List.of(movimiento("30000", TransactionType.GASTO, comida)));
        when(savingsGoalRepository.findByUserId(1L)).thenReturn(List.of(
                new SavingsGoal("Viaje", new BigDecimal("600000"), LocalDate.of(2026, 12, 20), ana)));

        RecommendationResponse respuesta = service.recomendar("ana@example.com", LocalDate.of(2026, 9, 13));

        assertThat(respuesta.source()).isEqualTo("reglas");
        assertThat(respuesta.recommendations()).hasSize(3);
        assertThat(respuesta.recommendations().get(0)).contains("Comida");
        assertThat(respuesta.recommendations().get(1)).contains("Viaje");
        // El ingreso de "Otros" no cuenta como gasto: gastado 55 000 de 400 000 es el 14%.
        assertThat(respuesta.recommendations().get(2)).contains("14%");
    }

    @Test
    void conGeminiConfiguradoRespondeConLosConsejosDelModelo() {
        GeminiRecommender gemini = mock(GeminiRecommender.class);
        when(gemini.estaConfigurado()).thenReturn(true);
        when(gemini.recomendar(any())).thenReturn(List.of("Uno", "Dos", "Tres"));

        RecommendationResponse respuesta = servicioCon(gemini).recomendar("ana@example.com", LocalDate.of(2026, 9, 13));

        assertThat(respuesta.source()).isEqualTo("gemini");
        assertThat(respuesta.recommendations()).containsExactly("Uno", "Dos", "Tres");
    }

    @Test
    void siGeminiFallaRespondeConLasReglas() {
        // D-03: la demo no puede depender de la red ni de la cuota gratuita.
        GeminiRecommender gemini = mock(GeminiRecommender.class);
        when(gemini.estaConfigurado()).thenReturn(true);
        when(gemini.recomendar(any())).thenThrow(new IllegalStateException("cuota agotada"));

        RecommendationResponse respuesta = servicioCon(gemini).recomendar("ana@example.com", LocalDate.of(2026, 9, 13));

        assertThat(respuesta.source()).isEqualTo("reglas");
        assertThat(respuesta.recommendations()).hasSize(3);
    }

    /** Servicio con repositorios vacios: el resumen no importa, solo de donde salen los consejos. */
    private static RecommendationService servicioCon(GeminiRecommender gemini) {
        UserRepository userRepository = mock(UserRepository.class);
        User ana = mock(User.class);
        when(ana.getId()).thenReturn(1L);
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(ana));
        return new RecommendationService(mock(TransactionRepository.class), mock(SavingsGoalRepository.class),
                userRepository, new RuleBasedRecommender(), gemini);
    }

    private static GeminiRecommender geminiSinClave() {
        GeminiRecommender gemini = mock(GeminiRecommender.class);
        when(gemini.estaConfigurado()).thenReturn(false);
        return gemini;
    }

    private static Transaction movimiento(String monto, TransactionType tipo, Category categoria) {
        return new Transaction(new BigDecimal(monto), tipo, LocalDate.of(2026, 9, 5), null, null, categoria);
    }
}
