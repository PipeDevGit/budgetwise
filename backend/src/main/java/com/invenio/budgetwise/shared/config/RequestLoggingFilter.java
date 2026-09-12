package com.invenio.budgetwise.shared.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Deja una linea de log por cada peticion HTTP con los datos que pide la issue
 * #19: request_id, metodo, ruta, codigo de estado y duracion. Si la peticion
 * termina en 5xx, la linea sale como ERROR y con la traza de la excepcion.
 *
 * Corre antes que Spring Security (HIGHEST_PRECEDENCE), asi que tambien quedan
 * registrados los 401 y 403, que son justo los que hay que ver cuando algo falla
 * desde el navegador.
 *
 * El request_id entra al MDC al principio: cualquier log que se escriba durante
 * la peticion, en cualquier capa, sale con el mismo id. Tambien vuelve en el
 * header X-Request-Id, para poder buscar en los logs lo que le paso a un usuario.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    static final String REQUEST_ID_HEADER = "X-Request-Id";

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Docker, Kubernetes y Prometheus consultan estas rutas cada pocos
        // segundos. Si se loguearan, taparian las peticiones de verdad.
        String path = request.getRequestURI();
        return path.equals("/health") || path.startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString();
        long start = System.nanoTime();
        MDC.put("request_id", requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId);

        Exception failure = null;
        try {
            filterChain.doFilter(request, response);
        } catch (ServletException | IOException | RuntimeException e) {
            failure = e;
            throw e;
        } finally {
            // Con una excepcion sin manejar la respuesta todavia dice 200: el 500
            // lo pone el servidor despues, cuando la excepcion sale de este filtro.
            int status = failure != null ? 500 : response.getStatus();
            MDC.put("method", request.getMethod());
            MDC.put("path", request.getRequestURI());
            MDC.put("status", String.valueOf(status));
            MDC.put("duration_ms", String.valueOf((System.nanoTime() - start) / 1_000_000));

            if (status >= 500) {
                log.error("Peticion fallida", failure);
            } else {
                log.info("Peticion atendida");
            }

            // Los hilos del servidor se reutilizan: sin limpiar, el id de esta
            // peticion apareceria en los logs de la siguiente.
            MDC.clear();
        }
    }
}
