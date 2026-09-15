package com.invenio.budgetwise.budget.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/** Presupuesto del mes en curso para una categoria (issue #13). */
public record BudgetRequest(
        @NotNull(message = "La categoria es obligatoria") Long categoryId,
        @NotNull(message = "El limite mensual es obligatorio")
                @Positive(message = "El limite mensual debe ser mayor a cero")
                BigDecimal monthlyLimit) {
}
