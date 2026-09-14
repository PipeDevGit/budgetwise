package com.invenio.budgetwise.ai.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Lo que el recomendador necesita saber de las finanzas de un usuario, ya
 * agregado (issue #16). No es una entidad JPA: lo arma RecommendationService a
 * partir de las transacciones y las metas, y el recomendador por reglas trabaja
 * solo con esto. Por eso sus reglas se prueban sin base y sin Spring.
 */
public record ResumenFinanciero(
        LocalDate hoy,
        BigDecimal ingresosDelMes,
        BigDecimal gastosDelMes,
        Map<String, BigDecimal> gastoPorCategoriaMesActual,
        Map<String, BigDecimal> gastoPorCategoriaMesAnterior,
        List<Meta> metas) {

    /** Una meta de ahorro reducida a lo que usa la proyeccion. */
    public record Meta(String nombre, BigDecimal objetivo, BigDecimal ahorrado, LocalDate fechaLimite) {
    }
}
