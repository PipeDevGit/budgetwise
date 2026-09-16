package com.invenio.budgetwise.ai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invenio.budgetwise.ai.domain.ResumenFinanciero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

/**
 * Prueba lo que el componente de IA hace por su cuenta, sin salir a la red: el
 * prompt que arma, la peticion que pide JSON y la lectura de la respuesta. La
 * llamada real a Gemini se verifica en vivo, no en una prueba unitaria.
 */
class GeminiRecommenderTest {

    private final GeminiRecommender sinClave =
            new GeminiRecommender(RestClient.builder(), new ObjectMapper(), "", "gemini-3.5-flash-lite");

    @Test
    void sinClaveNoEstaConfigurado() {
        assertThat(sinClave.estaConfigurado()).isFalse();
    }

    @Test
    void conClaveEstaConfigurado() {
        GeminiRecommender conClave =
                new GeminiRecommender(RestClient.builder(), new ObjectMapper(), "clave-de-prueba", "gemini-3.5-flash-lite");

        assertThat(conClave.estaConfigurado()).isTrue();
    }

    @Test
    void extraeLosTresConsejosDeUnaRespuestaDeGemini() {
        String respuesta = """
                {"candidates":[{"content":{"parts":[{"text":"{\\"recomendaciones\\":[\\"Uno\\",\\"Dos\\",\\"Tres\\"]}"}]}}]}
                """;

        assertThat(sinClave.extraerConsejos(respuesta)).containsExactly("Uno", "Dos", "Tres");
    }

    @Test
    void unaRespuestaConMenosDeTresConsejosEsUnaFalla() {
        String respuesta = """
                {"candidates":[{"content":{"parts":[{"text":"{\\"recomendaciones\\":[\\"Uno\\",\\"Dos\\"]}"}]}}]}
                """;

        assertThatThrownBy(() -> sinClave.extraerConsejos(respuesta))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("2 consejos");
    }

