package co.com.crediya.solicitudes.r2dbc.repository;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

import co.com.crediya.solicitudes.r2dbc.model.TipoPrestamoData;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
public interface TipoPrestamoReactiveRepository extends ReactiveCrudRepository<TipoPrestamoData, UUID>, 
                                                      ReactiveSortingRepository<TipoPrestamoData, UUID> {
    
    // Queries nativas optimizadas
    static final String FIND_BY_NOMBRE_SQL = """
        SELECT id_tipo_prestamo, nombre, monto_minimo, monto_maximo, tasa_interes_anual, 
               validacion_automatica, version, fecha_creacion, fecha_actualizacion, activo
        FROM tipos_prestamo 
        WHERE LOWER(nombre) = LOWER(:nombre) 
        AND activo = true
        """;
    
    static final String FIND_BY_NOMBRE_CONTAINING_SQL = """
        SELECT id_tipo_prestamo, nombre, monto_minimo, monto_maximo, tasa_interes_anual, 
               validacion_automatica, version, fecha_creacion, fecha_actualizacion, activo
        FROM tipos_prestamo 
        WHERE LOWER(nombre) LIKE LOWER(CONCAT('%', :nombreParcial, '%'))
        AND activo = true
        ORDER BY nombre ASC
        """;
    
    static final String FIND_BY_MONTO_PERMITIDO_SQL = """
        SELECT id_tipo_prestamo, nombre, monto_minimo, monto_maximo, tasa_interes_anual, 
               validacion_automatica, version, fecha_creacion, fecha_actualizacion, activo
        FROM tipos_prestamo 
        WHERE :monto >= monto_minimo AND :monto <= monto_maximo
        AND activo = true
        ORDER BY nombre ASC
        """;
    
    static final String FIND_BY_VALIDACION_AUTOMATICA_SQL = """
        SELECT id_tipo_prestamo, nombre, monto_minimo, monto_maximo, tasa_interes_anual, 
               validacion_automatica, version, fecha_creacion, fecha_actualizacion, activo
        FROM tipos_prestamo 
        WHERE validacion_automatica = :requiereValidacion
        AND activo = true
        ORDER BY nombre ASC
        """;
    
    static final String FIND_BY_RANGO_TASA_INTERES_SQL = """
        SELECT id_tipo_prestamo, nombre, monto_minimo, monto_maximo, tasa_interes_anual, 
               validacion_automatica, version, fecha_creacion, fecha_actualizacion, activo
        FROM tipos_prestamo 
        WHERE tasa_interes_anual >= :tasaMinima AND tasa_interes_anual <= :tasaMaxima
        AND activo = true
        ORDER BY tasa_interes_anual ASC
        """;
    
    static final String FIND_ALL_ACTIVOS_SQL = """
        SELECT id_tipo_prestamo, nombre, monto_minimo, monto_maximo, tasa_interes_anual, 
               validacion_automatica, version, fecha_creacion, fecha_actualizacion, activo
        FROM tipos_prestamo 
        WHERE activo = true
        ORDER BY nombre ASC
        """;
    
    static final String FIND_ALL_PAGINATED_SQL = """
        SELECT id_tipo_prestamo, nombre, monto_minimo, monto_maximo, tasa_interes_anual, 
               validacion_automatica, version, fecha_creacion, fecha_actualizacion, activo
        FROM tipos_prestamo 
        WHERE activo = true
        ORDER BY nombre ASC
        LIMIT :limit OFFSET :offset
        """;
    
    static final String FIND_BY_CRITERIOS_SQL = """
        SELECT id_tipo_prestamo, nombre, monto_minimo, monto_maximo, tasa_interes_anual, 
               validacion_automatica, version, fecha_creacion, fecha_actualizacion, activo
        FROM tipos_prestamo 
        WHERE activo = true
        AND (:nombreParcial IS NULL OR LOWER(nombre) LIKE LOWER(CONCAT('%', :nombreParcial, '%')))
        AND (:requiereValidacionAutomatica IS NULL OR validacion_automatica = :requiereValidacionAutomatica)
        AND (:montoMinimo IS NULL OR monto_maximo >= :montoMinimo)
        AND (:montoMaximo IS NULL OR monto_minimo <= :montoMaximo)
        ORDER BY nombre ASC
        """;
    
    static final String FIND_MAS_POPULARES_SQL = """
        SELECT tp.id_tipo_prestamo, tp.nombre, tp.monto_minimo, tp.monto_maximo, tp.tasa_interes_anual, 
               tp.validacion_automatica, tp.version, tp.fecha_creacion, tp.fecha_actualizacion, tp.activo
        FROM tipos_prestamo tp
        LEFT JOIN (
            SELECT tipo_prestamo_id, COUNT(*) as solicitudes_count
            FROM solicitudes
            WHERE activo = true
            GROUP BY tipo_prestamo_id
        ) s ON tp.id_tipo_prestamo = s.tipo_prestamo_id
        WHERE tp.activo = true
        ORDER BY COALESCE(s.solicitudes_count, 0) DESC, tp.nombre ASC
        LIMIT :limite
        """;
    
    static final String EXISTS_BY_NOMBRE_SQL = """
        SELECT COUNT(*) > 0
        FROM tipos_prestamo 
        WHERE LOWER(nombre) = LOWER(:nombre) 
        AND activo = true
        """;
    
    static final String EXISTS_BY_MONTO_PERMITIDO_SQL = """
        SELECT COUNT(*) > 0
        FROM tipos_prestamo 
        WHERE :monto >= monto_minimo AND :monto <= monto_maximo
        AND activo = true
        """;
    
    static final String COUNT_ACTIVOS_SQL = """
        SELECT COUNT(*)
        FROM tipos_prestamo 
        WHERE activo = true
        """;
    
    static final String COUNT_BY_VALIDACION_AUTOMATICA_SQL = """
        SELECT COUNT(*)
        FROM tipos_prestamo 
        WHERE validacion_automatica = :requiereValidacion
        AND activo = true
        """;
    
    static final String SOFT_DELETE_BY_ID_SQL = """
        UPDATE tipos_prestamo 
        SET activo = false, fecha_actualizacion = CURRENT_TIMESTAMP
        WHERE id_tipo_prestamo = :idTipoPrestamo
        """;
    
    /**
     * Busca un tipo de préstamo por nombre exacto (case-insensitive).
     */
    @Query(FIND_BY_NOMBRE_SQL)
    Mono<TipoPrestamoData> findByNombre(@Param("nombre") String nombre);
    
    /**
     * Busca tipos de préstamo cuyo nombre contenga el texto especificado.
     */
    @Query(FIND_BY_NOMBRE_CONTAINING_SQL)
    Flux<TipoPrestamoData> findByNombreContaining(@Param("nombreParcial") String nombreParcial);
    
    /**
     * Busca tipos de préstamo que permitan un monto específico.
     */
    @Query(FIND_BY_MONTO_PERMITIDO_SQL)
    Flux<TipoPrestamoData> findByMontoPermitido(@Param("monto") BigDecimal monto);
    
    /**
     * Busca tipos de préstamo que requieran validación automática.
     */
    @Query(FIND_BY_VALIDACION_AUTOMATICA_SQL)
    Flux<TipoPrestamoData> findByValidacionAutomatica(@Param("requiereValidacion") boolean requiereValidacion);
    
    /**
     * Busca tipos de préstamo con tasa de interés dentro de un rango específico.
     */
    @Query(FIND_BY_RANGO_TASA_INTERES_SQL)
    Flux<TipoPrestamoData> findByRangoTasaInteres(@Param("tasaMinima") BigDecimal tasaMinima, 
                                                  @Param("tasaMaxima") BigDecimal tasaMaxima);
    
    /**
     * Obtiene todos los tipos de préstamo activos.
     */
    @Query(FIND_ALL_ACTIVOS_SQL)
    Flux<TipoPrestamoData> findActivos();
    
    /**
     * Obtiene tipos de préstamo con paginación.
     */
    @Query(FIND_ALL_PAGINATED_SQL)
    Flux<TipoPrestamoData> findAllPaginated(@Param("limit") int limit, @Param("offset") int offset);
    
    /**
     * Busca tipos de préstamo que coincidan con múltiples criterios.
     */
    @Query(FIND_BY_CRITERIOS_SQL)
    Flux<TipoPrestamoData> findByCriterios(@Param("nombreParcial") String nombreParcial, 
                                          @Param("requiereValidacionAutomatica") Boolean requiereValidacionAutomatica,
                                          @Param("montoMinimo") BigDecimal montoMinimo, 
                                          @Param("montoMaximo") BigDecimal montoMaximo);
    
    /**
     * Obtiene los tipos de préstamo más populares basado en el número de solicitudes.
     */
    @Query(FIND_MAS_POPULARES_SQL)
    Flux<TipoPrestamoData> findMasPopulares(@Param("limite") int limite);
    
    /**
     * Verifica si existe un tipo de préstamo con el nombre especificado.
     */
    @Query(EXISTS_BY_NOMBRE_SQL)
    Mono<Boolean> existsByNombre(@Param("nombre") String nombre);
    
    /**
     * Verifica si existe algún tipo de préstamo que permita un monto específico.
     */
    @Query(EXISTS_BY_MONTO_PERMITIDO_SQL)
    Mono<Boolean> existsByMontoPermitido(@Param("monto") BigDecimal monto);
    
    /**
     * Cuenta el número total de tipos de préstamo activos.
     */
    @Query(COUNT_ACTIVOS_SQL)
    Mono<Long> countActivos();
    
    /**
     * Cuenta tipos de préstamo que requieren validación automática.
     */
    @Query(COUNT_BY_VALIDACION_AUTOMATICA_SQL)
    Mono<Long> countByValidacionAutomatica(@Param("requiereValidacion") boolean requiereValidacion);
    
    /**
     * Elimina suavemente un tipo de préstamo (marca como inactivo).
     */
    @Query(SOFT_DELETE_BY_ID_SQL)
    Mono<Void> softDeleteById(@Param("idTipoPrestamo") UUID idTipoPrestamo);
    
    /**
     * Obtiene todos los tipos de préstamo ordenados por un criterio específico.
     * Método dinámico para diferentes criterios de ordenamiento.
     */
    default Flux<TipoPrestamoData> findAllOrderedBy(String ordenCriterio, boolean ascendente) {
        return findAll().sort((a, b) -> {
            int comparison = switch (ordenCriterio.toLowerCase()) {
                case "nombre" -> a.nombre().compareToIgnoreCase(b.nombre());
                case "monto_minimo" -> a.montoMinimo().compareTo(b.montoMinimo());
                case "monto_maximo" -> a.montoMaximo().compareTo(b.montoMaximo());
                case "tasa_interes" -> a.tasaInteresAnual().compareTo(b.tasaInteresAnual());
                case "fecha_creacion" -> a.fechaCreacion().compareTo(b.fechaCreacion());
                default -> a.nombre().compareToIgnoreCase(b.nombre());
            };
            return ascendente ? comparison : -comparison;
        });
    }
}
