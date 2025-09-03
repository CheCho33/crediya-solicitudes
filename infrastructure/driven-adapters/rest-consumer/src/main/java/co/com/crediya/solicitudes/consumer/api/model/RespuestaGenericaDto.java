package co.com.crediya.solicitudes.consumer.api.model;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
* RespuestaGenericaDto
*/

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaGenericaDto {
    private String mensaje = null;
    private Object data = null;
}