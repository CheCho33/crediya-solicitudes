package co.com.crediya.solicitudes.consumer.api;

import co.com.crediya.solicitudes.consumer.api.model.CrearUsuarioDto;
import co.com.crediya.solicitudes.consumer.api.model.ErrorResponseDto;
import co.com.crediya.solicitudes.consumer.api.model.RespuestaGenericaDto;
import co.com.crediya.solicitudes.consumer.api.model.UsuarioResponseDto;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import lombok.RequiredArgsConstructor;

@Log4j2
@RequiredArgsConstructor
@Service
public class UsuariosApi {
    private final WebClient client;

    /**
    * Build call for getUsuario
    * @param xToken Token de autenticación para acceder al servicio (required)
    * @return Mono<UsuarioResponseDto> response
    */
    public Mono<UsuarioResponseDto> getUsuarioRequest(String xToken) {
        return client.method(HttpMethod.GET)
            .uri("/api/v1/usuarios")
            .header("x-token", xToken)
            .accept(MediaType.parseMediaType("*/*"))
            .retrieve()
            .bodyToMono(UsuarioResponseDto.class);
    }
    /**
    * Build call for guardarUsuario
    * @param body Datos del usuario a registrar (required)
    * @param xToken Token de autenticación para acceder al servicio (required)
    * @return Mono<RespuestaGenericaDto> response
    */
    public Mono<RespuestaGenericaDto> guardarUsuarioRequest(CrearUsuarioDto body, String xToken) {
        return client.method(HttpMethod.POST)
            .uri("/api/api/v1/usuarios")
            .header("x-token", xToken)
            .contentType(MediaType.parseMediaType("application/json"))
            .body(BodyInserters.fromValue(body))
            .accept(MediaType.parseMediaType("*/*"))
            .retrieve()
            .bodyToMono(RespuestaGenericaDto.class);
    }
}
