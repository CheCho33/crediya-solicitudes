package co.com.crediya.solicitudes.r2dbc.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Modelo de datos para la persistencia de la entidad Estados.
 * Representa la estructura de la tabla en la base de datos PostgreSQL.
 * 
 * Este modelo sigue las reglas de adaptadores secundarios:
 * - Solo contiene datos de persistencia
 * - No contiene lógica de negocio
 * - Usa anotaciones de Spring Data R2DBC
 */
@Table("estados")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EstadoEntity{
    @Id
    @Column("id_estado")
    Long idEstado;
    
    @Column("nombre")
    String nombre;
    
    @Column("descripcion")
    String descripcion;

}
