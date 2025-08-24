package co.com.crediya.solicitudes.usecase.solicitud;

import java.util.UUID;
import java.util.function.Supplier;

import co.com.crediya.solicitudes.model.estados.Estados;
import co.com.crediya.solicitudes.model.estados.gateways.EstadosRepository;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.SolicitudId;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamoId;
import co.com.crediya.solicitudes.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.crediya.solicitudes.model.valueobjects.Email;
import co.com.crediya.solicitudes.model.valueobjects.Monto;
import co.com.crediya.solicitudes.model.valueobjects.Plazo;
import reactor.core.publisher.Mono;

/**
 * Caso de uso para crear una nueva solicitud de préstamo.
 * 
 * Este caso de uso implementa las siguientes reglas de negocio:
 * - Valida que el tipo de préstamo seleccionado exista
 * - Asigna automáticamente el estado inicial "Pendiente de revisión"
 * - Valida que el monto esté dentro del rango permitido para el tipo de préstamo
 * - Genera un identificador único para la solicitud
 * - Persiste la solicitud en la base de datos
 * 
 * Sigue los principios de Arquitectura Hexagonal:
 * - Orquesta la lógica de negocio sin depender de detalles técnicos
 * - Utiliza programación reactiva con Project Reactor
 * - Mantiene la pureza del dominio
 * - Maneja errores de negocio de forma explícita
 */
public class CrearSolicitudUseCase {
    
    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final EstadosRepository estadosRepository;
    private final Supplier<UUID> uuidGenerator;
    
    /**
     * Constructor principal del caso de uso.
     * 
     * @param solicitudRepository repositorio de solicitudes
     * @param tipoPrestamoRepository repositorio de tipos de préstamo
     * @param estadosRepository repositorio de estados
     * @param uuidGenerator generador de UUIDs
     */
    public CrearSolicitudUseCase(SolicitudRepository solicitudRepository,
                                TipoPrestamoRepository tipoPrestamoRepository,
                                EstadosRepository estadosRepository,
                                Supplier<UUID> uuidGenerator) {
        this.solicitudRepository = solicitudRepository;
        this.tipoPrestamoRepository = tipoPrestamoRepository;
        this.estadosRepository = estadosRepository;
        this.uuidGenerator = uuidGenerator;
    }
    
    /**
     * Constructor por defecto que usa UUID.randomUUID() como generador.
     * Usado principalmente para testing y configuración automática.
     */
    public CrearSolicitudUseCase(SolicitudRepository solicitudRepository,
                                TipoPrestamoRepository tipoPrestamoRepository,
                                EstadosRepository estadosRepository) {
        this(solicitudRepository, tipoPrestamoRepository, estadosRepository, UUID::randomUUID);
    }
    
    /**
     * Crea una nueva solicitud de préstamo.
     * 
     * @param montoSolicitado monto del préstamo solicitado
     * @param plazoMeses plazo en meses del préstamo
     * @param emailSolicitante email del solicitante
     * @param idTipoPrestamo identificador del tipo de préstamo
     * @return Mono con la solicitud creada
     * @throws IllegalArgumentException si los datos de entrada son inválidos
     * @throws IllegalStateException si el tipo de préstamo no existe o el estado inicial no está disponible
     */
    public Mono<Solicitud> crearSolicitud(Monto montoSolicitado, 
                                         Plazo plazoMeses, 
                                         Email emailSolicitante, 
                                         TipoPrestamoId idTipoPrestamo) {
        
        return validarTipoPrestamo(idTipoPrestamo)
                .flatMap(tipoPrestamo -> validarMontoParaTipoPrestamo(montoSolicitado, tipoPrestamo))
                .flatMap(tipoPrestamo -> obtenerEstadoInicial()
                        .flatMap(estadoInicial -> crearYGuardarSolicitud(
                                montoSolicitado, 
                                plazoMeses, 
                                emailSolicitante, 
                                tipoPrestamo, 
                                estadoInicial)));
    }
    
    /**
     * Valida que el tipo de préstamo exista en el sistema.
     * 
     * @param idTipoPrestamo identificador del tipo de préstamo
     * @return Mono con el tipo de préstamo si existe
     * @throws IllegalStateException si el tipo de préstamo no existe
     */
    private Mono<TipoPrestamo> validarTipoPrestamo(TipoPrestamoId idTipoPrestamo) {
        return tipoPrestamoRepository.findById(idTipoPrestamo)
                .switchIfEmpty(Mono.error(new IllegalStateException(
                        "El tipo de préstamo con ID " + idTipoPrestamo.value() + " no existe")));
    }
    
    /**
     * Valida que el monto esté dentro del rango permitido para el tipo de préstamo.
     * 
     * @param montoSolicitado monto a validar
     * @param tipoPrestamo tipo de préstamo para validar el monto
     * @return Mono con el tipo de préstamo si la validación es exitosa
     * @throws IllegalArgumentException si el monto no está en el rango permitido
     */
    private Mono<TipoPrestamo> validarMontoParaTipoPrestamo(Monto montoSolicitado, TipoPrestamo tipoPrestamo) {
        if (!tipoPrestamo.montoValido(montoSolicitado)) {
            return Mono.error(new IllegalArgumentException(
                    String.format("El monto $%,.2f no está dentro del rango permitido para el tipo de préstamo '%s' (%s)", 
                            montoSolicitado.valor(), 
                            tipoPrestamo.nombre().valor(),
                            tipoPrestamo.obtenerRangoMontos())));
        }
        return Mono.just(tipoPrestamo);
    }
    
    /**
     * Obtiene el estado inicial "Pendiente de revisión" para la nueva solicitud.
     * 
     * @return Mono con el estado inicial
     * @throws IllegalStateException si el estado inicial no está disponible
     */
    private Mono<Estados> obtenerEstadoInicial() {
        return estadosRepository.findByNombre("Pendiente de revisión")
                .switchIfEmpty(Mono.error(new IllegalStateException(
                        "El estado inicial 'Pendiente de revisión' no está disponible en el sistema")));
    }
    
    /**
     * Crea y guarda la solicitud en la base de datos.
     * 
     * @param montoSolicitado monto del préstamo
     * @param plazoMeses plazo en meses
     * @param emailSolicitante email del solicitante
     * @param tipoPrestamo tipo de préstamo
     * @param estadoInicial estado inicial
     * @return Mono con la solicitud guardada
     */
    private Mono<Solicitud> crearYGuardarSolicitud(Monto montoSolicitado, 
                                                   Plazo plazoMeses, 
                                                   Email emailSolicitante, 
                                                   TipoPrestamo tipoPrestamo, 
                                                   Estados estadoInicial) {
        
        // Generar identificador único para la solicitud
        SolicitudId solicitudId = SolicitudId.newId(uuidGenerator);
        
        // Crear la entidad Solicitud
        Solicitud solicitud = Solicitud.create(
                solicitudId,
                montoSolicitado,
                plazoMeses,
                emailSolicitante,
                estadoInicial.idEstado(),
                tipoPrestamo.id()
        );
        
        // Guardar en la base de datos
        return solicitudRepository.save(solicitud);
    }
}
