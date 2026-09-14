package com.invenio.budgetwise.category.dto;

/**
 * Forma exacta que ya consume el frontend: el tipo Categoria de
 * frontend/src/api/client.ts (PR #55). predefinida le dice a la pantalla si la
 * categoria es de todos o la creo el usuario.
 */
public record CategoryResponse(Long id, String name, boolean predefinida) {
}
