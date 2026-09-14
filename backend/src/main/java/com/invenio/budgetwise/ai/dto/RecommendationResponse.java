package com.invenio.budgetwise.ai.dto;

import java.util.List;

/**
 * Los tres consejos del mes y quien los genero. source se muestra en la demo
 * para que quede claro si respondio el modelo o el respaldo por reglas (D-03).
 */
public record RecommendationResponse(List<String> recommendations, String source) {
}
