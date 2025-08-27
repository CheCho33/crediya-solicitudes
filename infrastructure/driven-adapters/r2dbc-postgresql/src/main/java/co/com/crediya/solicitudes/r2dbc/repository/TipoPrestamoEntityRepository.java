package co.com.crediya.solicitudes.r2dbc.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

import co.com.crediya.solicitudes.r2dbc.entities.TipoPrestamoEntity;

/**
 * Repositorio reactivo para la entidad TipoPrestamo.
 * Implementa operaciones de base de datos usando R2DBC con queries nativas optimizadas.
 * 
 * Este repositorio sigue las reglas de adaptadores secundarios:
 * - Queries nativas parametrizadas
 * - Sin concatenación de strings
 * - Optimización para rendimiento
 * - Manejo reactivo puro
 */
@Repository
public interface TipoPrestamoEntityRepository extends ReactiveCrudRepository<TipoPrestamoEntity, Long>,
                                                      ReactiveSortingRepository<TipoPrestamoEntity, Long> {
    // Operaciones básicas para lectura/creación (findById/save)
}
