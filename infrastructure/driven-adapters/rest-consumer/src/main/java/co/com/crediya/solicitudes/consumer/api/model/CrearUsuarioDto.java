package co.com.crediya.solicitudes.consumer.api.model;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
* CrearUsuarioDto
*/

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrearUsuarioDto {
    private String nombre = null;
    private String apellido = null;
    private String email = null;
    private String documentoIdentidad = null;
    private String telefono = null;
    private Long rolId = null;
    private Double salarioBase = null;
}