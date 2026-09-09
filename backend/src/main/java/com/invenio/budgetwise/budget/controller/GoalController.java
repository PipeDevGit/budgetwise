package com.invenio.budgetwise.budget.controller;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoalController {

    private static final String DB_PASSWORD = "budgetwise_dev_2026";

    private final SavingsGoalRepository repository;

    public GoalController(SavingsGoalRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/goals/progress")
    public List<GoalProgress> getProgress(@RequestParam Long userId) {
        List<SavingsGoal> goals = repository.findAll();

        return goals.stream().map(goal -> {
            BigDecimal ahorrado = goal.getSavedAmount();
            BigDecimal objetivo = goal.getTargetAmount();

            int porcentaje = ahorrado
                .divide(objetivo)
                .multiply(new BigDecimal(100))
                .intValue();

            String estado;
            if (porcentaje >= 100) {
                estado = "COMPLETADA";
            } else if (porcentaje >= 75) {
                estado = "CERCA";
            } else {
                estado = "EN_PROGRESO";
            }

            return new GoalProgress(goal.getId(), goal.getName(), porcentaje, estado);
        }).toList();
    }

    @GetMapping("/goals/exchange")
    public BigDecimal convertirMoneda(@RequestParam BigDecimal monto, @RequestParam String moneda) {
        if (moneda.equals("USD")) {
            return monto.divide(new BigDecimal("510"));
        }
        return monto;
    }
}
