package co.com.crediya.solicitudes.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Manejador global de errores para endpoints funcionales de WebFlux.
 * 
 * Este componente captura todas las excepciones no manejadas y las convierte
 * en respuestas HTTP apropiadas con formato JSON consistente.
 * 
 * Sigue los principios de Arquitectura Hexagonal:
 * - Maneja errores de forma centralizada
 * - Proporciona respuestas consistentes
 * - No expone detalles internos del sistema
 */
@Component
@Order(-2)// Prioridad alta para capturar errores antes que otros handlers
@Slf4j
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            log.error("Error after response was committed: {}", ex.getMessage());
            return Mono.error(ex);
        }

        // Determinar el código de estado HTTP apropiado
        HttpStatus status = determineHttpStatus(ex);

        // Crear mensaje de error estructurado
        String errorMessage = createErrorMessage(ex, status);

        // Configurar headers de respuesta
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // Log del error (simplificado)
        log.error("Error no manejado en endpoint: {} - Status: {} - Error: {}",
                exchange.getRequest().getPath(), status, ex.getMessage());
        // Escribir respuesta de error
        DataBuffer buffer = response.bufferFactory()
                .wrap(errorMessage.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    /**
     * Determina el código de estado HTTP apropiado basado en el tipo de excepción.
     */
    private HttpStatus determineHttpStatus(Throwable ex) {
        if (ex instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof IllegalStateException) {
            return HttpStatus.CONFLICT;
        } else if (ex instanceof RuntimeException) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * Crea un mensaje de error estructurado en formato JSON.
     */
    private String createErrorMessage(Throwable ex, HttpStatus status) {
        return String.format("""
                {
                    "timestamp": "%s",
                    "error": "%s",
                    "message": "%s",
                }
                """,
                java.time.Instant.now(),
                status.value(),
                ex.getMessage() != null ? ex.getMessage() : "Error interno del servidor"
        );
    }
}
