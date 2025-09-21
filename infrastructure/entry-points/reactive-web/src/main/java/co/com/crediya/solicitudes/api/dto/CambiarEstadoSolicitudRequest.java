package co.com.crediya.solicitudes.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para la petición de cambio de estado de una solicitud.
 * 
 * Contiene:
 * - Email de la solicitud a cambiar
 * - Nuevo estado para la solicitud
 */
public record CambiarEstadoSolicitudRequest(
    @NotBlank
    @Email
    String email,

    @NotBlank
    String nuevoEstado
) {
}
