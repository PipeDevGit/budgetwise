package com.invenio.budgetwise.auth.dto;

/** tokenType siempre es "Bearer". expiresInSeconds son las 24 horas de D-08. */
public record AuthResponse(String token, String tokenType, long expiresInSeconds) {
}
