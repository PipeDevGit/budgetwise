package com.invenio.budgetwise.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Email(message = "El email no es valido") String email,
        @NotBlank(message = "La contrasena es obligatoria") String password) {
}
