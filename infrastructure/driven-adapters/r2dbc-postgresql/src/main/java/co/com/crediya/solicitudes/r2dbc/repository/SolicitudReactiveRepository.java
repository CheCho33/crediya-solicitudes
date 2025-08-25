package co.com.crediya.solicitudes.r2dbc.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

import co.com.crediya.solicitudes.r2dbc.model.SolicitudData;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
public interface SolicitudReactiveRepository extends ReactiveCrudRepository<SolicitudData, UUID>, 
                                                   ReactiveSortingRepository<SolicitudData, UUID> {
    
    // Queries nativas optimizadas
    static final String FIND_BY_EMAIL_SQL = """
        SELECT id_solicitud, monto_solicitado, plazo_meses, email_solicitante, 
               id_estado, id_tipo_prestamo, version, fecha_creacion, fecha_actualizacion, activo
        FROM solicitudes 
        WHERE LOWER(email_solicitante) = LOWER(:email) 
        AND activo = true
        ORDER BY fecha_creacion DESC
        """;
    
    static final String FIND_BY_ESTADO_SQL = """
        SELECT id_solicitud, monto_solicitado, plazo_meses, email_solicitante, 
               id_estado, id_tipo_prestamo, version, fecha_creacion, fecha_actualizacion, activo
        FROM solicitudes 
        WHERE id_estado = :idEstado 
        AND activo = true
        ORDER BY fecha_creacion DESC
        """;
    
    static final String FIND_BY_TIPO_PRESTAMO_SQL = """
        SELECT id_solicitud, monto_solicitado, plazo_meses, email_solicitante, 
               id_estado, id_tipo_prestamo, version, fecha_creacion, fecha_actualizacion, activo
        FROM solicitudes 
        WHERE id_tipo_prestamo = :idTipoPrestamo 
        AND activo = true
        ORDER BY fecha_creacion DESC
        """;
    
    static final String FIND_BY_RANGO_MONTO_SQL = """
        SELECT id_solicitud, monto_solicitado, plazo_meses, email_solicitante, 
               id_estado, id_tipo_prestamo, version, fecha_creacion, fecha_actualizacion, activo
        FROM solicitudes 
        WHERE monto_solicitado >= :montoMinimo AND monto_solicitado <= :montoMaximo
        AND activo = true
        ORDER BY fecha_creacion DESC
        """;
    
    static final String FIND_BY_RANGO_FECHAS_SQL = """
        SELECT id_solicitud, monto_solicitado, plazo_meses, email_solicitante, 
               id_estado, id_tipo_prestamo, version, fecha_creacion, fecha_actualizacion, activo
        FROM solicitudes 
        WHERE fecha_creacion >= :fechaInicio AND fecha_creacion <= :fechaFin
        AND activo = true
        ORDER BY fecha_creacion DESC
        """;
    
    static final String FIND_PENDIENTES_REVISION_SQL = """
        SELECT s.id_solicitud, s.monto_solicitado, s.plazo_meses, s.email_solicitante, 
               s.id_estado, s.id_tipo_prestamo, s.version, s.fecha_creacion, s.fecha_actualizacion, s.activo
        FROM solicitudes s
        INNER JOIN estados e ON s.id_estado = e.id_estado
        WHERE LOWER(e.nombre) = LOWER('Pendiente de revisión')
        AND s.activo = true
        ORDER BY s.fecha_creacion ASC
        """;
    
    static final String FIND_APROBADAS_SQL = """
        SELECT s.id_solicitud, s.monto_solicitado, s.plazo_meses, s.email_solicitante, 
               s.id_estado, s.id_tipo_prestamo, s.version, s.fecha_creacion, s.fecha_actualizacion, s.activo
        FROM solicitudes s
        INNER JOIN estados e ON s.id_estado = e.id_estado
        WHERE LOWER(e.nombre) = LOWER('Aprobada')
        AND s.activo = true
        ORDER BY s.fecha_creacion DESC
        """;
    
    static final String FIND_RECHAZADAS_SQL = """
        SELECT s.id_solicitud, s.monto_solicitado, s.plazo_meses, s.email_solicitante, 
               s.id_estado, s.id_tipo_prestamo, s.version, s.fecha_creacion, s.fecha_actualizacion, s.activo
        FROM solicitudes s
        INNER JOIN estados e ON s.id_estado = e.id_estado
        WHERE LOWER(e.nombre) = LOWER('Rechazada')
        AND s.activo = true
        ORDER BY s.fecha_creacion DESC
        """;
    
    static final String FIND_ALL_ACTIVOS_SQL = """
        SELECT id_solicitud, monto_solicitado, plazo_meses, email_solicitante, 
               id_estado, id_tipo_prestamo, version, fecha_creacion, fecha_actualizacion, activo
        FROM solicitudes 
        WHERE activo = true
        ORDER BY fecha_creacion DESC
        """;
    
    static final String FIND_BY_EMAIL_AND_ESTADO_SQL = """
        SELECT s.id_solicitud, s.monto_solicitado, s.plazo_meses, s.email_solicitante, 
               s.id_estado, s.id_tipo_prestamo, s.version, s.fecha_creacion, s.fecha_actualizacion, s.activo
        FROM solicitudes s
        WHERE LOWER(s.email_solicitante) = LOWER(:email) 
        AND s.id_estado = :idEstado
        AND s.activo = true
        ORDER BY s.fecha_creacion DESC
        """;
    
    static final String FIND_MAS_RECIENTES_SQL = """
        SELECT id_solicitud, monto_solicitado, plazo_meses, email_solicitante, 
               id_estado, id_tipo_prestamo, version, fecha_creacion, fecha_actualizacion, activo
        FROM solicitudes 
        WHERE activo = true
        ORDER BY fecha_creacion DESC
        LIMIT :limite
        """;
    
    static final String FIND_REQUIEREN_SEGUIMIENTO_SQL = """
        SELECT id_solicitud, monto_solicitado, plazo_meses, email_solicitante, 
               id_estado, id_tipo_prestamo, version, fecha_creacion, fecha_actualizacion, activo
        FROM solicitudes 
        WHERE fecha_creacion < (CURRENT_TIMESTAMP - INTERVAL ':diasLimite days')
        AND activo = true
        ORDER BY fecha_creacion ASC
        """;
    
    static final String COUNT_BY_ESTADO_SQL = """
        SELECT COUNT(*)
        FROM solicitudes 
        WHERE id_estado = :idEstado 
        AND activo = true
        """;
    
    static final String COUNT_BY_TIPO_PRESTAMO_SQL = """
        SELECT COUNT(*)
        FROM solicitudes 
        WHERE id_tipo_prestamo = :idTipoPrestamo 
        AND activo = true
        """;
    
    static final String FIND_ESTADISTICAS_POR_ESTADO_SQL = """
        SELECT e.nombre as estado, COUNT(s.id_solicitud) as cantidad
        FROM solicitudes s
        INNER JOIN estados e ON s.id_estado = e.id_estado
        WHERE s.activo = true
        GROUP BY e.id_estado, e.nombre
        ORDER BY cantidad DESC
        """;
    
    static final String FIND_ESTADISTICAS_POR_TIPO_PRESTAMO_SQL = """
        SELECT tp.nombre as tipo_prestamo, COUNT(s.id_solicitud) as cantidad
        FROM solicitudes s
        INNER JOIN tipos_prestamo tp ON s.id_tipo_prestamo = tp.id_tipo_prestamo
        WHERE s.activo = true
        GROUP BY tp.id_tipo_prestamo, tp.nombre
        ORDER BY cantidad DESC
        """;
    
    // Métodos de consulta básicos
    @Query(FIND_BY_EMAIL_SQL)
    Flux<SolicitudData> findByEmailSolicitante(@Param("email") String email);
    
    @Query(FIND_BY_ESTADO_SQL)
    Flux<SolicitudData> findByIdEstado(@Param("idEstado") UUID idEstado);
    
    @Query(FIND_BY_TIPO_PRESTAMO_SQL)
    Flux<SolicitudData> findByIdTipoPrestamo(@Param("idTipoPrestamo") UUID idTipoPrestamo);
    
    @Query(FIND_BY_RANGO_MONTO_SQL)
    Flux<SolicitudData> findByRangoMonto(@Param("montoMinimo") BigDecimal montoMinimo, 
                                        @Param("montoMaximo") BigDecimal montoMaximo);
    
    @Query(FIND_BY_RANGO_FECHAS_SQL)
    Flux<SolicitudData> findByRangoFechas(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                         @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query(FIND_PENDIENTES_REVISION_SQL)
    Flux<SolicitudData> findPendientesRevision();
    
    @Query(FIND_APROBADAS_SQL)
    Flux<SolicitudData> findAprobadas();
    
    @Query(FIND_RECHAZADAS_SQL)
    Flux<SolicitudData> findRechazadas();
    
    @Query(FIND_ALL_ACTIVOS_SQL)
    Flux<SolicitudData> findAllActivos();
    
    @Query(FIND_BY_EMAIL_AND_ESTADO_SQL)
    Flux<SolicitudData> findByEmailSolicitanteAndIdEstado(@Param("email") String email, 
                                                         @Param("idEstado") UUID idEstado);
    
    @Query(FIND_MAS_RECIENTES_SQL)
    Flux<SolicitudData> findMasRecientes(@Param("limite") int limite);
    
    @Query(FIND_REQUIEREN_SEGUIMIENTO_SQL)
    Flux<SolicitudData> findRequierenSeguimiento(@Param("diasLimite") int diasLimite);
    
    @Query(COUNT_BY_ESTADO_SQL)
    Mono<Long> countByIdEstado(@Param("idEstado") UUID idEstado);
    
    @Query(COUNT_BY_TIPO_PRESTAMO_SQL)
    Mono<Long> countByIdTipoPrestamo(@Param("idTipoPrestamo") UUID idTipoPrestamo);
    
    @Query(FIND_ESTADISTICAS_POR_ESTADO_SQL)
    Flux<Object[]> findEstadisticasPorEstado();
    
    @Query(FIND_ESTADISTICAS_POR_TIPO_PRESTAMO_SQL)
    Flux<Object[]> findEstadisticasPorTipoPrestamo();
    
    // Métodos con paginación
    Flux<SolicitudData> findAllByActivoTrue(Pageable pageable);
    
    // Métodos de existencia
    Mono<Boolean> existsByEmailSolicitanteAndActivoTrue(String emailSolicitante);
    
    Mono<Boolean> existsByIdSolicitudAndActivoTrue(UUID idSolicitud);
}
