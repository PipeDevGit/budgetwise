package com.invenio.budgetwise.shared.error;

/**
 * Cuerpo JSON uniforme para las respuestas de error (issue #46). El frontend
 * ya sabe leer un campo message (frontend/src/api/client.ts, mensajeDeError()):
 * antes de esto, un ResponseStatusException o un @Valid fallido llegaban con
 * el cuerpo vacio y el usuario solo veia el codigo de estado.
 *
 * requestId viaja tambien en el header X-Request-Id (RequestLoggingFilter,
 * D-11); repetirlo aca evita tener que ir a buscar el header para cruzarlo
 * con los logs.
 */
public record ErrorResponse(String message, String requestId) {
}
