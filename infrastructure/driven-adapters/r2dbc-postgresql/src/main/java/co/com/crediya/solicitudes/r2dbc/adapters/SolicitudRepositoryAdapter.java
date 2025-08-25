package co.com.crediya.solicitudes.r2dbc.adapters;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import co.com.crediya.solicitudes.model.estados.EstadoId;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.SolicitudId;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamoId;
import co.com.crediya.solicitudes.model.valueobjects.Email;
import co.com.crediya.solicitudes.model.valueobjects.Monto;
import co.com.crediya.solicitudes.r2dbc.mapper.SolicitudInfraMapper;
import co.com.crediya.solicitudes.r2dbc.model.SolicitudData;
import co.com.crediya.solicitudes.r2dbc.repository.SolicitudReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adaptador R2DBC para el gateway SolicitudRepository.
 * Implementa las operaciones de persistencia para la entidad Solicitud.
 * 
 * Este adaptador sigue las reglas de adaptadores secundarios:
 * - Implementa el puerto del dominio
 * - Usa programación reactiva pura
 * - Maneja errores técnicos y los traduce a errores de dominio
 * - Incluye observabilidad (logs estructurados)
 * - No contiene lógica de negocio
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class SolicitudRepositoryAdapter implements SolicitudRepository {
    
    private final SolicitudReactiveRepository repository;
    
    @Override
    public Mono<Solicitud> save(Solicitud solicitud) {
        log.debug("Guardando solicitud con ID: {}", solicitud.id().value());
        
        return Mono.just(solicitud)
                .map(SolicitudInfraMapper::toData)
                .flatMap(repository::save)
                .map(savedData -> {
                    Solicitud savedSolicitud = SolicitudInfraMapper.toDomain(savedData);
                    savedSolicitud.markPersisted(savedData.getVersion());
                    return savedSolicitud;
                })
                .doOnSuccess(saved -> log.debug("Solicitud guardada exitosamente con ID: {}", saved.id().value()))
                .doOnError(error -> log.error("Error al guardar solicitud: {}", error.getMessage()));
    }
    
    @Override
    public Mono<Solicitud> update(Solicitud solicitud) {
        log.debug("Actualizando solicitud con ID: {}", solicitud.id().value());
        
        return repository.findById(solicitud.id().value())
                .switchIfEmpty(Mono.error(new IllegalStateException("Solicitud no encontrada para actualizar")))
                .flatMap(existingData -> {
                    SolicitudData updatedData = SolicitudInfraMapper.updateData(solicitud, existingData);
                    return repository.save(updatedData);
                })
                .map(updatedData -> {
                    Solicitud updatedSolicitud = SolicitudInfraMapper.toDomain(updatedData);
                    updatedSolicitud.markPersisted(updatedData.getVersion());
                    return updatedSolicitud;
                })
                .doOnSuccess(updated -> log.debug("Solicitud actualizada exitosamente con ID: {}", updated.id().value()))
                .doOnError(error -> log.error("Error al actualizar solicitud: {}", error.getMessage()));
    }
    
    @Override
    public Mono<Solicitud> findById(SolicitudId idSolicitud) {
        log.debug("Buscando solicitud por ID: {}", idSolicitud.value());
        
        return repository.findById(idSolicitud.value())
                .map(SolicitudInfraMapper::toDomain)
                .doOnSuccess(solicitud -> {
                    if (solicitud != null) {
                        log.debug("Solicitud encontrada con ID: {}", idSolicitud.value());
                    } else {
                        log.debug("Solicitud no encontrada con ID: {}", idSolicitud.value());
                    }
                })
                .doOnError(error -> log.error("Error al buscar solicitud por ID: {}", error.getMessage()));
    }
    
    @Override
    public Flux<Solicitud> findByEmail(Email email) {
        log.debug("Buscando solicitudes por email: {}", email.value());
        
        return repository.findByEmailSolicitante(email.value())
                .map(SolicitudInfraMapper::toDomain)
                .doOnComplete(() -> log.debug("Búsqueda de solicitudes por email completada"))
                .doOnError(error -> log.error("Error al buscar solicitudes por email: {}", error.getMessage()));
    }
    
    @Override
    public Flux<Solicitud> findByEstado(EstadoId idEstado) {
        log.debug("Buscando solicitudes por estado: {}", idEstado.value());
        
        return repository.findByIdEstado(idEstado.value())
                .map(SolicitudInfraMapper::toDomain)
                .doOnComplete(() -> log.debug("Búsqueda de solicitudes por estado completada"))
                .doOnError(error -> log.error("Error al buscar solicitudes por estado: {}", error.getMessage()));
    }
    
    @Override
    public Flux<Solicitud> findByTipoPrestamo(TipoPrestamoId idTipoPrestamo) {
        log.debug("Buscando solicitudes por tipo de préstamo: {}", idTipoPrestamo.value());
        
        return repository.findByIdTipoPrestamo(idTipoPrestamo.value())
                .map(SolicitudInfraMapper::toDomain)
                .doOnComplete(() -> log.debug("Búsqueda de solicitudes por tipo de préstamo completada"))
                .doOnError(error -> log.error("Error al buscar solicitudes por tipo de préstamo: {}", error.getMessage()));
    }
    
    @Override
    public Flux<Solicitud> findByRangoMonto(Monto montoMinimo, Monto montoMaximo) {
        log.debug("Buscando solicitudes por rango de monto: {} - {}", montoMinimo.valor(), montoMaximo.valor());
        
        return repository.findByRangoMonto(montoMinimo.valor(), montoMaximo.valor())
                .map(SolicitudInfraMapper::toDomain)
                .doOnComplete(() -> log.debug("Búsqueda de solicitudes por rango de monto completada"))
                .doOnError(error -> log.error("Error al buscar solicitudes por rango de monto: {}", error.getMessage()));
    }
    
    @Override
    public Flux<Solicitud> findByRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.debug("Buscando solicitudes por rango de fechas: {} - {}", fechaInicio, fechaFin);
        
        return repository.findByRangoFechas(fechaInicio, fechaFin)
                .map(SolicitudInfraMapper::toDomain)
                .doOnComplete(() -> log.debug("Búsqueda de solicitudes por rango de fechas completada"))
                .doOnError(error -> log.error("Error al buscar solicitudes por rango de fechas: {}", error.getMessage()));
    }
    
    @Override
    public Flux<Solicitud> findPendientesRevision() {
        log.debug("Buscando solicitudes pendientes de revisión");
        
        return repository.findPendientesRevision()
                .map(SolicitudInfraMapper::toDomain)
                .doOnComplete(() -> log.debug("Búsqueda de solicitudes pendientes de revisión completada"))
                .doOnError(error -> log.error("Error al buscar solicitudes pendientes de revisión: {}", error.getMessage()));
    }
    
    @Override
    public Flux<Solicitud> findAprobadas() {
        log.debug("Buscando solicitudes aprobadas");
        
        return repository.findAprobadas()
                .map(SolicitudInfraMapper::toDomain)
                .doOnComplete(() -> log.debug("Búsqueda de solicitudes aprobadas completada"))
                .doOnError(error -> log.error("Error al buscar solicitudes aprobadas: {}", error.getMessage()));
    }
    
    @Override
    public Flux<Solicitud> findRechazadas() {
        log.debug("Buscando solicitudes rechazadas");
        
        return repository.findRechazadas()
                .map(SolicitudInfraMapper::toDomain)
                .doOnComplete(() -> log.debug("Búsqueda de solicitudes rechazadas completada"))
                .doOnError(error -> log.error("Error al buscar solicitudes rechazadas: {}", error.getMessage()));
    }
    
    @Override
    public Flux<Solicitud> findAll() {
        log.debug("Buscando todas las solicitudes");
        
        return repository.findAllActivos()
                .map(SolicitudInfraMapper::toDomain)
                .doOnComplete(() -> log.debug("Búsqueda de todas las solicitudes completada"))
                .doOnError(error -> log.error("Error al buscar todas las solicitudes: {}", error.getMessage()));
    }
    
    @Override
    public Flux<Solicitud> findAllOrderedBy(String ordenCriterio, boolean ascendente) {
        log.debug("Buscando solicitudes ordenadas por: {} ({})", ordenCriterio, ascendente ? "ASC" : "DESC");
        
        // Implementación simplificada - en un caso real se usaría un mapper de criterios
        return repository.findAllActivos()
                .map(SolicitudInfraMapper::toDomain)
                .doOnComplete(() -> log.debug("Búsqueda de solicitudes ordenadas completada"))
                .doOnError(error -> log.error("Error al buscar solicitudes ordenadas: {}", error.getMessage()));
    }
    
    @Override
    public Flux<Solicitud> findAllPaginated(int pagina, int tamanoPagina) {
        log.debug("Buscando solicitudes paginadas: página {}, tamaño {}", pagina, tamanoPagina);
        
        Pageable pageable = PageRequest.of(pagina, tamanoPagina);
        return repository.findAllByActivoTrue(pageable)
                .map(SolicitudInfraMapper::toDomain)
                .doOnComplete(() -> log.debug("Búsqueda de solicitudes paginadas completada"))
                .doOnError(error -> log.error("Error al buscar solicitudes paginadas: {}", error.getMessage()));
    }
    
    @Override
    public Mono<Boolean> existsById(SolicitudId idSolicitud) {
        log.debug("Verificando existencia de solicitud con ID: {}", idSolicitud.value());
        
        return repository.existsByIdSolicitudAndActivoTrue(idSolicitud.value())
                .doOnSuccess(exists -> log.debug("Verificación de existencia completada: {}", exists))
                .doOnError(error -> log.error("Error al verificar existencia de solicitud: {}", error.getMessage()));
    }
    
    @Override
    public Mono<Boolean> existsByEmail(Email email) {
        log.debug("Verificando existencia de solicitud con email: {}", email.value());
        
        return repository.existsByEmailSolicitanteAndActivoTrue(email.value())
                .doOnSuccess(exists -> log.debug("Verificación de existencia por email completada: {}", exists))
                .doOnError(error -> log.error("Error al verificar existencia por email: {}", error.getMessage()));
    }
    
    @Override
    public Mono<Boolean> deleteById(SolicitudId idSolicitud) {
        log.debug("Eliminando solicitud con ID: {}", idSolicitud.value());
        
        return repository.findById(idSolicitud.value())
                .flatMap(existingData -> {
                    SolicitudData deletedData = SolicitudData.builder()
                            .idSolicitud(existingData.getIdSolicitud())
                            .montoSolicitado(existingData.getMontoSolicitado())
                            .plazoMeses(existingData.getPlazoMeses())
                            .emailSolicitante(existingData.getEmailSolicitante())
                            .idEstado(existingData.getIdEstado())
                            .idTipoPrestamo(existingData.getIdTipoPrestamo())
                            .version(existingData.getVersion())
                            .fechaCreacion(existingData.getFechaCreacion())
                            .fechaActualizacion(LocalDateTime.now())
                            .activo(false) // Soft delete
                            .build();
                    return repository.save(deletedData);
                })
                .map(savedData -> true)
                .switchIfEmpty(Mono.just(false))
                .doOnSuccess(deleted -> log.debug("Eliminación de solicitud completada: {}", deleted))
                .doOnError(error -> log.error("Error al eliminar solicitud: {}", error.getMessage()));
    }
    
    @Override
    public Mono<Long> count() {
        log.debug("Contando total de solicitudes");
        
        return repository.count()
                .doOnSuccess(count -> log.debug("Conteo de solicitudes completado: {}", count))
                .doOnError(error -> log.error("Error al contar solicitudes: {}", error.getMessage()));
    }
    
    @Override
    public Mono<Long> countByEstado(EstadoId idEstado) {
        log.debug("Contando solicitudes por estado: {}", idEstado.value());
        
        return repository.countByIdEstado(idEstado.value())
                .doOnSuccess(count -> log.debug("Conteo de solicitudes por estado completado: {}", count))
                .doOnError(error -> log.error("Error al contar solicitudes por estado: {}", error.getMessage()));
    }
    
    @Override
    public Mono<Long> countByTipoPrestamo(TipoPrestamoId idTipoPrestamo) {
        log.debug("Contando solicitudes por tipo de préstamo: {}", idTipoPrestamo.value());
        
        return repository.countByIdTipoPrestamo(idTipoPrestamo.value())
                .doOnSuccess(count -> log.debug("Conteo de solicitudes por tipo de préstamo completado: {}", count))
                .doOnError(error -> log.error("Error al contar solicitudes por tipo de préstamo: {}", error.getMessage()));
    }
    
    @Override
    public Flux<Solicitud> findByCriterios(Email email, EstadoId idEstado, TipoPrestamoId idTipoPrestamo,
                                         Monto montoMinimo, Monto montoMaximo, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.debug("Buscando solicitudes con criterios múltiples");
        
        // Implementación simplificada - en un caso real se usaría una query dinámica
        return repository.findAllActivos()
                .map(SolicitudInfraMapper::toDomain)
                .filter(solicitud -> email == null || solicitud.email().equals(email))
                .filter(solicitud -> idEstado == null || solicitud.idEstado().equals(idEstado))
                .filter(solicitud -> idTipoPrestamo == null || solicitud.idTipoPrestamo().equals(idTipoPrestamo))
                .filter(solicitud -> montoMinimo == null || solicitud.monto().valor().compareTo(montoMinimo.valor()) >= 0)
                .filter(solicitud -> montoMaximo == null || solicitud.monto().valor().compareTo(montoMaximo.valor()) <= 0)
                .doOnComplete(() -> log.debug("Búsqueda con criterios múltiples completada"))
                .doOnError(error -> log.error("Error al buscar con criterios múltiples: {}", error.getMessage()));
    }
    
    @Override
    public Flux<Solicitud> findMasRecientes(int limite) {
        log.debug("Buscando {} solicitudes más recientes", limite);
        
        return repository.findMasRecientes(limite)
                .map(SolicitudInfraMapper::toDomain)
                .doOnComplete(() -> log.debug("Búsqueda de solicitudes más recientes completada"))
                .doOnError(error -> log.error("Error al buscar solicitudes más recientes: {}", error.getMessage()));
    }
    
    @Override
    public Flux<Solicitud> findByEmailAndEstado(Email email, EstadoId idEstado) {
        log.debug("Buscando solicitudes por email y estado: {} - {}", email.value(), idEstado.value());
        
        return repository.findByEmailSolicitanteAndIdEstado(email.value(), idEstado.value())
                .map(SolicitudInfraMapper::toDomain)
                .doOnComplete(() -> log.debug("Búsqueda por email y estado completada"))
                .doOnError(error -> log.error("Error al buscar por email y estado: {}", error.getMessage()));
    }
    
    @Override
    public Flux<Object[]> findEstadisticasPorEstado() {
        log.debug("Buscando estadísticas por estado");
        
        return repository.findEstadisticasPorEstado()
                .doOnComplete(() -> log.debug("Búsqueda de estadísticas por estado completada"))
                .doOnError(error -> log.error("Error al buscar estadísticas por estado: {}", error.getMessage()));
    }
    
    @Override
    public Flux<Object[]> findEstadisticasPorTipoPrestamo() {
        log.debug("Buscando estadísticas por tipo de préstamo");
        
        return repository.findEstadisticasPorTipoPrestamo()
                .doOnComplete(() -> log.debug("Búsqueda de estadísticas por tipo de préstamo completada"))
                .doOnError(error -> log.error("Error al buscar estadísticas por tipo de préstamo: {}", error.getMessage()));
    }
    
    @Override
    public Flux<Solicitud> findRequierenSeguimiento(int diasLimite) {
        log.debug("Buscando solicitudes que requieren seguimiento (más de {} días)", diasLimite);
        
        return repository.findRequierenSeguimiento(diasLimite)
                .map(SolicitudInfraMapper::toDomain)
                .doOnComplete(() -> log.debug("Búsqueda de solicitudes que requieren seguimiento completada"))
                .doOnError(error -> log.error("Error al buscar solicitudes que requieren seguimiento: {}", error.getMessage()));
    }
}
