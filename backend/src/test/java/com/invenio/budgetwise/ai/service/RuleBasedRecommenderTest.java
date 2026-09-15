package com.invenio.budgetwise.ai.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.invenio.budgetwise.ai.domain.ResumenFinanciero;
import com.invenio.budgetwise.ai.domain.ResumenFinanciero.Meta;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Las reglas se prueban con resumenes armados a mano: sin base, sin Spring y sin red. */
class RuleBasedRecommenderTest {

    private static final LocalDate HOY = LocalDate.of(2026, 9, 13);

    private final RuleBasedRecommender recomendador = new RuleBasedRecommender();

    @Test
    void siempreDevuelveTresConsejosAunqueNoHayaDatos() {
        List<String> consejos = recomendador.recomendar(resumen("0", "0", Map.of(), Map.of(), List.of()));

        assertThat(consejos).hasSize(3).allSatisfy(consejo -> assertThat(consejo).isNotBlank());
    }

    @Test
    void senalaLaCategoriaCuyoGastoMasCrecio() {
        String consejo = recomendador.categoriaQueMasCrecio(resumen("400000", "85000",
                Map.of("Comida", monto("55000"), "Transporte", monto("30000")),
                Map.of("Comida", monto("30000"), "Transporte", monto("29000")),
                List.of()));

        assertThat(consejo).contains("Comida").contains("subio");
    }

    @Test
    void unaCategoriaSinGastosElMesPasadoCuentaComoCrecimientoCompleto() {
        // Ocio pasa de nada a 40 000; Comida solo sube 25 000.
        String consejo = recomendador.categoriaQueMasCrecio(resumen("400000", "95000",
                Map.of("Comida", monto("55000"), "Ocio", monto("40000")),
                Map.of("Comida", monto("30000")),
                List.of()));

        assertThat(consejo).contains("Ocio");
    }

    @Test
    void siNingunaCategoriaCrecioLoDice() {
        String consejo = recomendador.categoriaQueMasCrecio(resumen("400000", "20000",
                Map.of("Comida", monto("20000")),
                Map.of("Comida", monto("30000")),
                List.of()));

        assertThat(consejo).contains("Ninguna categoria crecio");
    }

    @Test
    void proyectaQueNoSeLlegaALaMetaSiElAhorroDelMesNoAlcanza() {
        // Faltan 600 000 entre septiembre y diciembre: 150 000 por mes. Este mes ahorra 100 000.
        Meta viaje = new Meta("Viaje", monto("600000"), BigDecimal.ZERO, LocalDate.of(2026, 12, 20));

        String consejo = recomendador.proyeccionDeMeta(resumen("400000", "300000", Map.of(), Map.of(), List.of(viaje)));

        assertThat(consejo).contains("Viaje").contains("no llegas a tiempo");
    }

    @Test
    void proyectaQueSeLlegaALaMetaSiElAhorroDelMesAlcanza() {
        // Faltan 400 000 en cuatro meses: 100 000 por mes. Este mes ahorra 150 000.
        Meta viaje = new Meta("Viaje", monto("600000"), monto("200000"), LocalDate.of(2026, 12, 20));

        String consejo = recomendador.proyeccionDeMeta(resumen("400000", "250000", Map.of(), Map.of(), List.of(viaje)));

        assertThat(consejo).contains("Viaje").contains("llegas a tiempo").doesNotContain("no llegas");
    }

    @Test
    void conLaFechaDeLaMetaVencidaPideAjustarla() {
        Meta vencida = new Meta("Moto", monto("900000"), monto("100000"), LocalDate.of(2026, 8, 1));

        String consejo = recomendador.proyeccionDeMeta(resumen("400000", "100000", Map.of(), Map.of(), List.of(vencida)));

        assertThat(consejo).contains("Moto").contains("ya paso");
    }

    @Test
    void avisaCuandoSeGastaMasDeLoQueSeIngresa() {
        String consejo = recomendador.proporcionGastada(resumen("100000", "130000", Map.of(), Map.of(), List.of()));

        assertThat(consejo).contains("mas de lo que ingresaste").contains("130%");
    }

    @Test
    void sinIngresosPideRegistrarlos() {
        String consejo = recomendador.proporcionGastada(resumen("0", "50000", Map.of(), Map.of(), List.of()));

        assertThat(consejo).contains("Registra tus ingresos");
    }

    private static ResumenFinanciero resumen(String ingresos, String gastos,
            Map<String, BigDecimal> mesActual, Map<String, BigDecimal> mesAnterior, List<Meta> metas) {
        return new ResumenFinanciero(HOY, monto(ingresos), monto(gastos), mesActual, mesAnterior, metas);
    }

    private static BigDecimal monto(String valor) {
        return new BigDecimal(valor);
    }
}
