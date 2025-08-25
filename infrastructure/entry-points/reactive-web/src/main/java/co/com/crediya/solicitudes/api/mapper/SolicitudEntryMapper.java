package co.com.crediya.solicitudes.api.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import co.com.crediya.solicitudes.api.dto.CrearSolicitudRequest;
import co.com.crediya.solicitudes.api.dto.SolicitudResponse;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamoId;
import co.com.crediya.solicitudes.model.valueobjects.Email;
import co.com.crediya.solicitudes.model.valueobjects.Monto;
import co.com.crediya.solicitudes.model.valueobjects.Plazo;

/**
 * Mapper para convertir entre DTOs de entrada/salida y objetos del dominio.
 * 
 * Este mapper se encarga de:
 * - Convertir CrearSolicitudRequest a objetos del dominio (Monto, Plazo, Email, TipoPrestamoId)
 * - Convertir objetos del dominio (Solicitud) a SolicitudResponse
 * 
 * Sigue los principios de Arquitectura Hexagonal:
 * - No contiene lógica de negocio
 * - Solo realiza transformaciones de datos
 * - Mantiene la separación entre capas
 */
@Component
public class SolicitudEntryMapper {
    
    /**
     * Convierte un CrearSolicitudRequest a los objetos del dominio necesarios.
     * 
     * @param request DTO de entrada con los datos de la solicitud
     * @return objeto con los value objects del dominio
     */
    public SolicitudCreationData toDomain(CrearSolicitudRequest request) {
        return new SolicitudCreationData(
            Monto.of(request.montoSolicitado()),
            Plazo.of(request.plazoMeses()),
            Email.of(request.emailSolicitante()),
            new TipoPrestamoId(request.idTipoPrestamo())
        );
    }
    
    /**
     * Convierte una entidad Solicitud del dominio a un DTO de respuesta.
     * 
     * @param solicitud entidad del dominio
     * @return DTO de respuesta
     */
    public SolicitudResponse toResponse(Solicitud solicitud) {
        return new SolicitudResponse(
            solicitud.id().value(),
            solicitud.monto().valor(),
            solicitud.plazo().meses(),
            solicitud.email().value(),
            solicitud.idEstado().value().toString(),
            solicitud.idTipoPrestamo().value(),
            LocalDateTime.now() // TODO: Agregar fecha de creación a la entidad Solicitud
        );
    }
    
    /**
     * Clase interna que encapsula los datos del dominio necesarios para crear una solicitud.
     * Esta clase actúa como un DTO interno para pasar los datos entre el mapper y el caso de uso.
     */
    public static class SolicitudCreationData {
        private final Monto montoSolicitado;
        private final Plazo plazoMeses;
        private final Email emailSolicitante;
        private final TipoPrestamoId idTipoPrestamo;
        
        public SolicitudCreationData(Monto montoSolicitado, Plazo plazoMeses, 
                                   Email emailSolicitante, TipoPrestamoId idTipoPrestamo) {
            this.montoSolicitado = montoSolicitado;
            this.plazoMeses = plazoMeses;
            this.emailSolicitante = emailSolicitante;
            this.idTipoPrestamo = idTipoPrestamo;
        }
        
        public Monto montoSolicitado() {
            return montoSolicitado;
        }
        
        public Plazo plazoMeses() {
            return plazoMeses;
        }
        
        public Email emailSolicitante() {
            return emailSolicitante;
        }
        
        public TipoPrestamoId idTipoPrestamo() {
            return idTipoPrestamo;
        }
    }
}
