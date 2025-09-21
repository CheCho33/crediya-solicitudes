package co.com.crediya.solicitudes.sqs.sender;

import co.com.crediya.solicitudes.model.notificacion.gateways.NotificacionGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementación del gateway de notificaciones usando Amazon SQS.
 * 
 * Esta implementación utiliza SQSSender para enviar mensajes a una cola SQS
 * siguiendo los principios de Arquitectura Hexagonal.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class NotificacionGatewayAdapter implements NotificacionGateway {

    private final SQSSender sqsSender;

    @Override
    public Mono<String> enviarMensaje(String mensaje) {
        if (mensaje == null || mensaje.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El mensaje no puede ser null o vacío"));
        }

        log.info("Enviando mensaje de notificación: {}", mensaje);
        
        return sqsSender.send(mensaje)
                .doOnSuccess(messageId -> log.info("Mensaje enviado exitosamente con ID: {}", messageId))
                .doOnError(error -> log.error("Error al enviar mensaje de notificación: {}", error.getMessage()));
    }
}
