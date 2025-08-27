package co.com.crediya.solicitudes.model.estados;

import lombok.*;

/**
 * Entidad que representa un estado en el sistema de solicitudes.
 * Aggregate Root que encapsula la información de un estado con sus invariantes.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public final class Estados {
    
    private  Long idEstado;
    private  String nombre;
    private  String descripcion;
}
