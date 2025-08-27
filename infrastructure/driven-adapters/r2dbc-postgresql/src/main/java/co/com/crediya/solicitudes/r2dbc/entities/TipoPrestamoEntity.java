package co.com.crediya.solicitudes.r2dbc.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Modelo de datos para la persistencia de la entidad TipoPrestamo.
 * Representa la estructura de la tabla en la base de datos PostgreSQL.
 * 
 * Este modelo sigue las reglas de adaptadores secundarios:
 * - Solo contiene datos de persistencia
 * - No contiene lógica de negocio
 * - Usa anotaciones de Spring Data R2DBC
 */
@Table("tipos_prestamo")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TipoPrestamoEntity{
    @Id
    @Column("id_tipo_prestamo")
    Long idTipoPrestamo;

    @Column("nombre")
    String nombre;

    @Column("monto_minimo")
    Double montoMinimo;

    @Column("monto_maximo")
    Double montoMaximo;

    @Column("tasa_interes")
    Double tasaInteres;

    @Column("validacion_automatica")
    Boolean validacionAutomatica;
}

