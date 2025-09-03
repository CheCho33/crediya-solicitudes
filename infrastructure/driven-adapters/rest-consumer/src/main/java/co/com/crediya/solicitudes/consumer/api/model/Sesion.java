package co.com.crediya.solicitudes.consumer.api.model;

import java.util.Objects;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
* Sesion
*/

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sesion {
    private Long sesionId = null;
    private Long usuarioId = null;
    private String token = null;
    private OffsetDateTime fechaExpiracion = null;
}