package co.com.crediya.solicitudes.model.solicitud;

import lombok.*;

/**
 * Entidad que representa una solicitud de préstamo.
 * Aggregate Root que encapsula la lógica de negocio relacionada con solicitudes.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public final class Solicitud {
    
    private Long idSolicitud;
    private Double monto;
    private Double plazo;
    private String email;
    private Long idEstado;
    private Long idTipoPrestamo;

}
