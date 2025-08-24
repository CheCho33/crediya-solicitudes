package co.com.crediya.solicitudes.r2dbc.adapters;

import co.com.crediya.solicitudes.model.estados.EstadoId;
import co.com.crediya.solicitudes.model.estados.Estados;
import co.com.crediya.solicitudes.model.estados.gateways.EstadosRepository;
import co.com.crediya.solicitudes.r2dbc.mapper.EstadosInfraMapper;
import co.com.crediya.solicitudes.r2dbc.model.EstadosData;
import co.com.crediya.solicitudes.r2dbc.repository.EstadosReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Adaptador R2DBC para el gateway EstadosRepository.
 * Implementa las operaciones de persistencia para la entidad Estados.
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
public class EstadosRepositoryAdapter implements EstadosRepository {
    
    private final EstadosReactiveRepository repository;
    private final EstadosInfraMapper mapper;
    
    @Override
    public Mono<Estados> save(Estados estado) {
        if (estado == null) {
            return Mono.error(new IllegalArgumentException("Estado no puede ser null"));
        }
        
        log.info("event=estados.save action=create estadoId={}", estado.idEstado().value());
        
        EstadosData estadosData = mapper.toData(estado);
        return repository.save(estadosData)
                .map(mapper::toDomain)
                .doOnSuccess(saved -> log.info("event=estados.save status=success estadoId={}", saved.idEstado().value()))
                .doOnError(error -> log.error("event=estados.save status=error estadoId={} error={}", 
                    estado.idEstado().value(), error.getMessage()));
    }
    
    @Override
    public Mono<Estados> update(Estados estado) {
        if (estado == null) {
            return Mono.error(new IllegalArgumentException("Estado no puede ser null"));
        }
        
        log.info("event=estados.update action=update estadoId={} version={}", 
            estado.idEstado().value(), estado.version());
        
        EstadosData estadosData = mapper.toData(estado).withIncrementedVersion();
        return repository.save(estadosData)
                .map(mapper::toDomain)
                .doOnSuccess(updated -> log.info("event=estados.update status=success estadoId={} version={}", 
                    updated.idEstado().value(), updated.version()))
                .doOnError(error -> log.error("event=estados.update status=error estadoId={} error={}", 
                    estado.idEstado().value(), error.getMessage()));
    }
    
    @Override
    public Mono<Estados> findById(EstadoId idEstado) {
        if (idEstado == null) {
            return Mono.error(new IllegalArgumentException("ID de estado no puede ser null"));
        }
        
        UUID uuid = mapper.toUUID(idEstado);
        log.debug("event=estados.findById action=search estadoId={}", uuid);
        
        return repository.findById(uuid)
                .map(mapper::toDomain)
                .doOnSuccess(found -> {
                    if (found != null) {
                        log.debug("event=estados.findById status=found estadoId={}", uuid);
                    } else {
                        log.debug("event=estados.findById status=not_found estadoId={}", uuid);
                    }
                })
                .doOnError(error -> log.error("event=estados.findById status=error estadoId={} error={}", 
                    uuid, error.getMessage()));
    }
    
