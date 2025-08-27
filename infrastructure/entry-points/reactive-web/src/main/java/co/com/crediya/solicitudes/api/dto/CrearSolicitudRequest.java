package co.com.crediya.solicitudes.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record CrearSolicitudRequest(
    @NotNull
    Double monto,

    @NotNull
    Double plazo,

    @NotBlank
    @Email
    String email,

    @NotBlank
    Long idTipoPrestamo
) {
    

}
