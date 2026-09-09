package com.invenio.budgetwise.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Alcance minimo de la issue #4: sin verificacion de correo, sin roles.
 * El profesor los excluyo del MVP; no se dejan "preparados" (CLAUDE.md).
 */
public record RegisterRequest(
        @NotBlank(message = "El nombre es obligatorio") String name,
        @NotBlank @Email(message = "El email no es valido") String email,
        @NotBlank @Size(min = 8, message = "La contrasena debe tener al menos 8 caracteres") String password) {
}
