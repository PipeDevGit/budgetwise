package com.invenio.budgetwise.shared.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static final String SECRET = "esta-es-una-clave-de-prueba-de-al-menos-32-bytes-para-hs256";

    @Test
    void generaUnTokenQueValidaAlMismoEmail() {
        JwtService jwtService = new JwtService(SECRET, 24);

        String token = jwtService.generate("ana@example.com");

        assertThat(jwtService.validateAndGetEmail(token)).contains("ana@example.com");
    }

    @Test
    void unTokenFirmadoConOtraClaveNoValida() {
        JwtService jwtService = new JwtService(SECRET, 24);
        JwtService otroServicio =
                new JwtService("otra-clave-completamente-distinta-de-al-menos-32-bytes", 24);

        String token = otroServicio.generate("ana@example.com");

        assertThat(jwtService.validateAndGetEmail(token)).isEmpty();
    }

    @Test
    void unTextoQueNoEsUnTokenNoValida() {
        JwtService jwtService = new JwtService(SECRET, 24);

        assertThat(jwtService.validateAndGetEmail("esto-no-es-un-jwt")).isEmpty();
    }

    @Test
    void unTokenExpiradoNoValida() throws InterruptedException {
        // expiration-hours en 0 hace que expire casi al instante.
        JwtService jwtService = new JwtService(SECRET, 0);

        String token = jwtService.generate("ana@example.com");
        Thread.sleep(50);

        assertThat(jwtService.validateAndGetEmail(token)).isEmpty();
    }

    @Test
    void expirationSecondsReflejaLasHorasConfiguradas() {
        JwtService jwtService = new JwtService(SECRET, 24);

        assertThat(jwtService.expirationSeconds()).isEqualTo(24 * 60 * 60);
    }
}
