package co.com.crediya.solicitudes.consumer.api;

import co.com.crediya.solicitudes.consumer.api.model.ErrorResponseDto;
import co.com.crediya.solicitudes.consumer.api.model.Sesion;
import co.com.crediya.solicitudes.consumer.api.model.UsuarioLoginDto;

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
import lombok.AllArgsConstructor;

@Log4j2
@AllArgsConstructor
@Service
public class AutenticacinApi {
    private final WebClient client;

    /**
    * Build call for loginUsuario
    * @param body Credenciales de autenticación (required)
    * @return Mono<Sesion> response
    */
    public Mono<Sesion> loginUsuarioRequest(UsuarioLoginDto body) {
        return client.method(HttpMethod.POST)
            .uri("/api/v1/login")
            .contentType(MediaType.parseMediaType("application/json"))
            .body(BodyInserters.fromValue(body))
            .accept(MediaType.parseMediaType("*/*"))
            .retrieve()
            .bodyToMono(Sesion.class);
    }
}
