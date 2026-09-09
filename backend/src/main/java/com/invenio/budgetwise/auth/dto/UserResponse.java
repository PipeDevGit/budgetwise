package com.invenio.budgetwise.auth.dto;

/** Nunca se expone el User (entidad JPA) ni el passwordHash hacia afuera. */
public record UserResponse(Long id, String email, String name) {
}
