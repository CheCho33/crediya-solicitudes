package co.com.crediya.solicitudes.r2dbc.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo de datos para la tabla de solicitudes de préstamo.
 * Representa la estructura de datos en la base de datos PostgreSQL.
 * 
 * Este modelo sigue las reglas de adaptadores secundarios:
 * - Mapeo directo a la estructura de tabla
 * - Anotaciones de Spring Data R2DBC
 * - Inmutabilidad con Lombok
 * - Sin lógica de negocio
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("solicitudes")
public class SolicitudData {
    
    @Id
    @Column("id_solicitud")
    private UUID idSolicitud;
    
    @Column("monto_solicitado")
    private BigDecimal montoSolicitado;
    
    @Column("plazo_meses")
    private Integer plazoMeses;
    
    @Column("email_solicitante")
    private String emailSolicitante;
    
    @Column("id_estado")
    private UUID idEstado;
    
    @Column("id_tipo_prestamo")
    private UUID idTipoPrestamo;
    
    @Column("version")
    private Long version;
    
    @Column("fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @Column("activo")
    private Boolean activo;
}
