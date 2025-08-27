package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.api.dto.CrearSolicitudRequest;
import co.com.crediya.solicitudes.api.mapper.CrearSolicitudRequestMapper;
import co.com.crediya.solicitudes.api.mapper.SolicitudResponseMapper;
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
        return serverRequest
                .bodyToMono(CrearSolicitudRequest.class)
                .flatMap(req -> crearSolicitudUseCase
                        .crearSolicitud(req.monto(), req.plazo(), req.email(), req.idTipoPrestamo()))
                .map(solicitudResponseMapper::toResponse)
                .flatMap(resp -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(resp))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(e.getMessage()))
                .onErrorResume(IllegalStateException.class, e ->
                        ServerResponse.status(HttpStatus.CONFLICT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(e.getMessage()));
    }
}
