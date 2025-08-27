package co.com.crediya.solicitudes.model.estados.gateways;

import co.com.crediya.solicitudes.model.estados.Estados;
import reactor.core.publisher.Mono;

/**
 * Gateway para la persistencia de la entidad Estados.
 * Define los contratos para las operaciones de base de datos relacionadas con estados.
 *
 * Este gateway sigue los principios de Arquitectura Hexagonal:
 * - Define contratos del dominio sin dependencias de infraestructura
 * - Permite diferentes implementaciones (R2DBC, JPA, etc.)
 * - Mantiene el dominio libre de detalles técnicos de persistencia
 * - Implementa programación reactiva con Project Reactor
 */
public interface EstadosRepository {

    /**
     * Guarda un nuevo estado en la base de datos.
     *
     * @param estado entidad Estados a persistir
     * @return Mono con la entidad guardada con versión actualizada
     * @throws IllegalArgumentException si el estado es null
     */
    Mono<Estados> save(Estados estado);
    /**
     * Busca un estado por su identificador único.
     *
     * @param idEstado identificador del estado a buscar
     * @return Mono con el estado si existe, Mono.empty() si no existe
     * @throws IllegalArgumentException si el idEstado es null
     */
    Mono<Estados> findById(Long idEstado);

    Mono<Estados> findByNombre(String nombre);


}
