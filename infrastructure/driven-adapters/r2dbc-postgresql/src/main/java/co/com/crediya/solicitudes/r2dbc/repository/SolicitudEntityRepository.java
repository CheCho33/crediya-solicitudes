package co.com.crediya.solicitudes.r2dbc.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

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
    // Solo las operaciones básicas son necesarias para crear la solicitud (save/findById)
}
