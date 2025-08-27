package co.com.crediya.solicitudes.model.tipoprestamo;

import lombok.*;

/**
 * Entidad que representa un tipo de préstamo en el sistema CrediYa.
 * Aggregate Root que encapsula las reglas de negocio para los tipos de préstamo.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public final class TipoPrestamo {
    
    private Long idTipoPrestamo;
    private String nombre;
    private Double montoMinimo;
    private Double montoMaximo;
    private Double tasaInteres;
    private Boolean validacionAutomatica;

}
