package com.invenio.budgetwise.transaction.dto;

import java.math.BigDecimal;

/**
 * Totales del usuario autenticado (issue #15). Los tres montos viajan juntos
 * para que el panel de la #11 no tenga que sumar nada del lado del cliente.
 */
public record BalanceResponse(BigDecimal totalIncome, BigDecimal totalExpenses, BigDecimal balance) {
}
