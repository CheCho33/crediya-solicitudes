package co.com.crediya.solicitudes.consumer.api.model;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
* UsuarioLoginDto
*/

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioLoginDto {
    private String email = null;
    private String password = null;
}