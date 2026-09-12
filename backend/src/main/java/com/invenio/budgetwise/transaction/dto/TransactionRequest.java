package com.invenio.budgetwise.transaction.dto;

import com.invenio.budgetwise.transaction.domain.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * El monto siempre viaja positivo: el signo (si suma o resta al saldo) lo da
 * el type, no el numero (issue #7). La categoria se valida en el service,
 * no aca: hay que mirar la base para saber si existe y de quien es.
 */
public record TransactionRequest(
        @NotNull(message = "El monto es obligatorio")
                @Positive(message = "El monto debe ser mayor a cero")
                BigDecimal amount,
        @NotNull(message = "El tipo es obligatorio (INGRESO o GASTO)") TransactionType type,
        @NotNull(message = "La fecha es obligatoria") LocalDate date,
        @NotNull(message = "La categoria es obligatoria") Long categoryId,
        @Size(max = 255, message = "La descripcion no puede superar los 255 caracteres") String description) {
}
