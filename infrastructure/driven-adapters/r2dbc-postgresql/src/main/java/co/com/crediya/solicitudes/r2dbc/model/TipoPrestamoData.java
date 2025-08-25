package co.com.crediya.solicitudes.r2dbc.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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
public record TipoPrestamoData(
    @Id
    @Column("id_tipo_prestamo")
    UUID idTipoPrestamo,
    
    @Column("nombre")
    String nombre,
    
    @Column("monto_minimo")
    BigDecimal montoMinimo,
    
    @Column("monto_maximo")
    BigDecimal montoMaximo,
    
    @Column("tasa_interes_anual")
    BigDecimal tasaInteresAnual,
    
    @Column("validacion_automatica")
    Boolean validacionAutomatica,
    
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
    public TipoPrestamoData(UUID idTipoPrestamo, String nombre, BigDecimal montoMinimo, 
                           BigDecimal montoMaximo, BigDecimal tasaInteresAnual, 
                           Boolean validacionAutomatica) {
        this(
            idTipoPrestamo,
            nombre,
            montoMinimo,
            montoMaximo,
            tasaInteresAnual,
            validacionAutomatica,
            0L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            true
        );
    }
    
    /**
     * Constructor para reconstruir desde persistencia.
     */
    public TipoPrestamoData(UUID idTipoPrestamo, String nombre, BigDecimal montoMinimo, 
                           BigDecimal montoMaximo, BigDecimal tasaInteresAnual, 
                           Boolean validacionAutomatica, Long version, 
                           LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion, 
                           Boolean activo) {
        this.idTipoPrestamo = idTipoPrestamo;
        this.nombre = nombre;
        this.montoMinimo = montoMinimo;
        this.montoMaximo = montoMaximo;
        this.tasaInteresAnual = tasaInteresAnual;
        this.validacionAutomatica = validacionAutomatica;
        this.version = version;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.activo = activo;
    }
    
    /**
     * Crea una nueva instancia con fecha de actualización actualizada.
     */
    public TipoPrestamoData withUpdatedTimestamp() {
        return new TipoPrestamoData(
            idTipoPrestamo,
            nombre,
            montoMinimo,
            montoMaximo,
            tasaInteresAnual,
            validacionAutomatica,
            version,
            fechaCreacion,
            LocalDateTime.now(),
            activo
        );
    }
    
    /**
     * Crea una nueva instancia con versión incrementada.
     */
    public TipoPrestamoData withIncrementedVersion() {
        return new TipoPrestamoData(
            idTipoPrestamo,
            nombre,
            montoMinimo,
            montoMaximo,
            tasaInteresAnual,
            validacionAutomatica,
            version + 1,
            fechaCreacion,
            LocalDateTime.now(),
            activo
        );
    }
    
    /**
     * Crea una nueva instancia marcada como inactiva.
     */
    public TipoPrestamoData asInactive() {
        return new TipoPrestamoData(
            idTipoPrestamo,
            nombre,
            montoMinimo,
            montoMaximo,
            tasaInteresAnual,
            validacionAutomatica,
            version,
            fechaCreacion,
            LocalDateTime.now(),
            false
        );
    }
    
    /**
     * Verifica si un monto está dentro del rango permitido.
     * Método de conveniencia para queries de base de datos.
     */
    public boolean montoEnRango(BigDecimal monto) {
        if (monto == null) return false;
        return monto.compareTo(montoMinimo) >= 0 && monto.compareTo(montoMaximo) <= 0;
    }
}
