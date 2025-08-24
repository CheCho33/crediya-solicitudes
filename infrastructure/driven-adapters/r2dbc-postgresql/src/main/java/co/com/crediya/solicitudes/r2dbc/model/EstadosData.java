package co.com.crediya.solicitudes.r2dbc.model;

import java.time.LocalDateTime;
import java.util.UUID;

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
public record EstadosData(
    @Id
    @Column("id_estado")
    UUID idEstado,
    
    @Column("nombre")
    String nombre,
    
    @Column("descripcion")
    String descripcion,
    
    @Column("version")
    Long version,
    
    @Column("fecha_creacion")
    LocalDateTime fechaCreacion,
    
    @Column("fecha_actualizacion")
    LocalDateTime fechaActualizacion,
    
    @Column("activo")
    Boolean activo
) {
    
    /**
     * Constructor con valores por defecto para nuevos registros.
     */
    public EstadosData(UUID idEstado, String nombre, String descripcion) {
        this(
            idEstado,
            nombre,
            descripcion,
            0L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            true
        );
    }
    
    /**
     * Constructor para reconstruir desde persistencia.
     */
    public EstadosData(UUID idEstado, String nombre, String descripcion, Long version, 
                      LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion, Boolean activo) {
        this.idEstado = idEstado;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.version = version;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.activo = activo;
    }
    
    /**
     * Crea una nueva instancia con fecha de actualización actualizada.
     */
    public EstadosData withUpdatedTimestamp() {
        return new EstadosData(
            idEstado,
            nombre,
            descripcion,
            version,
            fechaCreacion,
            LocalDateTime.now(),
            activo
        );
    }
    
    /**
     * Crea una nueva instancia con versión incrementada.
     */
    public EstadosData withIncrementedVersion() {
        return new EstadosData(
            idEstado,
            nombre,
            descripcion,
            version + 1,
            fechaCreacion,
            LocalDateTime.now(),
            activo
        );
    }
    
    /**
     * Crea una nueva instancia marcada como inactiva.
     */
    public EstadosData asInactive() {
        return new EstadosData(
            idEstado,
            nombre,
            descripcion,
            version,
            fechaCreacion,
            LocalDateTime.now(),
            false
        );
    }
}
