package com.invenio.budgetwise.budget.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * progressPercent ya viene calculado (0 a 100): la barra de progreso del
 * frontend solo lo dibuja, no hace cuentas con dinero.
 */
public record SavingsGoalResponse(
        Long id,
        String name,
        BigDecimal targetAmount,
        BigDecimal savedAmount,
        LocalDate targetDate,
        int progressPercent) {
}
