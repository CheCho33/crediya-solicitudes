package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.api.dto.CrearSolicitudRequest;
import co.com.crediya.solicitudes.api.mapper.CrearSolicitudRequestMapper;
import co.com.crediya.solicitudes.api.mapper.SolicitudResponseMapper;
import co.com.crediya.solicitudes.consumer.api.model.UsuarioResponseDto;
import co.com.crediya.solicitudes.usecase.solicitud.CrearSolicitudUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Handler para los endpoints de solicitudes de préstamo.
 *
 * Este handler implementa los endpoints funcionales de WebFlux para:
 * - Crear nuevas solicitudes de préstamo
 * - Listar solicitudes existentes
 * - Actualizar estados de solicitudes
 *
 * Sigue los principios de Arquitectura Hexagonal:
 * - Orquesta casos de uso sin lógica de negocio
 * - Utiliza programación reactiva con Project Reactor
 * - Maneja errores de forma consistente
 * - Proporciona logging estructurado
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    private final CrearSolicitudUseCase crearSolicitudUseCase;
    private final SolicitudResponseMapper solicitudResponseMapper;
    private final CrearSolicitudRequestMapper crearSolicitudRequestMapper;

    public Mono<ServerResponse> listenGETUseCase(ServerRequest serverRequest) {
        return ServerResponse.ok().bodyValue("");
    }

    public Mono<ServerResponse> crearSolicitud(ServerRequest serverRequest) {
        log.info("Inicio creación de solicitud");

        Object usuarioAttr = serverRequest.attribute("usuario").orElse(null);
        UsuarioResponseDto usuarioResponseDto = (UsuarioResponseDto) usuarioAttr;

        return serverRequest
                .bodyToMono(CrearSolicitudRequest.class)
                .doOnNext(dto -> log.info("DTO recibido: {}", dto))
                .flatMap(request -> {
                    if (!usuarioResponseDto.getEmail().equals(request.email())) {
                        log.warn("El usuario no tiene permiso para crear solicitudes a nombre de otro usuario");
                        return ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.TEXT_PLAIN)
                                .bodyValue("Solo puedes generar solicitudes a tu nombre.");
                    }

                    return crearSolicitudUseCase
                            .crearSolicitud(request.monto(), request.plazo(), request.email(), request.idTipoPrestamo())
                            .map(solicitudResponseMapper::toResponse)
                            .flatMap(respuesta -> ServerResponse.ok().bodyValue(respuesta));
                })
                .doOnSuccess(resp -> {
                    if (resp != null && resp.statusCode().is2xxSuccessful()) {
                        log.info("Solicitud creada exitosamente");
                    }
                })
                .doOnError(error ->
                        log.error("Error al crear la solicitud: {}", error.getMessage())
                );
    }





}
