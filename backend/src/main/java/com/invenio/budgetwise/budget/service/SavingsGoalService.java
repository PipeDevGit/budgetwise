package com.invenio.budgetwise.budget.service;

import com.invenio.budgetwise.auth.domain.User;
import com.invenio.budgetwise.auth.repository.UserRepository;
import com.invenio.budgetwise.budget.domain.SavingsGoal;
import com.invenio.budgetwise.budget.dto.SavingsGoalRequest;
import com.invenio.budgetwise.budget.dto.SavingsGoalResponse;
import com.invenio.budgetwise.budget.dto.SavingsUpdateRequest;
import com.invenio.budgetwise.budget.repository.SavingsGoalRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** Metas de ahorro (issue #12). El calculo del progreso vive aca, en progreso(). */
@Service
public class SavingsGoalService {

    private static final String META_INEXISTENTE = "La meta de ahorro no existe";
    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    private final SavingsGoalRepository savingsGoalRepository;
    private final UserRepository userRepository;

    public SavingsGoalService(SavingsGoalRepository savingsGoalRepository, UserRepository userRepository) {
        this.savingsGoalRepository = savingsGoalRepository;
        this.userRepository = userRepository;
    }

    /**
     * Porcentaje entero de avance, entre 0 y 100, para la barra de progreso.
     *
     * divide() lleva el modo de redondeo explicito a proposito: sin el, una
     * division que no es exacta (100 / 300) lanza ArithmeticException. Se
     * redondea hacia abajo para que la barra no marque 100% antes de llegar, y
     * se topa en 100 si se ahorro de mas.
     */
    static int progreso(BigDecimal ahorrado, BigDecimal objetivo) {
        if (objetivo.signum() <= 0) {
            return 0;
        }
        int porcentaje = ahorrado.multiply(CIEN).divide(objetivo, 0, RoundingMode.DOWN).intValue();
        return Math.max(0, Math.min(100, porcentaje));
    }

    @Transactional(readOnly = true)
    public List<SavingsGoalResponse> listar(String email) {
        Long userId = usuarioAutenticado(email).getId();
        return savingsGoalRepository.findByUserId(userId).stream()
                .map(SavingsGoalService::aRespuesta)
                .toList();
    }

    @Transactional
    public SavingsGoalResponse crear(String email, SavingsGoalRequest request) {
        User user = usuarioAutenticado(email);
        SavingsGoal meta = new SavingsGoal(request.name().trim(), request.targetAmount(), request.targetDate(), user);
        return aRespuesta(savingsGoalRepository.save(meta));
    }

    /** Una meta ajena responde 404, igual que una inexistente: no se confirma que exista. */
    @Transactional
    public SavingsGoalResponse actualizarAhorro(String email, Long id, SavingsUpdateRequest request) {
        Long userId = usuarioAutenticado(email).getId();
        SavingsGoal meta = savingsGoalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, META_INEXISTENTE));
        meta.setSavedAmount(request.savedAmount());
        return aRespuesta(meta);
    }

    private User usuarioAutenticado(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private static SavingsGoalResponse aRespuesta(SavingsGoal meta) {
        return new SavingsGoalResponse(
                meta.getId(),
                meta.getName(),
                meta.getTargetAmount(),
                meta.getSavedAmount(),
                meta.getTargetDate(),
                progreso(meta.getSavedAmount(), meta.getTargetAmount()));
    }
}
