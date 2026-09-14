package com.invenio.budgetwise.budget.dto;

import java.math.BigDecimal;

/**
 * Un presupuesto del mes junto con lo gastado hasta hoy (issue #13). exceeded
 * es la marca que pide la issue: el frontend no compara montos, solo lee este
 * campo para decidir si muestra la alerta.
 */
public record BudgetStatusResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String period,
        BigDecimal monthlyLimit,
        BigDecimal spent,
        boolean exceeded) {
}
