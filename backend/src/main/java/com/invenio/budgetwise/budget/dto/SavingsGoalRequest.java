package com.invenio.budgetwise.budget.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Crear una meta de ahorro: nombre, monto objetivo y fecha limite (issue #12). */
public record SavingsGoalRequest(
        @NotBlank(message = "El nombre de la meta es obligatorio")
                @Size(max = 120, message = "El nombre no puede superar los 120 caracteres")
                String name,
        @NotNull(message = "El monto objetivo es obligatorio")
                @Positive(message = "El monto objetivo debe ser mayor a cero")
                BigDecimal targetAmount,
        @NotNull(message = "La fecha limite es obligatoria")
                @FutureOrPresent(message = "La fecha limite no puede estar en el pasado")
                LocalDate targetDate) {
}
