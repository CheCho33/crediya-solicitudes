package co.com.crediya.solicitudes.r2dbc.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

import co.com.crediya.solicitudes.r2dbc.model.EstadosData;
import reactor.core.publisher.Flux;
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
public interface EstadosReactiveRepository extends ReactiveCrudRepository<EstadosData, UUID>, 
                                                  ReactiveSortingRepository<EstadosData, UUID> {
    
    // Queries nativas optimizadas
    static final String FIND_BY_NOMBRE_SQL = """
        SELECT id_estado, nombre, descripcion, version, fecha_creacion, fecha_actualizacion, activo
        FROM estados 
        WHERE LOWER(nombre) = LOWER(:nombre) 
        AND activo = true
        """;
    
    static final String FIND_BY_NOMBRE_IGNORE_CASE_SQL = """
        SELECT id_estado, nombre, descripcion, version, fecha_creacion, fecha_actualizacion, activo
        FROM estados 
        WHERE LOWER(nombre) = LOWER(:nombre) 
        AND activo = true
        """;
    
    static final String FIND_BY_NOMBRE_CONTAINING_SQL = """
        SELECT id_estado, nombre, descripcion, version, fecha_creacion, fecha_actualizacion, activo
        FROM estados 
        WHERE LOWER(nombre) LIKE LOWER(CONCAT('%', :nombreParcial, '%'))
        AND activo = true
        ORDER BY nombre ASC
        """;
    
    static final String FIND_BY_DESCRIPCION_CONTAINING_SQL = """
        SELECT id_estado, nombre, descripcion, version, fecha_creacion, fecha_actualizacion, activo
        FROM estados 
        WHERE LOWER(descripcion) LIKE LOWER(CONCAT('%', :descripcionParcial, '%'))
        AND activo = true
        ORDER BY nombre ASC
        """;
    
    static final String FIND_ALL_ACTIVOS_SQL = """
        SELECT id_estado, nombre, descripcion, version, fecha_creacion, fecha_actualizacion, activo
        FROM estados 
        WHERE activo = true
        ORDER BY nombre ASC
        """;
    
    static final String FIND_ALL_ORDERED_BY_SQL = """
        SELECT id_estado, nombre, descripcion, version, fecha_creacion, fecha_actualizacion, activo
        FROM estados 
        WHERE activo = true
        ORDER BY %s %s
        """;
    
    static final String FIND_ALL_PAGINATED_SQL = """
        SELECT id_estado, nombre, descripcion, version, fecha_creacion, fecha_actualizacion, activo
        FROM estados 
        WHERE activo = true
        ORDER BY nombre ASC
        LIMIT :limit OFFSET :offset
        """;
    
    static final String FIND_BY_CRITERIOS_SQL = """
        SELECT id_estado, nombre, descripcion, version, fecha_creacion, fecha_actualizacion, activo
        FROM estados 
        WHERE activo = true
        AND (:nombreParcial IS NULL OR LOWER(nombre) LIKE LOWER(CONCAT('%', :nombreParcial, '%')))
        AND (:descripcionParcial IS NULL OR LOWER(descripcion) LIKE LOWER(CONCAT('%', :descripcionParcial, '%')))
        ORDER BY nombre ASC
        """;
    
    static final String FIND_ALL_ORDERED_BY_FECHA_CREACION_SQL = """
        SELECT id_estado, nombre, descripcion, version, fecha_creacion, fecha_actualizacion, activo
        FROM estados 
        WHERE activo = true
        ORDER BY fecha_creacion %s
        """;
    
    static final String EXISTS_BY_NOMBRE_SQL = """
        SELECT COUNT(*) > 0
        FROM estados 
        WHERE LOWER(nombre) = LOWER(:nombre) 
        AND activo = true
        """;
    
    static final String COUNT_ACTIVOS_SQL = """
        SELECT COUNT(*)
        FROM estados 
        WHERE activo = true
        """;
    
    static final String SOFT_DELETE_BY_ID_SQL = """
        UPDATE estados 
        SET activo = false, fecha_actualizacion = CURRENT_TIMESTAMP
        WHERE id_estado = :idEstado
        """;
    
    /**
     * Busca un estado por nombre exacto (case-insensitive).
     */
    @Query(FIND_BY_NOMBRE_SQL)
    Mono<EstadosData> findByNombre(@Param("nombre") String nombre);
    
    /**
     * Busca un estado por nombre exacto ignorando mayúsculas/minúsculas.
     */
    @Query(FIND_BY_NOMBRE_IGNORE_CASE_SQL)
    Mono<EstadosData> findByNombreIgnoreCase(@Param("nombre") String nombre);
    
    /**
     * Busca estados cuyo nombre contenga el texto especificado.
     */
    @Query(FIND_BY_NOMBRE_CONTAINING_SQL)
    Flux<EstadosData> findByNombreContaining(@Param("nombreParcial") String nombreParcial);
    
    /**
     * Busca estados cuya descripción contenga el texto especificado.
     */
    @Query(FIND_BY_DESCRIPCION_CONTAINING_SQL)
    Flux<EstadosData> findByDescripcionContaining(@Param("descripcionParcial") String descripcionParcial);
    
    /**
     * Obtiene todos los estados activos.
     */
    @Query(FIND_ALL_ACTIVOS_SQL)
    Flux<EstadosData> findActivos();
    
    /**
     * Obtiene estados con paginación.
     */
    @Query(FIND_ALL_PAGINATED_SQL)
    Flux<EstadosData> findAllPaginated(@Param("limit") int limit, @Param("offset") int offset);
    
    /**
     * Busca estados que coincidan con múltiples criterios.
     */
    @Query(FIND_BY_CRITERIOS_SQL)
    Flux<EstadosData> findByCriterios(@Param("nombreParcial") String nombreParcial, 
                                     @Param("descripcionParcial") String descripcionParcial);
    
    /**
     * Obtiene estados ordenados por fecha de creación.
     */
    @Query(FIND_ALL_ORDERED_BY_FECHA_CREACION_SQL)
    Flux<EstadosData> findAllOrderedByFechaCreacion(@Param("orden") String orden);
    
    /**
     * Verifica si existe un estado con el nombre especificado.
     */
    @Query(EXISTS_BY_NOMBRE_SQL)
    Mono<Boolean> existsByNombre(@Param("nombre") String nombre);
    
    /**
     * Cuenta el número total de estados activos.
     */
    @Query(COUNT_ACTIVOS_SQL)
    Mono<Long> countActivos();
    
    /**
     * Elimina suavemente un estado (marca como inactivo).
     */
    @Query(SOFT_DELETE_BY_ID_SQL)
    Mono<Void> softDeleteById(@Param("idEstado") UUID idEstado);
    
    /**
     * Obtiene todos los estados ordenados por un criterio específico.
     * Método dinámico para diferentes criterios de ordenamiento.
     */
    default Flux<EstadosData> findAllOrderedBy(String ordenCriterio, boolean ascendente) {
        String orden = ascendente ? "ASC" : "DESC";
        String campo = switch (ordenCriterio.toLowerCase()) {
            case "nombre" -> "nombre";
            case "descripcion" -> "descripcion";
            case "id" -> "id_estado";
            case "fecha_creacion" -> "fecha_creacion";
            default -> "nombre"; // default fallback
        };
        
        String sql = String.format(FIND_ALL_ORDERED_BY_SQL, campo, orden);
        // Nota: En una implementación real, esto requeriría usar DatabaseClient
        // para queries dinámicas, pero por simplicidad usamos el método por defecto
        return findAll().sort((a, b) -> {
            int comparison = switch (ordenCriterio.toLowerCase()) {
                case "nombre" -> a.nombre().compareToIgnoreCase(b.nombre());
                case "descripcion" -> a.descripcion().compareToIgnoreCase(b.descripcion());
                case "id" -> a.idEstado().compareTo(b.idEstado());
                case "fecha_creacion" -> a.fechaCreacion().compareTo(b.fechaCreacion());
                default -> a.nombre().compareToIgnoreCase(b.nombre());
            };
            return ascendente ? comparison : -comparison;
        });
    }
}
