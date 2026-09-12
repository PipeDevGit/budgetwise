package com.invenio.budgetwise.shared.error;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * Centraliza el cuerpo de las respuestas de error (issue #46). Sin esto, un
 * ResponseStatusException (login con clave incorrecta, email repetido) o una
 * validacion de @Valid que falla (por ejemplo en RegisterRequest) llegaban al
 * frontend con el cuerpo vacio: el usuario solo veia "La API respondio 401"
 * en vez del mensaje real, aunque la validacion ya estaba escrita y corria
 * bien del lado del backend.
 *
 * No reemplaza a /error: lo que no cae en estos dos casos lo sigue manejando
 * el controlador de errores por defecto de Spring Boot, y esa ruta tiene que
 * seguir siendo publica en SecurityConfig (issue #4, un 400 real no se puede
 * disfrazar de 403 vacio).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String MENSAJE_GENERICO = "No se pudo completar la solicitud";

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> manejarRespuestaConEstado(ResponseStatusException ex) {
        String message = ex.getReason() != null ? ex.getReason() : MENSAJE_GENERICO;
        return ResponseEntity.status(ex.getStatusCode()).body(new ErrorResponse(message, requestId()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse(MENSAJE_GENERICO);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(message, requestId()));
    }

    // No se expone nada de la excepcion mas alla de su mensaje ya pensado
    // para el usuario (getReason() o el message() de la anotacion de
    // validacion): nunca el nombre de la clase, una traza ni una consulta SQL.
    private String requestId() {
        return MDC.get("request_id");
    }
}
