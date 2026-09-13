package com.invenio.budgetwise.transaction.dto;

import java.math.BigDecimal;

/**
 * Saldo automatico (issue #15): ingresos totales, gastos totales y el saldo
 * resultante, sobre las transacciones del usuario autenticado.
 */
public record BalanceResponse(BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal balance) {
}
