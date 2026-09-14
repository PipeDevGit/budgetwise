package com.invenio.budgetwise.ai.service;

import com.invenio.budgetwise.ai.domain.ResumenFinanciero;
import com.invenio.budgetwise.ai.domain.ResumenFinanciero.Meta;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Recomendador por reglas (issue #16, D-03). Devuelve siempre tres consejos,
 * uno por regla:
 *
 * 1. La categoria cuyo gasto mas crecio respecto al mes anterior.
 * 2. La proyeccion de la meta de ahorro mas cercana, al ritmo de ahorro del mes.
 * 3. Que parte de los ingresos del mes ya se gasto.
 *
 * Es el respaldo obligatorio de D-03: la demo es en vivo y no puede depender de
 * la red. Por si solo ya cumple el criterio de la rubrica.
 */
@Service
public class RuleBasedRecommender {

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);
    private static final BigDecimal UMBRAL_POCO_MARGEN = BigDecimal.valueOf(80);

    public List<String> recomendar(ResumenFinanciero resumen) {
        return List.of(categoriaQueMasCrecio(resumen), proyeccionDeMeta(resumen), proporcionGastada(resumen));
    }

    String categoriaQueMasCrecio(ResumenFinanciero resumen) {
        Map<String, BigDecimal> anterior = resumen.gastoPorCategoriaMesAnterior();
        if (anterior.isEmpty()) {
            return "Todavia no hay gastos del mes pasado para comparar. El mes que viene vas a ver "
                    + "en que categoria crecio tu gasto.";
        }
        // Una categoria que el mes pasado no tuvo gastos cuenta como crecimiento completo.
        Optional<Map.Entry<String, BigDecimal>> mayor = resumen.gastoPorCategoriaMesActual().entrySet().stream()
                .map(actual -> Map.entry(
                        actual.getKey(),
                        actual.getValue().subtract(anterior.getOrDefault(actual.getKey(), BigDecimal.ZERO))))
                .filter(aumento -> aumento.getValue().signum() > 0)
                .max(Map.Entry.comparingByValue());
        if (mayor.isEmpty()) {
            return "Ninguna categoria crecio respecto al mes pasado. Mantene ese ritmo.";
        }
        return "Tu gasto en " + mayor.get().getKey() + " subio " + monto(mayor.get().getValue())
                + " respecto al mes pasado. Es la categoria que mas crecio: revisa si podes recortar ahi.";
    }

    String proyeccionDeMeta(ResumenFinanciero resumen) {
        Optional<Meta> proxima = resumen.metas().stream()
                .filter(meta -> meta.fechaLimite() != null)
                .filter(meta -> meta.ahorrado().compareTo(meta.objetivo()) < 0)
                .min(Comparator.comparing(Meta::fechaLimite));
        if (proxima.isEmpty()) {
            return resumen.metas().isEmpty()
                    ? "Defini una meta de ahorro con fecha limite: asi se puede proyectar si llegas a tiempo."
                    : "Ya alcanzaste tus metas de ahorro. Es buen momento para definir la siguiente.";
        }
        Meta meta = proxima.get();
        if (meta.fechaLimite().isBefore(resumen.hoy())) {
            return "La fecha limite de tu meta " + meta.nombre() + " ya paso. Ajusta la fecha o el monto "
                    + "para volver a proyectarla.";
        }
        // Se cuenta el mes en curso: de septiembre a diciembre quedan cuatro meses para ahorrar.
        long meses = ChronoUnit.MONTHS.between(YearMonth.from(resumen.hoy()), YearMonth.from(meta.fechaLimite())) + 1;
        BigDecimal falta = meta.objetivo().subtract(meta.ahorrado());
        // divide() con modo de redondeo explicito: sin el, una division no exacta lanza ArithmeticException.
        BigDecimal necesarioPorMes = falta.divide(BigDecimal.valueOf(meses), 2, RoundingMode.UP);
        BigDecimal ahorroDelMes = resumen.ingresosDelMes().subtract(resumen.gastosDelMes());
        if (ahorroDelMes.compareTo(necesarioPorMes) >= 0) {
            return "Al ritmo de este mes llegas a tiempo a tu meta " + meta.nombre() + ": necesitas "
                    + monto(necesarioPorMes) + " por mes y este mes vas ahorrando " + monto(ahorroDelMes) + ".";
        }
        return "Para llegar a tu meta " + meta.nombre() + " necesitas ahorrar " + monto(necesarioPorMes)
                + " por mes, pero este mes vas ahorrando " + monto(ahorroDelMes.max(BigDecimal.ZERO))
                + ". Al ritmo actual no llegas a tiempo.";
    }

    String proporcionGastada(ResumenFinanciero resumen) {
        if (resumen.ingresosDelMes().signum() <= 0) {
            return "Registra tus ingresos del mes: sin ellos no se puede calcular cuanto estas ahorrando.";
        }
        BigDecimal porcentaje = resumen.gastosDelMes().multiply(CIEN)
                .divide(resumen.ingresosDelMes(), 0, RoundingMode.HALF_UP);
        if (porcentaje.compareTo(CIEN) > 0) {
            return "Este mes gastaste mas de lo que ingresaste: el " + porcentaje + "% de tus ingresos. "
                    + "Frena los gastos que no sean necesarios.";
        }
        if (porcentaje.compareTo(UMBRAL_POCO_MARGEN) >= 0) {
            return "Ya gastaste el " + porcentaje + "% de tus ingresos del mes. Queda poco margen para imprevistos.";
        }
        return "Llevas gastado el " + porcentaje + "% de tus ingresos del mes: estas ahorrando el "
                + CIEN.subtract(porcentaje) + "%.";
    }

    private static String monto(BigDecimal valor) {
        NumberFormat formato = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-CR"));
        formato.setMinimumFractionDigits(2);
        formato.setMaximumFractionDigits(2);
        return formato.format(valor);
    }
}
