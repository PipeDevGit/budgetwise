package com.invenio.budgetwise.shared.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.invenio.budgetwise.shared.dto.HealthResponse;
import org.junit.jupiter.api.Test;

/** El service se prueba sin levantar el contexto de Spring: ese es el punto. */
class HealthServiceTest {

    private final HealthService healthService = new HealthService();

    @Test
    void reportaLaAplicacionComoDisponible() {
        HealthResponse response = healthService.check();

        assertThat(response.status()).isEqualTo("ok");
        assertThat(response.application()).isEqualTo("budgetwise");
    }
}
