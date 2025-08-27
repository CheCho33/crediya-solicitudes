package co.com.crediya.solicitudes.usecase.solicitud;

import co.com.crediya.solicitudes.model.estados.Estados;
import co.com.crediya.solicitudes.model.estados.gateways.EstadosRepository;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.solicitudes.model.tipoprestamo.gateways.TipoPrestamoRepository;
import reactor.core.publisher.Mono;

/**
 * Caso de uso para crear una nueva solicitud de préstamo.
 *
 * Reglas de negocio:
 * - Valida datos de entrada (cliente y préstamo)
 * - Valida que el tipo de préstamo exista
 * - Valida que el monto esté dentro del rango permitido por el tipo de préstamo
 * - Asigna estado inicial "Pendiente de revisión"
 * - Persiste la solicitud
 */
public class CrearSolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final EstadosRepository estadosRepository;

    public CrearSolicitudUseCase(SolicitudRepository solicitudRepository,
                                 TipoPrestamoRepository tipoPrestamoRepository,
                                 EstadosRepository estadosRepository) {
        this.solicitudRepository = solicitudRepository;
        this.tipoPrestamoRepository = tipoPrestamoRepository;
        this.estadosRepository = estadosRepository;
    }

    // --- API principal ---

    /**
     * Crea una solicitud validando entradas y reglas de negocio.
     * Nota: documentoIdentidad se valida como presente, aunque el modelo actual no lo persiste.
     */
    public Mono<Solicitud> crearSolicitud(Double monto,
                                          Double plazo,
                                          String email,
                                          Long idTipoPrestamo) {
        // Validaciones de presencia y formato básico
        if (monto == null || monto <= 0) {
            return Mono.error(new IllegalArgumentException("El monto solicitado debe ser un número positivo"));
        }
        if (plazo == null || plazo <= 0) {
            return Mono.error(new IllegalArgumentException("El plazo en meses debe ser un número positivo"));
        }
        if (email == null || email.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El email del solicitante es obligatorio"));
        }
        if (idTipoPrestamo == null) {
            return Mono.error(new IllegalArgumentException("El identificador del tipo de préstamo es obligatorio"));
        }

        return validarTipoPrestamo(idTipoPrestamo)
                .flatMap(tipo -> validarMontoParaTipoPrestamo(monto, tipo))
                .flatMap(tipo -> obtenerEstadoInicial()
                        .flatMap(estado -> crearYGuardarSolicitud(monto, plazo, email, tipo, estado))
                );
    }

    /**
     * Sobrecarga que valida la presencia del documento de identidad del cliente.
     * El modelo actual no lo persiste; se valida para cumplir el contrato de negocio.
     */
    public Mono<Solicitud> crearSolicitud(Double monto,
                                          Double plazo,
                                          String email,
                                          String documentoIdentidad,
                                          Long idTipoPrestamo) {
        if (documentoIdentidad == null || documentoIdentidad.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El documento de identidad del cliente es obligatorio"));
        }
        return crearSolicitud(monto, plazo, email, idTipoPrestamo);
    }

    // --- Reglas de negocio privadas ---

    private Mono<TipoPrestamo> validarTipoPrestamo(Long idTipoPrestamo) {
        return tipoPrestamoRepository.findById(idTipoPrestamo)
                .switchIfEmpty(Mono.error(new IllegalStateException(
                        "El tipo de préstamo con ID " + idTipoPrestamo + " no existe")));
    }

    private Mono<TipoPrestamo> validarMontoParaTipoPrestamo(Double monto, TipoPrestamo tipoPrestamo) {
        Double min = tipoPrestamo.getMontoMinimo();
        Double max = tipoPrestamo.getMontoMaximo();
        if (min == null || max == null) {
            return Mono.error(new IllegalStateException("El tipo de préstamo no tiene configurado el rango de montos"));
        }
        if (monto < min || monto > max) {
            String nombre = tipoPrestamo.getNombre() != null ? tipoPrestamo.getNombre() : "desconocido";
            return Mono.error(new IllegalArgumentException(
                    String.format("El monto %.2f no está dentro del rango permitido para el tipo de préstamo '%s' (%.2f - %.2f)",
                            monto, nombre, min, max)));
        }
        return Mono.just(tipoPrestamo);
    }

    private Mono<Estados> obtenerEstadoInicial() {
        return estadosRepository.findByNombre("PENDIENTE")
                .switchIfEmpty(Mono.error(new IllegalStateException(
                        "El estado inicial 'PENDIENTE' no está disponible en el sistema")));
    }

    private Mono<Solicitud> crearYGuardarSolicitud(Double monto,
                                                   Double plazo,
                                                   String email,
                                                   TipoPrestamo tipoPrestamo,
                                                   Estados estadoInicial) {
        Solicitud solicitud = new Solicitud();
        solicitud.setMonto(monto);
        solicitud.setPlazo(plazo);
        solicitud.setEmail(email);
        solicitud.setIdEstado(estadoInicial.getIdEstado());
        solicitud.setIdTipoPrestamo(tipoPrestamo.getIdTipoPrestamo());

        return solicitudRepository.save(solicitud);
    }
}
