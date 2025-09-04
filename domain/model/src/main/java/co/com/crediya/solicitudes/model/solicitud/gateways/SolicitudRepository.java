package co.com.crediya.solicitudes.model.solicitud.gateways;

import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Gateway para la persistencia de la entidad Solicitud.
 * Define los contratos para las operaciones de base de datos relacionadas con solicitudes de préstamo.
 *
 * Este gateway sigue los principios de Arquitectura Hexagonal:
 * - Define contratos del dominio sin dependencias de infraestructura
 * - Permite diferentes implementaciones (R2DBC, JPA, etc.)
 * - Mantiene el dominio libre de detalles técnicos de persistencia
 * - Soporta las reglas de negocio específicas de CrediYa
 * - Implementa programación reactiva con Project Reactor
 */
public interface SolicitudRepository {

    /**
     * Guarda una nueva solicitud en la base de datos.
     *
     * @param solicitud entidad Solicitud a persistir
     * @return Mono con la solicitud guardada con versión actualizada
     * @throws IllegalArgumentException si la solicitud es null
     */
    Mono<Solicitud> save(Solicitud solicitud);

    /**
     * Busca todas las solicitudes que tengan un estado específico.
     *
     * @param idEstado identificador del estado a buscar
     * @return Flux con todas las solicitudes que tienen el estado especificado
     * @throws IllegalArgumentException si el idEstado es null
     */
    Flux<Solicitud> findByIdEstado(Long idEstado);

}
