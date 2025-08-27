package co.com.crediya.solicitudes.r2dbc.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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
public class SolicitudEntity {
    
    @Id
    @Column("id_solicitud")
    private Long idSolicitud;
    
    @Column("monto")
    private Double monto;
    
    @Column("plazo")
    private Double plazo;
    
    @Column("email")
    private String email;
    
    @Column("id_estado")
    private Long idEstado;
    
    @Column("id_tipo_prestamo")
    private Long idTipoPrestamo;

}
