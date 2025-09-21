package co.com.crediya.solicitudes.model.notificacion.gateways;

import reactor.core.publisher.Mono;

/**
 * Gateway para el envío de notificaciones.
 * Define los contratos para las operaciones de notificación del sistema.
 *
 * Este gateway sigue los principios de Arquitectura Hexagonal:
 * - Define contratos del dominio sin dependencias de infraestructura
 * - Permite diferentes implementaciones (SQS, Email, SMS, etc.)
 * - Mantiene el dominio libre de detalles técnicos de notificación
 * - Implementa programación reactiva con Project Reactor
 */
public interface NotificacionGateway {

    /**
     * Envía un mensaje de notificación.
     *
     * @param mensaje el mensaje a enviar
     * @return Mono con el identificador del mensaje enviado
     * @throws IllegalArgumentException si el mensaje es null o vacío
     */
    Mono<String> enviarMensaje(String mensaje);
}
