package co.com.crediya.solicitudes.usecase.solicitud;

import co.com.crediya.solicitudes.model.estados.Estados;
import co.com.crediya.solicitudes.model.estados.gateways.EstadosRepository;
import co.com.crediya.solicitudes.model.exceptions.CrediYautentiateException;
import co.com.crediya.solicitudes.model.notificacion.gateways.NotificacionGateway;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import reactor.core.publisher.Mono;

/**
 * Caso de uso para cambiar el estado de una solicitud de préstamo.
 */
public class CambiarEstadoSolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final EstadosRepository estadosRepository;
    private final NotificacionGateway notificacionGateway;

    public CambiarEstadoSolicitudUseCase(SolicitudRepository solicitudRepository,
                                        EstadosRepository estadosRepository,
                                        NotificacionGateway notificacionGateway) {
        this.solicitudRepository = solicitudRepository;
        this.estadosRepository = estadosRepository;
        this.notificacionGateway = notificacionGateway;
    }


    /**
     * Ejecuta el caso de uso para cambiar el estado de una solicitud.
     *
     * @param email email de la solicitud a cambiar
     * @param nombreNuevoEstado nombre del nuevo estado
     * @return Mono con la solicitud actualizada
     */
    public Mono<Solicitud> cambiarEstadoSolicitud(String email, String nombreNuevoEstado) {
        if (email == null || email.trim().isEmpty()) {
            return Mono.error(new CrediYautentiateException("El email no puede ser null o vacío"));
        }
        
        if (nombreNuevoEstado == null || nombreNuevoEstado.trim().isEmpty()) {
            return Mono.error(new CrediYautentiateException("El nombre del nuevo estado no puede ser null o vacío"));
        }

        return solicitudRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new CrediYautentiateException("No se encontró una solicitud con el email: " + email)))
                .zipWith(estadosRepository.findByNombre(nombreNuevoEstado.toUpperCase())
                        .switchIfEmpty(Mono.error(new CrediYautentiateException("No se encontró el estado: " + nombreNuevoEstado))))
                .flatMap(tuple -> {
                    Solicitud solicitud = tuple.getT1();
                    Estados nuevoEstado = tuple.getT2();
                    
                    // Actualizar el estado de la solicitud
                    solicitud.setIdEstado(nuevoEstado.getIdEstado());
                    
                    // Guardar la solicitud actualizada
                            return solicitudRepository.save(solicitud)
                            .flatMap(solicitudActualizada -> {
                                // Enviar notificación del cambio de estado
                                String descripcionEstado = nuevoEstado.getDescripcion() != null ? 
                                    nuevoEstado.getDescripcion() : nombreNuevoEstado;
                                String mensaje = String.format(
                                    "La solicitud del usuario %s, ha cambiado de estado a %s",
                                    email,
                                    descripcionEstado
                                );
                                
                                return notificacionGateway.enviarMensaje(mensaje)
                                        .thenReturn(solicitudActualizada);
                            });
                })
                .onErrorMap(this::mapearExcepciones);
    }

    /**
     * Mapea las excepciones del dominio a excepciones específicas.
     */
    private Throwable mapearExcepciones(Throwable error) {
        if (error instanceof CrediYautentiateException) {
            return error;
        }
        else{
            return new CrediYautentiateException("Ha ocurrido un error inesperado al registrar el usuario. Por favor, intente nuevamente.");
        }
    }
}
