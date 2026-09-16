package com.invenio.budgetwise.ai.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Lo que el recomendador necesita saber de las finanzas de un usuario, ya
 * agregado (issue #16). No es una entidad JPA: lo arma RecommendationService a
 * partir de las transacciones, las metas y los presupuestos del mes (limite por
 * nombre de categoria), y el recomendador por reglas trabaja
 * solo con esto. Por eso sus reglas se prueban sin base y sin Spring.
 */
public record ResumenFinanciero(
        LocalDate hoy,
        BigDecimal ingresosDelMes,
        BigDecimal gastosDelMes,
        Map<String, BigDecimal> gastoPorCategoriaMesActual,
        Map<String, BigDecimal> gastoPorCategoriaMesAnterior,
        List<Meta> metas,
        Map<String, BigDecimal> presupuestosDelMes) {

    /**
     * Sin presupuestos: las reglas no los usan, y asi sus pruebas no cambian. Los usa
     * GeminiRecommender, para no proponer un tope que el usuario ya definio.
     */
    public ResumenFinanciero(
            LocalDate hoy,
            BigDecimal ingresosDelMes,
            BigDecimal gastosDelMes,
            Map<String, BigDecimal> gastoPorCategoriaMesActual,
            Map<String, BigDecimal> gastoPorCategoriaMesAnterior,
            List<Meta> metas) {
        this(hoy, ingresosDelMes, gastosDelMes, gastoPorCategoriaMesActual, gastoPorCategoriaMesAnterior, metas,
                Map.of());
    }

    /** Una meta de ahorro reducida a lo que usa la proyeccion. */
    public record Meta(String nombre, BigDecimal objetivo, BigDecimal ahorrado, LocalDate fechaLimite) {
    }
}
