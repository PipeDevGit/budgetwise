package com.invenio.budgetwise.ai.service;

import com.invenio.budgetwise.ai.domain.ResumenFinanciero;
import com.invenio.budgetwise.ai.dto.RecommendationResponse;
import com.invenio.budgetwise.auth.domain.User;
import com.invenio.budgetwise.auth.repository.UserRepository;
import com.invenio.budgetwise.budget.repository.SavingsGoalRepository;
import com.invenio.budgetwise.transaction.domain.Transaction;
import com.invenio.budgetwise.transaction.domain.TransactionType;
import com.invenio.budgetwise.transaction.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Recomendaciones del mes (issue #16). Arma el resumen financiero del usuario
 * autenticado y se lo pasa al recomendador.
 *
 * D-03 pide llamar primero a un modelo y usar las reglas como respaldo. Por
 * ahora responde solo el recomendador por reglas: la llamada al modelo necesita
 * el SDK de Anthropic, que es una dependencia nueva y espera la aprobacion del
 * equipo (shared-change).
 */
@Service
public class RecommendationService {

    static final String FUENTE_REGLAS = "reglas";

    private final TransactionRepository transactionRepository;
    private final SavingsGoalRepository savingsGoalRepository;
    private final UserRepository userRepository;
    private final RuleBasedRecommender ruleBasedRecommender;

    public RecommendationService(
            TransactionRepository transactionRepository,
            SavingsGoalRepository savingsGoalRepository,
            UserRepository userRepository,
            RuleBasedRecommender ruleBasedRecommender) {
        this.transactionRepository = transactionRepository;
        this.savingsGoalRepository = savingsGoalRepository;
        this.userRepository = userRepository;
        this.ruleBasedRecommender = ruleBasedRecommender;
    }

    @Transactional(readOnly = true)
    public RecommendationResponse recomendar(String email) {
        return recomendar(email, LocalDate.now());
    }

    /** Recibe la fecha para que las pruebas no dependan del dia en que se corren. */
    RecommendationResponse recomendar(String email, LocalDate hoy) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        ResumenFinanciero resumen = resumir(user.getId(), hoy);
        return new RecommendationResponse(ruleBasedRecommender.recomendar(resumen), FUENTE_REGLAS);
    }

    private ResumenFinanciero resumir(Long userId, LocalDate hoy) {
        YearMonth mes = YearMonth.from(hoy);
        YearMonth mesAnterior = mes.minusMonths(1);
        List<Transaction> delMes = movimientosDe(userId, mes);
        List<Transaction> delMesAnterior = movimientosDe(userId, mesAnterior);
        List<ResumenFinanciero.Meta> metas = savingsGoalRepository.findByUserId(userId).stream()
                .map(meta -> new ResumenFinanciero.Meta(
                        meta.getName(), meta.getTargetAmount(), meta.getSavedAmount(), meta.getTargetDate()))
                .toList();
        return new ResumenFinanciero(
                hoy,
                sumar(delMes, TransactionType.INGRESO),
                sumar(delMes, TransactionType.GASTO),
                gastoPorCategoria(delMes),
                gastoPorCategoria(delMesAnterior),
                metas);
    }

    /** Filtra por usuario, igual que todas las consultas de transacciones: nunca datos de otra persona. */
    private List<Transaction> movimientosDe(Long userId, YearMonth mes) {
        return transactionRepository.findByUserIdAndDateBetween(userId, mes.atDay(1), mes.atEndOfMonth());
    }

    private static BigDecimal sumar(List<Transaction> movimientos, TransactionType tipo) {
        return movimientos.stream()
                .filter(movimiento -> movimiento.getType() == tipo)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static Map<String, BigDecimal> gastoPorCategoria(List<Transaction> movimientos) {
        return movimientos.stream()
                .filter(movimiento -> movimiento.getType() == TransactionType.GASTO)
                .collect(Collectors.groupingBy(
                        movimiento -> movimiento.getCategory().getName(),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));
    }
}
