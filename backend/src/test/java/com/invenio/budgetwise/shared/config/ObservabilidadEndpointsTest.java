package com.invenio.budgetwise.shared.config;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Prueba de integracion: levanta la aplicacion entera para verificar que los
 * endpoints de observabilidad responden sin token y que el filtro de logs corre
 * antes que Spring Security.
 *
 * AutoConfigureObservability hace falta porque, por defecto, Spring Boot apaga
 * la exportacion de metricas en las pruebas.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureObservability
class ObservabilidadEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthDeActuatorEsPublicoYReportaLaAplicacionArriba() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void prometheusEsPublicoYExponeMetricas() throws Exception {
        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("jvm_memory_used_bytes")));
    }

    @Test
    void unaRutaProtegidaSinTokenIgualDevuelveElRequestId() throws Exception {
        // Si el filtro corriera despues de Spring Security, un 403 no tendria
        // request_id y no se podria rastrear en los logs.
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().is4xxClientError())
                .andExpect(header().exists("X-Request-Id"));
    }
}
