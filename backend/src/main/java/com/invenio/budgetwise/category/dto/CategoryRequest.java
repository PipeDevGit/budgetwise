package com.invenio.budgetwise.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "El nombre de la categoria es obligatorio")
                @Size(max = 80, message = "El nombre no puede superar los 80 caracteres")
                String name) {
}
