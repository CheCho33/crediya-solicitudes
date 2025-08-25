package co.com.crediya.solicitudes.r2dbc.adapters;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamoId;
import co.com.crediya.solicitudes.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.crediya.solicitudes.model.valueobjects.Monto;
import co.com.crediya.solicitudes.r2dbc.mapper.TipoPrestamoInfraMapper;
import co.com.crediya.solicitudes.r2dbc.model.TipoPrestamoData;
import co.com.crediya.solicitudes.r2dbc.repository.TipoPrestamoReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adaptador R2DBC para el gateway TipoPrestamoRepository.
 * Implementa las operaciones de persistencia para la entidad TipoPrestamo.
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
public class TipoPrestamoRepositoryAdapter implements TipoPrestamoRepository {
    
    private final TipoPrestamoReactiveRepository repository;
    private final TipoPrestamoInfraMapper mapper;
    
    @Override
    public Mono<TipoPrestamo> save(TipoPrestamo tipoPrestamo) {
        if (tipoPrestamo == null) {
            return Mono.error(new IllegalArgumentException("Tipo de préstamo no puede ser null"));
        }
        
        log.info("event=tipoprestamo.save action=create tipoPrestamoId={}", tipoPrestamo.id().value());
        
        TipoPrestamoData tipoPrestamoData = mapper.toData(tipoPrestamo);
        return repository.save(tipoPrestamoData)
                .map(mapper::toDomain)
                .doOnSuccess(saved -> log.info("event=tipoprestamo.save status=success tipoPrestamoId={}", saved.id().value()))
                .doOnError(error -> log.error("event=tipoprestamo.save status=error tipoPrestamoId={} error={}", 
                    tipoPrestamo.id().value(), error.getMessage()));
    }
    
    @Override
    public Mono<TipoPrestamo> update(TipoPrestamo tipoPrestamo) {
        if (tipoPrestamo == null) {
            return Mono.error(new IllegalArgumentException("Tipo de préstamo no puede ser null"));
        }
        
        log.info("event=tipoprestamo.update action=update tipoPrestamoId={} version={}", 
            tipoPrestamo.id().value(), tipoPrestamo.version());
        
        TipoPrestamoData tipoPrestamoData = mapper.toData(tipoPrestamo).withIncrementedVersion();
        return repository.save(tipoPrestamoData)
                .map(mapper::toDomain)
                .doOnSuccess(updated -> log.info("event=tipoprestamo.update status=success tipoPrestamoId={} version={}", 
                    updated.id().value(), updated.version()))
                .doOnError(error -> log.error("event=tipoprestamo.update status=error tipoPrestamoId={} error={}", 
                    tipoPrestamo.id().value(), error.getMessage()));
    }
    
    @Override
    public Mono<TipoPrestamo> findById(TipoPrestamoId idTipoPrestamo) {
        if (idTipoPrestamo == null) {
            return Mono.error(new IllegalArgumentException("ID de tipo de préstamo no puede ser null"));
        }
        
        UUID uuid = mapper.toUUID(idTipoPrestamo);
        log.debug("event=tipoprestamo.findById action=search tipoPrestamoId={}", uuid);
        
        return repository.findById(uuid)
                .map(mapper::toDomain)
                .doOnSuccess(found -> {
                    if (found != null) {
                        log.debug("event=tipoprestamo.findById status=found tipoPrestamoId={}", uuid);
                    } else {
                        log.debug("event=tipoprestamo.findById status=not_found tipoPrestamoId={}", uuid);
                    }
                })
                .doOnError(error -> log.error("event=tipoprestamo.findById status=error tipoPrestamoId={} error={}", 
                    uuid, error.getMessage()));
    }
    
