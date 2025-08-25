package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.api.dto.CrearSolicitudRequest;
import co.com.crediya.solicitudes.api.dto.SolicitudResponse;
import co.com.crediya.solicitudes.api.mapper.SolicitudEntryMapper;
import co.com.crediya.solicitudes.usecase.solicitud.CrearSolicitudUseCase;
import lombok.RequiredArgsConstructor;
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
public class Handler {
    
    private final CrearSolicitudUseCase crearSolicitudUseCase;
    private final SolicitudEntryMapper solicitudEntryMapper;

    /**
     * Endpoint para crear una nueva solicitud de préstamo.
     * 
     * @param serverRequest request HTTP con los datos de la solicitud
     * @return response con la solicitud creada
     */
    public Mono<ServerResponse> crearSolicitud(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CrearSolicitudRequest.class)
                .doOnNext(request -> System.out.println("Creando solicitud de préstamo para email: " + 
                        request.emailSolicitante()))
                .map(solicitudEntryMapper::toDomain)
                .flatMap(creationData -> crearSolicitudUseCase.crearSolicitud(
                        creationData.montoSolicitado(),
                        creationData.plazoMeses(),
                        creationData.emailSolicitante(),
                        creationData.idTipoPrestamo()))
                .map(solicitudEntryMapper::toResponse)
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .doOnSuccess(response -> System.out.println("Solicitud creada exitosamente"))
                .doOnError(error -> System.err.println("Error al crear solicitud: " + error.getMessage()));
    }

    public Mono<ServerResponse> listenGETUseCase(ServerRequest serverRequest) {
        // useCase.logic();
        return ServerResponse.ok().bodyValue("");
    }

    public Mono<ServerResponse> listenGETOtherUseCase(ServerRequest serverRequest) {
        // useCase2.logic();
        return ServerResponse.ok().bodyValue("");
    }

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        // useCase.logic();
        return ServerResponse.ok().bodyValue("");
    }
}
