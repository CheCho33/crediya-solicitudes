package co.com.crediya.solicitudes.model.solicitud;

import co.com.crediya.solicitudes.model.estados.EstadoId;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamoId;
import co.com.crediya.solicitudes.model.valueobjects.Email;
import co.com.crediya.solicitudes.model.valueobjects.Monto;
import co.com.crediya.solicitudes.model.valueobjects.Plazo;

/**
 * Entidad que representa una solicitud de préstamo.
 * Aggregate Root que encapsula la lógica de negocio relacionada con solicitudes.
 */
public final class Solicitud {
    
    private final SolicitudId id;
    private final Monto monto;
    private final Plazo plazo;
    private final Email email;
    private final EstadoId idEstado;
    private final TipoPrestamoId idTipoPrestamo;
    private long version;
    
    /**
     * Constructor privado para crear una solicitud.
     * 
     * @param id identificador único de la solicitud
     * @param monto monto solicitado
     * @param plazo plazo en meses
     * @param email email del solicitante
     * @param idEstado identificador del estado actual
     * @param idTipoPrestamo identificador del tipo de préstamo
     * @param version versión para control de concurrencia optimista
     */
    private Solicitud(SolicitudId id, Monto monto, Plazo plazo, Email email, 
                     EstadoId idEstado, TipoPrestamoId idTipoPrestamo, long version) {
        this.id = id;
        this.monto = monto;
        this.plazo = plazo;
        this.email = email;
        this.idEstado = idEstado;
        this.idTipoPrestamo = idTipoPrestamo;
        this.version = version;
        validateInvariants();
    }
    
    /**
     * Crea una nueva solicitud de préstamo.
     * 
     * @param id identificador único de la solicitud
     * @param monto monto solicitado
     * @param plazo plazo en meses
     * @param email email del solicitante
     * @param idEstado identificador del estado inicial
     * @param idTipoPrestamo identificador del tipo de préstamo
     * @return nueva instancia de Solicitud
     */
    public static Solicitud create(SolicitudId id, Monto monto, Plazo plazo, Email email,
                                 EstadoId idEstado, TipoPrestamoId idTipoPrestamo) {
        return new Solicitud(id, monto, plazo, email, idEstado, idTipoPrestamo, 0L);
    }
    
    /**
     * Crea una solicitud desde datos existentes (para reconstrucción desde persistencia).
     * 
     * @param id identificador único de la solicitud
     * @param monto monto solicitado
     * @param plazo plazo en meses
     * @param email email del solicitante
     * @param idEstado identificador del estado actual
     * @param idTipoPrestamo identificador del tipo de préstamo
     * @param version versión actual
     * @return instancia de Solicitud
     */
    public static Solicitud from(SolicitudId id, Monto monto, Plazo plazo, Email email,
                               EstadoId idEstado, TipoPrestamoId idTipoPrestamo, long version) {
        return new Solicitud(id, monto, plazo, email, idEstado, idTipoPrestamo, version);
    }
    
    /**
     * Valida que todos los invariantes de la solicitud se cumplan.
     * 
     * @throws IllegalStateException si algún invariante no se cumple
     */
    private void validateInvariants() {
        if (id == null) {
            throw new IllegalStateException("El identificador de solicitud no puede ser nulo");
        }
        if (monto == null) {
            throw new IllegalStateException("El monto no puede ser nulo");
        }
        if (plazo == null) {
            throw new IllegalStateException("El plazo no puede ser nulo");
        }
        if (email == null) {
            throw new IllegalStateException("El email no puede ser nulo");
        }
        if (idEstado == null) {
            throw new IllegalStateException("El identificador de estado no puede ser nulo");
        }
        if (idTipoPrestamo == null) {
            throw new IllegalStateException("El identificador de tipo de préstamo no puede ser nulo");
        }
        if (version < 0) {
            throw new IllegalStateException("La versión no puede ser negativa");
        }
    }
    
    /**
     * Marca la solicitud como persistida con una nueva versión.
     * 
     * @param newVersion nueva versión después de la persistencia
     * @throws IllegalArgumentException si la nueva versión no es mayor que la actual
     */
    public void markPersisted(long newVersion) {
        if (newVersion <= version) {
            throw new IllegalArgumentException("La nueva versión debe ser mayor que la actual");
        }
        this.version = newVersion;
    }
    
    // Getters intencionales (no exponer estado mutable)
    
    public SolicitudId id() {
        return id;
    }
    
    public Monto monto() {
        return monto;
    }
    
    public Plazo plazo() {
        return plazo;
    }
    
    public Email email() {
        return email;
    }
    
    public EstadoId idEstado() {
        return idEstado;
    }
    
    public TipoPrestamoId idTipoPrestamo() {
        return idTipoPrestamo;
    }
    
    public long version() {
        return version;
    }
}
