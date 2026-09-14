package com.invenio.budgetwise.budget.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/** Cuanto lleva ahorrado el usuario para una meta (issue #12). */
public record SavingsUpdateRequest(
        @NotNull(message = "El monto ahorrado es obligatorio")
                @PositiveOrZero(message = "El monto ahorrado no puede ser negativo")
                BigDecimal savedAmount) {
}
