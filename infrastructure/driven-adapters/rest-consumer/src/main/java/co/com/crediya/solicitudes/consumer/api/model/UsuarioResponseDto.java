package co.com.crediya.solicitudes.consumer.api.model;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
* UsuarioResponseDto
*/

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDto {
    private Long usuarioId = null;
    private String nombre = null;
    private String apellido = null;
    private String email = null;
    private String documentoIdentidad = null;
    private String telefono = null;
    private Long rolId = null;
    private Double salarioBase = null;
}