package co.com.crediya.solicitudes.model.sesion;
import lombok.*;

import java.util.Date;
//import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SesionApi {
    private Long sesionId ;
    private Long usuarioId;
    private String token;
    private Date fechaExpiracion;
}
