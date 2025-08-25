package co.com.crediya.solicitudes.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;

/**
 * DTO de respuesta para una solicitud de préstamo.
 * 
 * Este DTO contiene todos los datos de la solicitud que se exponen al cliente:
 * - ID único de la solicitud
 * - Monto solicitado
 * - Plazo en meses
 * - Email del solicitante
 * - Estado actual de la solicitud
 * - ID del tipo de préstamo
 * - Fecha de creación
 */
@Builder
public record SolicitudResponse(
    
    UUID id,
    
    BigDecimal montoSolicitado,
    
    Integer plazoMeses,
    
    String emailSolicitante,
    
    String estadoSolicitud,
    
    UUID idTipoPrestamo,
    
    LocalDateTime fechaCreacion
) {
    
    /**
     * Constructor que valida que los datos no sean nulos.
     */
    public SolicitudResponse {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la solicitud no puede ser nulo");
        }
        if (montoSolicitado == null) {
            throw new IllegalArgumentException("El monto solicitado no puede ser nulo");
        }
        if (plazoMeses == null) {
            throw new IllegalArgumentException("El plazo en meses no puede ser nulo");
        }
        if (emailSolicitante == null || emailSolicitante.trim().isEmpty()) {
            throw new IllegalArgumentException("El email del solicitante no puede ser nulo o vacío");
        }
        if (estadoSolicitud == null || estadoSolicitud.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado de la solicitud no puede ser nulo o vacío");
        }
        if (idTipoPrestamo == null) {
            throw new IllegalArgumentException("El ID del tipo de préstamo no puede ser nulo");
        }
        if (fechaCreacion == null) {
            throw new IllegalArgumentException("La fecha de creación no puede ser nula");
        }
    }
}
