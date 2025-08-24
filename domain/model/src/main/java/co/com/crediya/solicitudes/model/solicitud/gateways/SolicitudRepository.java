package co.com.crediya.solicitudes.model.solicitud.gateways;

import java.time.LocalDateTime;

import co.com.crediya.solicitudes.model.estados.EstadoId;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.SolicitudId;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamoId;
import co.com.crediya.solicitudes.model.valueobjects.Email;
import co.com.crediya.solicitudes.model.valueobjects.Monto;
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
     * Actualiza una solicitud existente en la base de datos.
     * Utiliza control de concurrencia optimista basado en versión.
     * 
     * @param solicitud entidad Solicitud a actualizar
     * @return Mono con la solicitud actualizada con nueva versión
     * @throws IllegalArgumentException si la solicitud es null
     * @throws IllegalStateException si la versión no coincide (concurrencia)
     */
    Mono<Solicitud> update(Solicitud solicitud);
    
    /**
     * Busca una solicitud por su identificador único.
     * 
     * @param idSolicitud identificador de la solicitud a buscar
     * @return Mono con la solicitud si existe, Mono.empty() si no existe
     * @throws IllegalArgumentException si el idSolicitud es null
     */
    Mono<Solicitud> findById(SolicitudId idSolicitud);
    
    /**
     * Busca solicitudes por email del solicitante.
     * 
     * @param email email del solicitante
     * @return Flux con las solicitudes del solicitante
     * @throws IllegalArgumentException si el email es null
     */
    Flux<Solicitud> findByEmail(Email email);
    
    /**
     * Busca solicitudes por estado específico.
     * 
     * @param idEstado identificador del estado a buscar
     * @return Flux con las solicitudes que tienen el estado especificado
     * @throws IllegalArgumentException si el idEstado es null
     */
    Flux<Solicitud> findByEstado(EstadoId idEstado);
    
    /**
     * Busca solicitudes por tipo de préstamo.
     * 
     * @param idTipoPrestamo identificador del tipo de préstamo
     * @return Flux con las solicitudes del tipo especificado
     * @throws IllegalArgumentException si el idTipoPrestamo es null
     */
    Flux<Solicitud> findByTipoPrestamo(TipoPrestamoId idTipoPrestamo);
    
    /**
     * Busca solicitudes por rango de montos.
     * 
     * @param montoMinimo monto mínimo (inclusive)
     * @param montoMaximo monto máximo (inclusive)
     * @return Flux con las solicitudes dentro del rango de montos
     * @throws IllegalArgumentException si los montos son null o el mínimo es mayor que el máximo
     */
    Flux<Solicitud> findByRangoMonto(Monto montoMinimo, Monto montoMaximo);
    
    /**
     * Busca solicitudes por rango de fechas de creación.
     * 
     * @param fechaInicio fecha de inicio (inclusive)
     * @param fechaFin fecha de fin (inclusive)
     * @return Flux con las solicitudes creadas en el rango de fechas
     * @throws IllegalArgumentException si las fechas son null o la fecha inicio es posterior a la fecha fin
     */
    Flux<Solicitud> findByRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    /**
     * Busca solicitudes que necesitan revisión manual.
     * 
     * @return Flux con las solicitudes en estado de revisión manual
     */
    Flux<Solicitud> findPendientesRevision();
    
    /**
     * Busca solicitudes aprobadas.
     * 
     * @return Flux con las solicitudes aprobadas
     */
    Flux<Solicitud> findAprobadas();
    
    /**
     * Busca solicitudes rechazadas.
     * 
     * @return Flux con las solicitudes rechazadas
     */
    Flux<Solicitud> findRechazadas();
    
    /**
     * Obtiene todas las solicitudes disponibles.
     * 
     * @return Flux con todas las solicitudes ordenadas por fecha de creación (descendente)
     */
    Flux<Solicitud> findAll();
    
    /**
     * Obtiene todas las solicitudes ordenadas por un criterio específico.
     * 
     * @param ordenCriterio criterio de ordenamiento ("fechaCreacion", "monto", "email", "estado")
     * @param ascendente true para orden ascendente, false para descendente
     * @return Flux con las solicitudes ordenadas
     * @throws IllegalArgumentException si el criterio de orden es inválido
     */
    Flux<Solicitud> findAllOrderedBy(String ordenCriterio, boolean ascendente);
    
    /**
     * Obtiene solicitudes con paginación.
     * 
     * @param pagina número de página (base 0)
     * @param tamanoPagina tamaño de cada página
     * @return Flux con las solicitudes para la página especificada
     * @throws IllegalArgumentException si la página o tamaño son inválidos
     */
    Flux<Solicitud> findAllPaginated(int pagina, int tamanoPagina);
    
    /**
     * Verifica si existe una solicitud con el identificador especificado.
     * 
     * @param idSolicitud identificador de la solicitud a verificar
     * @return Mono<Boolean> con true si existe, false en caso contrario
     * @throws IllegalArgumentException si el idSolicitud es null
     */
    Mono<Boolean> existsById(SolicitudId idSolicitud);
    
    /**
     * Verifica si existe una solicitud con el email especificado.
     * 
     * @param email email del solicitante a verificar
     * @return Mono<Boolean> con true si existe, false en caso contrario
     * @throws IllegalArgumentException si el email es null
     */
    Mono<Boolean> existsByEmail(Email email);
    
    /**
     * Elimina una solicitud por su identificador.
     * 
     * @param idSolicitud identificador de la solicitud a eliminar
     * @return Mono<Boolean> con true si se eliminó correctamente, false si no existía
     * @throws IllegalArgumentException si el idSolicitud es null
     */
    Mono<Boolean> deleteById(SolicitudId idSolicitud);
    
    /**
     * Cuenta el número total de solicitudes en la base de datos.
     * 
     * @return Mono<Long> con el número total de solicitudes
     */
    Mono<Long> count();
    
    /**
     * Cuenta solicitudes por estado específico.
     * 
     * @param idEstado identificador del estado
     * @return Mono<Long> con el número de solicitudes en el estado especificado
     * @throws IllegalArgumentException si el idEstado es null
     */
    Mono<Long> countByEstado(EstadoId idEstado);
    
    /**
     * Cuenta solicitudes por tipo de préstamo.
     * 
     * @param idTipoPrestamo identificador del tipo de préstamo
     * @return Mono<Long> con el número de solicitudes del tipo especificado
     * @throws IllegalArgumentException si el idTipoPrestamo es null
     */
    Mono<Long> countByTipoPrestamo(TipoPrestamoId idTipoPrestamo);
    
    /**
     * Busca solicitudes que coincidan con múltiples criterios.
     * 
     * @param email filtro por email del solicitante (opcional)
     * @param idEstado filtro por estado (opcional)
     * @param idTipoPrestamo filtro por tipo de préstamo (opcional)
     * @param montoMinimo filtro por monto mínimo (opcional)
     * @param montoMaximo filtro por monto máximo (opcional)
     * @param fechaInicio filtro por fecha de inicio (opcional)
     * @param fechaFin filtro por fecha de fin (opcional)
     * @return Flux con las solicitudes que coinciden con los criterios especificados
     */
    Flux<Solicitud> findByCriterios(Email email, 
                                   EstadoId idEstado,
                                   TipoPrestamoId idTipoPrestamo,
                                   Monto montoMinimo, 
                                   Monto montoMaximo,
                                   LocalDateTime fechaInicio,
                                   LocalDateTime fechaFin);
    
    /**
     * Obtiene las solicitudes más recientes.
     * 
     * @param limite número máximo de solicitudes a retornar
     * @return Flux con las solicitudes más recientes
     * @throws IllegalArgumentException si el límite es inválido
     */
    Flux<Solicitud> findMasRecientes(int limite);
    
    /**
     * Busca solicitudes por email y estado.
     * 
     * @param email email del solicitante
     * @param idEstado identificador del estado
     * @return Flux con las solicitudes que coinciden con ambos criterios
     * @throws IllegalArgumentException si el email o idEstado son null
     */
    Flux<Solicitud> findByEmailAndEstado(Email email, EstadoId idEstado);
    
    /**
     * Obtiene estadísticas de solicitudes por estado.
     * 
     * @return Flux con pares de estado y conteo de solicitudes
     */
    Flux<Object[]> findEstadisticasPorEstado();
    
    /**
     * Obtiene estadísticas de solicitudes por tipo de préstamo.
     * 
     * @return Flux con pares de tipo de préstamo y conteo de solicitudes
     */
    Flux<Object[]> findEstadisticasPorTipoPrestamo();
    
    /**
     * Busca solicitudes que requieren seguimiento (pendientes por más de X días).
     * 
     * @param diasLimite número de días límite para considerar seguimiento
     * @return Flux con las solicitudes que requieren seguimiento
     * @throws IllegalArgumentException si el límite de días es inválido
     */
    Flux<Solicitud> findRequierenSeguimiento(int diasLimite);
}
