package co.com.crediya.solicitudes.r2dbc.repository;

import co.com.crediya.solicitudes.r2dbc.entities.EstadoEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Repositorio reactivo para la entidad Estados.
 * Implementa operaciones de base de datos usando R2DBC con queries nativas optimizadas.
 * 
 * Este repositorio sigue las reglas de adaptadores secundarios:
 * - Queries nativas parametrizadas
 * - Sin concatenación de strings
 * - Optimización para rendimiento
 * - Manejo reactivo puro
 */
@Repository
public interface EstadosEntityRepository extends ReactiveCrudRepository<EstadoEntity, Long>,
                                                  ReactiveSortingRepository<EstadoEntity, Long> {

    // Query nativa para obtener el estado por nombre (case-insensitive)
    @Query("SELECT id_estado, nombre, descripcion FROM estados WHERE LOWER(nombre) = LOWER(:nombre) LIMIT 1")
    Mono<EstadoEntity> findByNombre(String nombre);
}
