package com.invenio.budgetwise.shared.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.invenio.budgetwise.auth.controller.AuthController;
import com.invenio.budgetwise.auth.dto.RegisterRequest;
import java.lang.reflect.Method;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

/**
 * El manejador se prueba solo, sin levantar el contexto de Spring: alcanza
 * con construir la excepcion como la construiria Spring y llamar al metodo
 * (issue #46).
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @AfterEach
    void limpiarMdc() {
        MDC.clear();
    }

    @Test
    void unResponseStatusExceptionConRazonDevuelveEsaRazonComoMensaje() {
        MDC.put("request_id", "abc-123");
        ResponseStatusException ex =
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email o contrasena incorrectos");

        ResponseEntity<ErrorResponse> response = handler.manejarRespuestaConEstado(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().message()).isEqualTo("Email o contrasena incorrectos");
        assertThat(response.getBody().requestId()).isEqualTo("abc-123");
    }

    @Test
    void unResponseStatusExceptionSinRazonDevuelveUnMensajeGenericoYSinRequestId() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        ResponseEntity<ErrorResponse> response = handler.manejarRespuestaConEstado(ex);

        assertThat(response.getBody().message()).isEqualTo("No se pudo completar la solicitud");
        assertThat(response.getBody().requestId()).isNull();
    }

    @Test
    void unaValidacionFallidaDevuelveElMensajeDelCampoQueFallo() throws NoSuchMethodException {
        MDC.put("request_id", "req-9");
        MethodArgumentNotValidException ex =
                crearExcepcionDeValidacion("La contrasena debe tener al menos 8 caracteres");

        ResponseEntity<ErrorResponse> response = handler.manejarValidacion(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message()).isEqualTo("La contrasena debe tener al menos 8 caracteres");
        assertThat(response.getBody().requestId()).isEqualTo("req-9");
    }

    @Test
    void sinErroresDeCampoElMensajeSigueSiendoGenericoYNuncaExponeInternos() throws NoSuchMethodException {
        MethodArgumentNotValidException ex = crearExcepcionDeValidacion(null);

        ResponseEntity<ErrorResponse> response = handler.manejarValidacion(ex);

        assertThat(response.getBody().message()).isEqualTo("No se pudo completar la solicitud");
    }

    /** Arma la excepcion tal como la generaria Spring al validar un RegisterRequest real. */
    private MethodArgumentNotValidException crearExcepcionDeValidacion(String mensajeDelCampo)
            throws NoSuchMethodException {
        Method metodoRegister = AuthController.class.getDeclaredMethod("register", RegisterRequest.class);
        MethodParameter parametro = new MethodParameter(metodoRegister, 0);

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new RegisterRequest("", "", ""), "registerRequest");
        if (mensajeDelCampo != null) {
            bindingResult.addError(new FieldError("registerRequest", "password", mensajeDelCampo));
        }
        return new MethodArgumentNotValidException(parametro, bindingResult);
    }
}
