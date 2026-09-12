package com.invenio.budgetwise.shared.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.servlet.FilterChain;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.MDC;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/** El filtro se prueba sin levantar el contexto de Spring, con peticiones simuladas. */
@ExtendWith(OutputCaptureExtension.class)
class RequestLoggingFilterTest {

    private final RequestLoggingFilter filter = new RequestLoggingFilter();

    @Test
    void elRequestIdDelHeaderEsElMismoQueVenLosLogsDuranteLaPeticion() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> idDuranteLaPeticion = new AtomicReference<>();
        FilterChain chain = (req, res) -> idDuranteLaPeticion.set(MDC.get("request_id"));

        filter.doFilter(new MockHttpServletRequest("GET", "/api/transactions"), response, chain);

        String header = response.getHeader(RequestLoggingFilter.REQUEST_ID_HEADER);
        assertThat(header).isNotBlank();
        assertThat(idDuranteLaPeticion.get()).isEqualTo(header);
    }

    @Test
    void cadaPeticionRecibeUnIdDistinto() throws Exception {
        MockHttpServletResponse primera = new MockHttpServletResponse();
        MockHttpServletResponse segunda = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> {};

        filter.doFilter(new MockHttpServletRequest("GET", "/api/transactions"), primera, chain);
        filter.doFilter(new MockHttpServletRequest("GET", "/api/transactions"), segunda, chain);

        assertThat(primera.getHeader(RequestLoggingFilter.REQUEST_ID_HEADER))
                .isNotEqualTo(segunda.getHeader(RequestLoggingFilter.REQUEST_ID_HEADER));
    }

    @Test
    void alTerminarLimpiaElMdcParaNoContaminarLaSiguientePeticion() throws Exception {
        filter.doFilter(
                new MockHttpServletRequest("GET", "/api/transactions"),
                new MockHttpServletResponse(),
                (req, res) -> {});

        assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty();
    }

    @Test
    void unaExcepcionSinManejarSeRelanzaYQuedaLogueadaComoError(CapturedOutput output) {
        FilterChain chain = (req, res) -> {
            throw new IllegalStateException("se rompio el calculo del saldo");
        };

        assertThatThrownBy(() -> filter.doFilter(
                        new MockHttpServletRequest("GET", "/api/transactions"),
                        new MockHttpServletResponse(),
                        chain))
                .isInstanceOf(IllegalStateException.class);

        assertThat(output).contains("Peticion fallida").contains("se rompio el calculo del saldo");
        assertThat(MDC.getCopyOfContextMap()).isNullOrEmpty();
    }

    @Test
    void noLogueaLosHealthchecksNiActuator() throws Exception {
        MockHttpServletResponse health = new MockHttpServletResponse();
        MockHttpServletResponse prometheus = new MockHttpServletResponse();

        filter.doFilter(new MockHttpServletRequest("GET", "/health"), health, (req, res) -> {});
        filter.doFilter(new MockHttpServletRequest("GET", "/actuator/prometheus"), prometheus, (req, res) -> {});

        assertThat(health.getHeader(RequestLoggingFilter.REQUEST_ID_HEADER)).isNull();
        assertThat(prometheus.getHeader(RequestLoggingFilter.REQUEST_ID_HEADER)).isNull();
    }
}