    @Test
    void unaRespuestaSinTextoEsUnaFalla() {
        // Es lo que llega cuando Gemini bloquea el prompt: no hay candidatos.
        assertThatThrownBy(() -> sinClave.extraerConsejos("{\"candidates\":[]}"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("sin texto");
    }

    @Test
    void unTextoQueNoEsJsonEsUnaFalla() {
        String respuesta = """
                {"candidates":[{"content":{"parts":[{"text":"Aca van tus consejos del mes"}]}}]}
                """;

        assertThatThrownBy(() -> sinClave.extraerConsejos(respuesta))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no es JSON");
    }

    @Test
    void elPromptPideAccionesYLlevaLosMontosConElFormatoDeLaAppPeroNoLosNombresDeLasMetas() {
        ResumenFinanciero resumen = new ResumenFinanciero(
                LocalDate.of(2026, 9, 13),
                new BigDecimal("400000.00"),
                new BigDecimal("85000.00"),
                Map.of("Comida", new BigDecimal("55000.00")),
                Map.of("Comida", new BigDecimal("30000.00")),
                List.of(new ResumenFinanciero.Meta(
                        "Operacion de mi mama", new BigDecimal("600000"), BigDecimal.ZERO, LocalDate.of(2026, 12, 20))));

        // El separador de miles de es-CR es un espacio que no corta la linea: se normaliza para comparar.
        String prompt = sinClave.construirPrompt(resumen).replace('\u00A0', ' ').replace('\u202F', ' ');

        assertThat(prompt)
                .contains("accion concreta")
                .contains("2026-09")
                .contains("400 000,00")
                .contains("Comida: 55 000,00")
                .contains("objetivo 600 000,00")
                .doesNotContain("400000.00")
                .doesNotContain("Operacion de mi mama");
    }

    @Test
    void laMetaLlegaConLoQueHayQueApartarPorMesYaCalculado() {
        // Caso visto en vivo el 2026-09-16: con solo objetivo, ahorrado y fecha, el modelo
        // aconsejo "Aparta 300 000,00 este mes", que es el objetivo total y no lo de un mes.
        ResumenFinanciero.Meta viaje = new ResumenFinanciero.Meta(
                "Viaje", new BigDecimal("300000"), new BigDecimal("120000"), LocalDate.of(2026, 12, 20));

        String meta = GeminiRecommender.describirMeta(viaje, LocalDate.of(2026, 9, 16))
                .replace(' ', ' ').replace(' ', ' ');

        // Faltan 180 000 en cuatro meses (septiembre a diciembre): 45 000 por mes.
        assertThat(meta)
                .contains("falta 180 000,00")
                .contains("hay que apartar 45 000,00 por mes");
    }

    @Test
    void unaMetaAlcanzadaOVencidaNoLlevaMontoPorMes() {
        LocalDate hoy = LocalDate.of(2026, 9, 16);
        ResumenFinanciero.Meta alcanzada = new ResumenFinanciero.Meta(
                "Moto", new BigDecimal("100000"), new BigDecimal("100000"), LocalDate.of(2026, 12, 1));
        ResumenFinanciero.Meta vencida = new ResumenFinanciero.Meta(
                "Curso", new BigDecimal("100000"), new BigDecimal("20000"), LocalDate.of(2026, 8, 1));

        assertThat(GeminiRecommender.describirMeta(alcanzada, hoy))
                .endsWith("ya alcanzada").doesNotContain("por mes");
        assertThat(GeminiRecommender.describirMeta(vencida, hoy))
                .endsWith("fecha limite vencida").doesNotContain("por mes");
    }

    @Test
    void elPromptPideNoHacerCuentas() {
        assertThat(sinClave.construirPrompt(RESUMEN_VACIO)).contains("No hagas cuentas");
    }

    @Test
    void laPeticionPideLaRespuestaEnJson() {
        ResumenFinanciero vacio = new ResumenFinanciero(
                LocalDate.of(2026, 9, 13), BigDecimal.ZERO, BigDecimal.ZERO, Map.of(), Map.of(), List.of());

        Map<String, Object> peticion = sinClave.peticion(vacio);
        @SuppressWarnings("unchecked")
        Map<String, Object> configuracion = (Map<String, Object>) peticion.get("generationConfig");

        assertThat(configuracion)
                .containsEntry("responseMimeType", "application/json")
                .containsKey("responseSchema");
    }

    private static final ResumenFinanciero RESUMEN_VACIO = new ResumenFinanciero(
            LocalDate.of(2026, 9, 13), BigDecimal.ZERO, BigDecimal.ZERO, Map.of(), Map.of(), List.of());

    private static final String RESPUESTA_CON_TRES_CONSEJOS = """
            {"candidates":[{"content":{"parts":[{"text":"{\\"recomendaciones\\":[\\"Uno\\",\\"Dos\\",\\"Tres\\"]}"}]}}]}
            """;

    /**
     * Esta prueba habria atrapado el bug que encontro la prueba en vivo: sin la
     * barra inicial en la ruta, Spring tomaba el nombre del modelo como el esquema
     * de la URL y la llamada nunca salia. Verifica la URL exacta, el metodo, el
     * header de la clave y que el cuerpo pida JSON, sin salir a la red.
     */
    @Test
    void llamaAGenerateContentDelModeloConLaClaveEnUnHeader() {
        RestClient.Builder builder = RestClient.builder().baseUrl(GeminiRecommender.URL_BASE);
        MockRestServiceServer servidor = MockRestServiceServer.bindTo(builder).build();
        GeminiRecommender gemini =
                new GeminiRecommender(builder.build(), new ObjectMapper(), "clave-de-prueba", "gemini-3.5-flash-lite");

        servidor.expect(requestTo(
                        "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash-lite:generateContent"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("x-goog-api-key", "clave-de-prueba"))
                .andExpect(jsonPath("$.generationConfig.responseMimeType").value("application/json"))
                .andRespond(withSuccess(RESPUESTA_CON_TRES_CONSEJOS, MediaType.APPLICATION_JSON));

        List<String> consejos = gemini.recomendar(RESUMEN_VACIO);

        assertThat(consejos).containsExactly("Uno", "Dos", "Tres");
        servidor.verify();
    }

    @Test
    void siGeminiRespondeConErrorLanzaUnaExcepcionParaQueRespondanLasReglas() {
        // 429 es lo que devuelve la API cuando se agota la cuota gratuita.
        RestClient.Builder builder = RestClient.builder().baseUrl(GeminiRecommender.URL_BASE);
        MockRestServiceServer servidor = MockRestServiceServer.bindTo(builder).build();
        GeminiRecommender gemini =
                new GeminiRecommender(builder.build(), new ObjectMapper(), "clave-de-prueba", "gemini-3.5-flash-lite");
        servidor.expect(requestTo(
                        "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash-lite:generateContent"))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));

        assertThatThrownBy(() -> gemini.recomendar(RESUMEN_VACIO)).isInstanceOf(RuntimeException.class);
    }
}