    @Override
    public Mono<Estados> findByNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return Mono.error(new IllegalArgumentException("Nombre no puede ser null o vacío"));
        }
        
        log.debug("event=estados.findByNombre action=search nombre={}", nombre);
        
        return repository.findByNombre(nombre)
                .map(mapper::toDomain)
                .doOnSuccess(found -> {
                    if (found != null) {
                        log.debug("event=estados.findByNombre status=found nombre={}", nombre);
                    } else {
                        log.debug("event=estados.findByNombre status=not_found nombre={}", nombre);
                    }
                })
                .doOnError(error -> log.error("event=estados.findByNombre status=error nombre={} error={}", 
                    nombre, error.getMessage()));
    }
    
    @Override
    public Flux<Estados> findByNombreContaining(String nombreParcial) {
        if (nombreParcial == null) {
            return Flux.error(new IllegalArgumentException("Nombre parcial no puede ser null"));
        }
        
        log.debug("event=estados.findByNombreContaining action=search nombreParcial={}", nombreParcial);
        
        return repository.findByNombreContaining(nombreParcial)
                .map(mapper::toDomain)
                .doOnComplete(() -> log.debug("event=estados.findByNombreContaining status=completed nombreParcial={}", nombreParcial))
                .doOnError(error -> log.error("event=estados.findByNombreContaining status=error nombreParcial={} error={}", 
                    nombreParcial, error.getMessage()));
    }
    
    @Override
    public Flux<Estados> findByDescripcionContaining(String descripcionParcial) {
        if (descripcionParcial == null) {
            return Flux.error(new IllegalArgumentException("Descripción parcial no puede ser null"));
        }
        
        log.debug("event=estados.findByDescripcionContaining action=search descripcionParcial={}", descripcionParcial);
        
        return repository.findByDescripcionContaining(descripcionParcial)
                .map(mapper::toDomain)
                .doOnComplete(() -> log.debug("event=estados.findByDescripcionContaining status=completed descripcionParcial={}", descripcionParcial))
                .doOnError(error -> log.error("event=estados.findByDescripcionContaining status=error descripcionParcial={} error={}", 
                    descripcionParcial, error.getMessage()));
    }
    
    @Override
    public Flux<Estados> findAll() {
        log.debug("event=estados.findAll action=search");
        
        return repository.findActivos()
                .map(mapper::toDomain)
                .doOnComplete(() -> log.debug("event=estados.findAll status=completed"))
                .doOnError(error -> log.error("event=estados.findAll status=error error={}", error.getMessage()));
    }
    
    @Override
    public Flux<Estados> findAllOrderedBy(String ordenCriterio, boolean ascendente) {
        if (ordenCriterio == null || ordenCriterio.isBlank()) {
            return Flux.error(new IllegalArgumentException("Criterio de orden no puede ser null o vacío"));
        }
        
        log.debug("event=estados.findAllOrderedBy action=search ordenCriterio={} ascendente={}", ordenCriterio, ascendente);
        
        return repository.findAllOrderedBy(ordenCriterio, ascendente)
                .map(mapper::toDomain)
                .doOnComplete(() -> log.debug("event=estados.findAllOrderedBy status=completed ordenCriterio={}", ordenCriterio))
                .doOnError(error -> log.error("event=estados.findAllOrderedBy status=error ordenCriterio={} error={}", 
                    ordenCriterio, error.getMessage()));
    }
    
    @Override
    public Flux<Estados> findAllPaginated(int pagina, int tamanoPagina) {
        if (pagina < 0) {
            return Flux.error(new IllegalArgumentException("Página no puede ser negativa"));
        }
        if (tamanoPagina <= 0) {
            return Flux.error(new IllegalArgumentException("Tamaño de página debe ser positivo"));
        }
        
        int offset = pagina * tamanoPagina;
        log.debug("event=estados.findAllPaginated action=search pagina={} tamanoPagina={} offset={}", 
            pagina, tamanoPagina, offset);
        
        return repository.findAllPaginated(tamanoPagina, offset)
                .map(mapper::toDomain)
                .doOnComplete(() -> log.debug("event=estados.findAllPaginated status=completed pagina={}", pagina))
                .doOnError(error -> log.error("event=estados.findAllPaginated status=error pagina={} error={}", 
                    pagina, error.getMessage()));
    }
    
    @Override
    public Mono<Boolean> existsById(EstadoId idEstado) {
        if (idEstado == null) {
            return Mono.error(new IllegalArgumentException("ID de estado no puede ser null"));
        }
        
        UUID uuid = mapper.toUUID(idEstado);
        log.debug("event=estados.existsById action=check estadoId={}", uuid);
        
        return repository.existsById(uuid)
                .doOnSuccess(exists -> log.debug("event=estados.existsById status=completed estadoId={} exists={}", uuid, exists))
                .doOnError(error -> log.error("event=estados.existsById status=error estadoId={} error={}", 
                    uuid, error.getMessage()));
    }
    
    @Override
    public Mono<Boolean> existsByNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return Mono.error(new IllegalArgumentException("Nombre no puede ser null o vacío"));
        }
        
        log.debug("event=estados.existsByNombre action=check nombre={}", nombre);
        
        return repository.existsByNombre(nombre)
                .doOnSuccess(exists -> log.debug("event=estados.existsByNombre status=completed nombre={} exists={}", nombre, exists))
                .doOnError(error -> log.error("event=estados.existsByNombre status=error nombre={} error={}", 
                    nombre, error.getMessage()));
    }
    
    @Override
    public Mono<Boolean> deleteById(EstadoId idEstado) {
        if (idEstado == null) {
            return Mono.error(new IllegalArgumentException("ID de estado no puede ser null"));
        }
        
        UUID uuid = mapper.toUUID(idEstado);
        log.info("event=estados.deleteById action=delete estadoId={}", uuid);
        
        return repository.softDeleteById(uuid)
                .then(Mono.just(true))
                .doOnSuccess(deleted -> log.info("event=estados.deleteById status=success estadoId={}", uuid))
                .doOnError(error -> log.error("event=estados.deleteById status=error estadoId={} error={}", 
                    uuid, error.getMessage()));
    }
    
    @Override
    public Mono<Long> count() {
        log.debug("event=estados.count action=count");
        
        return repository.countActivos()
                .doOnSuccess(count -> log.debug("event=estados.count status=completed count={}", count))
                .doOnError(error -> log.error("event=estados.count status=error error={}", error.getMessage()));
    }
    
    @Override
    public Flux<Estados> findByCriterios(String nombreParcial, String descripcionParcial) {
        log.debug("event=estados.findByCriterios action=search nombreParcial={} descripcionParcial={}", 
            nombreParcial, descripcionParcial);
        
        return repository.findByCriterios(nombreParcial, descripcionParcial)
                .map(mapper::toDomain)
                .doOnComplete(() -> log.debug("event=estados.findByCriterios status=completed"))
                .doOnError(error -> log.error("event=estados.findByCriterios status=error error={}", error.getMessage()));
    }
    
    @Override
    public Flux<Estados> findActivos() {
        log.debug("event=estados.findActivos action=search");
        
        return repository.findActivos()
                .map(mapper::toDomain)
                .doOnComplete(() -> log.debug("event=estados.findActivos status=completed"))
                .doOnError(error -> log.error("event=estados.findActivos status=error error={}", error.getMessage()));
    }
    
    @Override
    public Mono<Estados> findByNombreIgnoreCase(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return Mono.error(new IllegalArgumentException("Nombre no puede ser null o vacío"));
        }
        
        log.debug("event=estados.findByNombreIgnoreCase action=search nombre={}", nombre);
        
        return repository.findByNombreIgnoreCase(nombre)
                .map(mapper::toDomain)
                .doOnSuccess(found -> {
                    if (found != null) {
                        log.debug("event=estados.findByNombreIgnoreCase status=found nombre={}", nombre);
                    } else {
                        log.debug("event=estados.findByNombreIgnoreCase status=not_found nombre={}", nombre);
                    }
                })
                .doOnError(error -> log.error("event=estados.findByNombreIgnoreCase status=error nombre={} error={}", 
                    nombre, error.getMessage()));
    }
    
    @Override
    public Flux<Estados> findAllOrderedByFechaCreacion(boolean ascendente) {
        String orden = ascendente ? "ASC" : "DESC";
        log.debug("event=estados.findAllOrderedByFechaCreacion action=search ascendente={}", ascendente);
        
        return repository.findAllOrderedByFechaCreacion(orden)
                .map(mapper::toDomain)
                .doOnComplete(() -> log.debug("event=estados.findAllOrderedByFechaCreacion status=completed ascendente={}", ascendente))
                .doOnError(error -> log.error("event=estados.findAllOrderedByFechaCreacion status=error ascendente={} error={}", 
                    ascendente, error.getMessage()));
    }
}
