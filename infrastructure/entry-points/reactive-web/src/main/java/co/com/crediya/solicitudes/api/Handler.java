package co.com.crediya.solicitudes.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.crediya.solicitudes.api.dto.CambiarEstadoSolicitudRequest;
import co.com.crediya.solicitudes.api.dto.CrearSolicitudRequest;
import co.com.crediya.solicitudes.api.mapper.CambiarEstadoSolicitudRequestMapper;
import co.com.crediya.solicitudes.api.mapper.CrearSolicitudRequestMapper;
import co.com.crediya.solicitudes.api.mapper.SolicitudResponseMapper;
import co.com.crediya.solicitudes.consumer.api.model.UsuarioResponseDto;
import co.com.crediya.solicitudes.usecase.solicitud.CambiarEstadoSolicitudUseCase;
import co.com.crediya.solicitudes.usecase.solicitud.CrearSolicitudUseCase;
import co.com.crediya.solicitudes.usecase.solicitud.ListarSolicitudesPendientesUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ListarSolicitudesPendientesUseCase listarSolicitudesPendientesUseCase;
    private final CambiarEstadoSolicitudUseCase cambiarEstadoSolicitudUseCase;
    private final SolicitudResponseMapper solicitudResponseMapper;
    private final CrearSolicitudRequestMapper crearSolicitudRequestMapper;
    private final CambiarEstadoSolicitudRequestMapper cambiarEstadoSolicitudRequestMapper;

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

    public Mono<ServerResponse> listarSolicitudesPendientes(ServerRequest serverRequest) {
        log.info("Inicio listar solicitudes pendientes");

        Object usuarioAttr = serverRequest.attribute("usuario").orElse(null);
        UsuarioResponseDto usuarioResponseDto = (UsuarioResponseDto) usuarioAttr;

        // Validar que el usuario tenga rol de administrador (rolId = 2)
        if (usuarioResponseDto.getRolId() != 2L) {
            log.warn("El usuario no tiene permiso para revisar solicitudes pendientes. Rol: {}", usuarioResponseDto.getRolId());
            return ServerResponse.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.TEXT_PLAIN)
                    .bodyValue("No tienes permiso para revisar solicitudes pendientes.");
        }

        return listarSolicitudesPendientesUseCase
                .listarSolicitudesPendientes()
                .map(solicitudResponseMapper::toResponse)
                .collectList()
                .flatMap(solicitudes -> {
                    log.info("Se encontraron {} solicitudes pendientes", solicitudes.size());
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(solicitudes);
                })
                .doOnSuccess(resp -> {
                    if (resp != null && resp.statusCode().is2xxSuccessful()) {
                        log.info("Solicitudes pendientes listadas exitosamente");
                    }
                })
                .doOnError(error ->
                        log.error("Error al listar solicitudes pendientes: {}", error.getMessage())
                );
    }

    public Mono<ServerResponse> cambiarEstadoSolicitud(ServerRequest serverRequest) {
        log.info("Inicio cambio de estado de solicitud");

        Object usuarioAttr = serverRequest.attribute("usuario").orElse(null);
        UsuarioResponseDto usuarioResponseDto = (UsuarioResponseDto) usuarioAttr;

        // Validar que el usuario tenga rol de administrador (rolId = 2) o vendedor (rolId = 3)
        if (usuarioResponseDto.getRolId() != 2L && usuarioResponseDto.getRolId() != 1L) {
            log.warn("El usuario no tiene permiso para cambiar el estado de una solicitud. Rol: {}", usuarioResponseDto.getRolId());
            return ServerResponse.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.TEXT_PLAIN)
                    .bodyValue("El usuario no tiene permiso para cambiar el estado de una solicitud");
        }

        return serverRequest
                .bodyToMono(CambiarEstadoSolicitudRequest.class)
                .doOnNext(dto -> log.info("DTO recibido para cambio de estado: {}", dto))
                .flatMap(request -> {
                    String email = cambiarEstadoSolicitudRequestMapper.getEmail(request);
                    String nuevoEstado = cambiarEstadoSolicitudRequestMapper.getNuevoEstado(request);
                    
                    return cambiarEstadoSolicitudUseCase
                            .cambiarEstadoSolicitud(email, nuevoEstado)
                            .map(solicitudResponseMapper::toResponse)
                            .flatMap(respuesta -> ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(respuesta));
                })
                .doOnSuccess(resp -> {
                    if (resp != null && resp.statusCode().is2xxSuccessful()) {
                        log.info("Estado de solicitud cambiado exitosamente");
                    }
                })
                .doOnError(error ->
                        log.error("Error al cambiar el estado de la solicitud: {}", error.getMessage())
                );
    }

}
