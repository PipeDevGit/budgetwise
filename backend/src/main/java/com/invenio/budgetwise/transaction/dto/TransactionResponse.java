package com.invenio.budgetwise.transaction.dto;

import com.invenio.budgetwise.transaction.domain.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Nunca se expone la entidad Transaction ni sus relaciones JPA directamente
 * (regla del CLAUDE.md). categoryName viaja junto al id para que el frontend
 * no tenga que pedir la lista de categorias solo para mostrar el nombre.
 */
public record TransactionResponse(
        Long id,
        BigDecimal amount,
        TransactionType type,
        LocalDate date,
        String description,
        Long categoryId,
        String categoryName) {
}