    @Override
    public Mono<TipoPrestamo> findByNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return Mono.error(new IllegalArgumentException("Nombre no puede ser null o vacío"));
        }
        
        log.debug("event=tipoprestamo.findByNombre action=search nombre={}", nombre);
        
        return repository.findByNombre(nombre)
                .map(mapper::toDomain)
                .doOnSuccess(found -> {
                    if (found != null) {
                        log.debug("event=tipoprestamo.findByNombre status=found nombre={}", nombre);
                    } else {
                        log.debug("event=tipoprestamo.findByNombre status=not_found nombre={}", nombre);
                    }
                })
                .doOnError(error -> log.error("event=tipoprestamo.findByNombre status=error nombre={} error={}", 
                    nombre, error.getMessage()));
    }
    
    @Override
    public Flux<TipoPrestamo> findByNombreContaining(String nombreParcial) {
        if (nombreParcial == null) {
            return Flux.error(new IllegalArgumentException("Nombre parcial no puede ser null"));
        }
        
        log.debug("event=tipoprestamo.findByNombreContaining action=search nombreParcial={}", nombreParcial);
        
        return repository.findByNombreContaining(nombreParcial)
                .map(mapper::toDomain)
                .doOnComplete(() -> log.debug("event=tipoprestamo.findByNombreContaining status=completed nombreParcial={}", nombreParcial))
                .doOnError(error -> log.error("event=tipoprestamo.findByNombreContaining status=error nombreParcial={} error={}", 
                    nombreParcial, error.getMessage()));
    }
    
    @Override
    public Flux<TipoPrestamo> findByMontoPermitido(Monto monto) {
        if (monto == null) {
            return Flux.error(new IllegalArgumentException("Monto no puede ser null"));
        }
        
        BigDecimal montoValue = mapper.toBigDecimal(monto);
        log.debug("event=tipoprestamo.findByMontoPermitido action=search monto={}", montoValue);
        
        return repository.findByMontoPermitido(montoValue)
                .map(mapper::toDomain)
                .doOnComplete(() -> log.debug("event=tipoprestamo.findByMontoPermitido status=completed monto={}", montoValue))
                .doOnError(error -> log.error("event=tipoprestamo.findByMontoPermitido status=error monto={} error={}", 
                    montoValue, error.getMessage()));
    }
    
    @Override
    public Flux<TipoPrestamo> findByValidacionAutomatica(boolean requiereValidacion) {
        log.debug("event=tipoprestamo.findByValidacionAutomatica action=search requiereValidacion={}", requiereValidacion);
        
        return repository.findByValidacionAutomatica(requiereValidacion)
                .map(mapper::toDomain)
                .doOnComplete(() -> log.debug("event=tipoprestamo.findByValidacionAutomatica status=completed requiereValidacion={}", requiereValidacion))
                .doOnError(error -> log.error("event=tipoprestamo.findByValidacionAutomatica status=error requiereValidacion={} error={}", 
                    requiereValidacion, error.getMessage()));
    }
    
    @Override
    public Flux<TipoPrestamo> findByRangoTasaInteres(double tasaMinima, double tasaMaxima) {
        if (tasaMinima > tasaMaxima) {
            return Flux.error(new IllegalArgumentException("La tasa mínima no puede ser mayor que la máxima"));
        }
        
        BigDecimal tasaMinimaBD = BigDecimal.valueOf(tasaMinima);
        BigDecimal tasaMaximaBD = BigDecimal.valueOf(tasaMaxima);
        log.debug("event=tipoprestamo.findByRangoTasaInteres action=search tasaMinima={} tasaMaxima={}", tasaMinima, tasaMaxima);
        
        return repository.findByRangoTasaInteres(tasaMinimaBD, tasaMaximaBD)
                .map(mapper::toDomain)
                .doOnComplete(() -> log.debug("event=tipoprestamo.findByRangoTasaInteres status=completed tasaMinima={} tasaMaxima={}", tasaMinima, tasaMaxima))
                .doOnError(error -> log.error("event=tipoprestamo.findByRangoTasaInteres status=error tasaMinima={} tasaMaxima={} error={}", 
                    tasaMinima, tasaMaxima, error.getMessage()));
    }
    
    @Override
    public Flux<TipoPrestamo> findAll() {
        log.debug("event=tipoprestamo.findAll action=search");
        
        return repository.findActivos()
                .map(mapper::toDomain)
                .doOnComplete(() -> log.debug("event=tipoprestamo.findAll status=completed"))
                .doOnError(error -> log.error("event=tipoprestamo.findAll status=error error={}", error.getMessage()));
    }
    
    @Override
    public Flux<TipoPrestamo> findAllOrderedBy(String ordenCriterio, boolean ascendente) {
        if (ordenCriterio == null || ordenCriterio.isBlank()) {
            return Flux.error(new IllegalArgumentException("Criterio de orden no puede ser null o vacío"));
        }
        
        log.debug("event=tipoprestamo.findAllOrderedBy action=search ordenCriterio={} ascendente={}", ordenCriterio, ascendente);
        
        return repository.findAllOrderedBy(ordenCriterio, ascendente)
                .map(mapper::toDomain)
                .doOnComplete(() -> log.debug("event=tipoprestamo.findAllOrderedBy status=completed ordenCriterio={}", ordenCriterio))
                .doOnError(error -> log.error("event=tipoprestamo.findAllOrderedBy status=error ordenCriterio={} error={}", 
                    ordenCriterio, error.getMessage()));
    }
    
    @Override
    public Flux<TipoPrestamo> findAllPaginated(int pagina, int tamanoPagina) {
        if (pagina < 0) {
            return Flux.error(new IllegalArgumentException("Página no puede ser negativa"));
        }
        if (tamanoPagina <= 0) {
            return Flux.error(new IllegalArgumentException("Tamaño de página debe ser positivo"));
        }
        
        int offset = pagina * tamanoPagina;
        log.debug("event=tipoprestamo.findAllPaginated action=search pagina={} tamanoPagina={} offset={}", 
            pagina, tamanoPagina, offset);
        
        return repository.findAllPaginated(tamanoPagina, offset)
                .map(mapper::toDomain)
                .doOnComplete(() -> log.debug("event=tipoprestamo.findAllPaginated status=completed pagina={}", pagina))
                .doOnError(error -> log.error("event=tipoprestamo.findAllPaginated status=error pagina={} error={}", 
                    pagina, error.getMessage()));
    }
    
    @Override
    public Mono<Boolean> existsById(TipoPrestamoId idTipoPrestamo) {
        if (idTipoPrestamo == null) {
            return Mono.error(new IllegalArgumentException("ID de tipo de préstamo no puede ser null"));
        }
        
        UUID uuid = mapper.toUUID(idTipoPrestamo);
        log.debug("event=tipoprestamo.existsById action=check tipoPrestamoId={}", uuid);
        
        return repository.existsById(uuid)
                .doOnSuccess(exists -> log.debug("event=tipoprestamo.existsById status=completed tipoPrestamoId={} exists={}", uuid, exists))
                .doOnError(error -> log.error("event=tipoprestamo.existsById status=error tipoPrestamoId={} error={}", 
                    uuid, error.getMessage()));
    }
    
    @Override
    public Mono<Boolean> existsByNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return Mono.error(new IllegalArgumentException("Nombre no puede ser null o vacío"));
        }
        
        log.debug("event=tipoprestamo.existsByNombre action=check nombre={}", nombre);
        
        return repository.existsByNombre(nombre)
                .doOnSuccess(exists -> log.debug("event=tipoprestamo.existsByNombre status=completed nombre={} exists={}", nombre, exists))
                .doOnError(error -> log.error("event=tipoprestamo.existsByNombre status=error nombre={} error={}", 
                    nombre, error.getMessage()));
    }
    
    @Override
    public Mono<Boolean> existsByMontoPermitido(Monto monto) {
        if (monto == null) {
            return Mono.error(new IllegalArgumentException("Monto no puede ser null"));
        }
        
        BigDecimal montoValue = mapper.toBigDecimal(monto);
        log.debug("event=tipoprestamo.existsByMontoPermitido action=check monto={}", montoValue);
        
        return repository.existsByMontoPermitido(montoValue)
                .doOnSuccess(exists -> log.debug("event=tipoprestamo.existsByMontoPermitido status=completed monto={} exists={}", montoValue, exists))
                .doOnError(error -> log.error("event=tipoprestamo.existsByMontoPermitido status=error monto={} error={}", 
                    montoValue, error.getMessage()));
    }
    
    @Override
    public Mono<Boolean> deleteById(TipoPrestamoId idTipoPrestamo) {
        if (idTipoPrestamo == null) {
            return Mono.error(new IllegalArgumentException("ID de tipo de préstamo no puede ser null"));
        }
        
        UUID uuid = mapper.toUUID(idTipoPrestamo);
        log.info("event=tipoprestamo.deleteById action=delete tipoPrestamoId={}", uuid);
        
        return repository.softDeleteById(uuid)
                .then(Mono.just(true))
                .doOnSuccess(deleted -> log.info("event=tipoprestamo.deleteById status=success tipoPrestamoId={}", uuid))
                .doOnError(error -> log.error("event=tipoprestamo.deleteById status=error tipoPrestamoId={} error={}", 
                    uuid, error.getMessage()));
    }
    
    @Override
    public Mono<Long> count() {
        log.debug("event=tipoprestamo.count action=count");
        
        return repository.countActivos()
                .doOnSuccess(count -> log.debug("event=tipoprestamo.count status=completed count={}", count))
                .doOnError(error -> log.error("event=tipoprestamo.count status=error error={}", error.getMessage()));
    }
    
    @Override
    public Mono<Long> countByValidacionAutomatica(boolean requiereValidacion) {
        log.debug("event=tipoprestamo.countByValidacionAutomatica action=count requiereValidacion={}", requiereValidacion);
        
        return repository.countByValidacionAutomatica(requiereValidacion)
                .doOnSuccess(count -> log.debug("event=tipoprestamo.countByValidacionAutomatica status=completed requiereValidacion={} count={}", requiereValidacion, count))
                .doOnError(error -> log.error("event=tipoprestamo.countByValidacionAutomatica status=error requiereValidacion={} error={}", 
                    requiereValidacion, error.getMessage()));
    }
    
    @Override
    public Flux<TipoPrestamo> findByCriterios(String nombreParcial, Boolean requiereValidacionAutomatica,
                                            Monto montoMinimo, Monto montoMaximo) {
        log.debug("event=tipoprestamo.findByCriterios action=search nombreParcial={} requiereValidacionAutomatica={} montoMinimo={} montoMaximo={}", 
            nombreParcial, requiereValidacionAutomatica, montoMinimo, montoMaximo);
        
        BigDecimal montoMinimoBD = montoMinimo != null ? mapper.toBigDecimal(montoMinimo) : null;
        BigDecimal montoMaximoBD = montoMaximo != null ? mapper.toBigDecimal(montoMaximo) : null;
        
        return repository.findByCriterios(nombreParcial, requiereValidacionAutomatica, montoMinimoBD, montoMaximoBD)
                .map(mapper::toDomain)
                .doOnComplete(() -> log.debug("event=tipoprestamo.findByCriterios status=completed"))
                .doOnError(error -> log.error("event=tipoprestamo.findByCriterios status=error error={}", error.getMessage()));
    }
    
    @Override
    public Flux<TipoPrestamo> findMasPopulares(int limite) {
        if (limite <= 0) {
            return Flux.error(new IllegalArgumentException("Límite debe ser positivo"));
        }
        
        log.debug("event=tipoprestamo.findMasPopulares action=search limite={}", limite);
        
        return repository.findMasPopulares(limite)
                .map(mapper::toDomain)
                .doOnComplete(() -> log.debug("event=tipoprestamo.findMasPopulares status=completed limite={}", limite))
                .doOnError(error -> log.error("event=tipoprestamo.findMasPopulares status=error limite={} error={}", 
                    limite, error.getMessage()));
    }
    
    @Override
    public Flux<TipoPrestamo> findActivos() {
        log.debug("event=tipoprestamo.findActivos action=search");
        
        return repository.findActivos()
                .map(mapper::toDomain)
                .doOnComplete(() -> log.debug("event=tipoprestamo.findActivos status=completed"))
                .doOnError(error -> log.error("event=tipoprestamo.findActivos status=error error={}", error.getMessage()));
    }
}
