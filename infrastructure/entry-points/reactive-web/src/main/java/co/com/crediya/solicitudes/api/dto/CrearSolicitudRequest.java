package co.com.crediya.solicitudes.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;

/**
 * DTO para la creación de una nueva solicitud de préstamo.
 * 
 * Este DTO contiene todos los datos necesarios para crear una solicitud:
 * - Monto solicitado (validado para ser positivo)
 * - Plazo en meses (validado para ser positivo)
 * - Email del solicitante (validado con formato de email)
 * - ID del tipo de préstamo (validado para no ser nulo)
 */
@Builder
public record CrearSolicitudRequest(
    
    BigDecimal montoSolicitado,
    
    Integer plazoMeses,
    
    String emailSolicitante,
    
    UUID idTipoPrestamo
) {
    
    /**
     * Constructor que valida que los datos no sean nulos.
     */
    public CrearSolicitudRequest {
        if (montoSolicitado == null) {
            throw new IllegalArgumentException("El monto solicitado no puede ser nulo");
        }
        if (plazoMeses == null) {
            throw new IllegalArgumentException("El plazo en meses no puede ser nulo");
        }
        if (emailSolicitante == null || emailSolicitante.trim().isEmpty()) {
            throw new IllegalArgumentException("El email del solicitante no puede ser nulo o vacío");
        }
        if (idTipoPrestamo == null) {
            throw new IllegalArgumentException("El ID del tipo de préstamo no puede ser nulo");
        }
    }
}
