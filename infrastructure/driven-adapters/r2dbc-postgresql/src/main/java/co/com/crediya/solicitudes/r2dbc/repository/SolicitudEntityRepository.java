package co.com.crediya.solicitudes.r2dbc.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import co.com.crediya.solicitudes.r2dbc.entities.SolicitudEntity;

/**
 * Repositorio reactivo para la entidad Solicitud.
 * Implementa operaciones de base de datos usando R2DBC con queries nativas optimizadas.
 * 
 * Este repositorio sigue las reglas de adaptadores secundarios:
 * - Queries nativas parametrizadas
 * - Sin concatenación de strings
 * - Optimización para rendimiento
 * - Manejo reactivo puro
 */
@Repository
public interface SolicitudEntityRepository extends ReactiveCrudRepository<SolicitudEntity, Long>,
                                                   ReactiveSortingRepository<SolicitudEntity, Long> {
    
    // Query nativa para obtener solicitudes por estado
    @Query("SELECT id_solicitud, monto, plazo, email, id_estado, id_tipo_prestamo FROM solicitudes WHERE id_estado = :idEstado")
    Flux<SolicitudEntity> findByIdEstado(Long idEstado);
}
