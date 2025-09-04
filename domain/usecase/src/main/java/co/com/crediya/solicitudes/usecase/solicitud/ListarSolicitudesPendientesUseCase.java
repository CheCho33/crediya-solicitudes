package co.com.crediya.solicitudes.usecase.solicitud;

import co.com.crediya.solicitudes.model.estados.gateways.EstadosRepository;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

/**
 * Caso de uso para listar todas las solicitudes que están en estado pendiente.
 *
 * Reglas de negocio:
 * - Busca el estado "PENDIENTE" en la base de datos
 * - Obtiene todas las solicitudes que tienen ese estado
 * - Retorna un flujo reactivo con las solicitudes encontradas
 */
@RequiredArgsConstructor
public class ListarSolicitudesPendientesUseCase {

    private final SolicitudRepository solicitudRepository;
    private final EstadosRepository estadosRepository;

    /**
     * Ejecuta el caso de uso para listar solicitudes pendientes.
     *
     * @return Flux con todas las solicitudes en estado pendiente
     */
    public Flux<Solicitud> listarSolicitudesPendientes() {
        return estadosRepository.findByNombre("PENDIENTE")
                .flatMapMany(estado -> solicitudRepository.findByIdEstado(estado.getIdEstado()))
                .onErrorMap(this::mapearExcepciones);
    }

    /**
     * Mapea excepciones de dominio a excepciones de negocio.
     */
    private Throwable mapearExcepciones(Throwable error) {
        if (error instanceof IllegalArgumentException) {
            return error;
        }
        return new RuntimeException("Error al listar solicitudes pendientes: " + error.getMessage(), error);
    }
}
