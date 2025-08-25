package co.com.crediya.solicitudes.r2dbc.mapper;

import java.time.LocalDateTime;

import co.com.crediya.solicitudes.model.estados.EstadoId;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.SolicitudId;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamoId;
import co.com.crediya.solicitudes.model.valueobjects.Email;
import co.com.crediya.solicitudes.model.valueobjects.Monto;
import co.com.crediya.solicitudes.model.valueobjects.Plazo;
import co.com.crediya.solicitudes.r2dbc.model.SolicitudData;
import lombok.experimental.UtilityClass;

/**
 * Mapper para convertir entre la entidad del dominio Solicitud y el modelo de datos SolicitudData.
 * 
 * Este mapper sigue las reglas de adaptadores secundarios:
 * - Conversión bidireccional entre dominio e infraestructura
 * - Manejo de tipos primitivos y value objects
 * - Sin lógica de negocio
 * - Métodos estáticos para evitar instanciación
 */
@UtilityClass
public class SolicitudInfraMapper {
    
    /**
     * Convierte una entidad del dominio a un modelo de datos de infraestructura.
     * 
     * @param solicitud entidad del dominio
     * @return modelo de datos para persistencia
     * @throws IllegalArgumentException si la solicitud es null
     */
    public static SolicitudData toData(Solicitud solicitud) {
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser null");
        }
        
        return SolicitudData.builder()
                .idSolicitud(solicitud.id().value())
                .montoSolicitado(solicitud.monto().valor())
                .plazoMeses(solicitud.plazo().meses())
                .emailSolicitante(solicitud.email().value())
                .idEstado(solicitud.idEstado().value())
                .idTipoPrestamo(solicitud.idTipoPrestamo().value())
                .version(solicitud.version())
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .activo(true)
                .build();
    }
    
    /**
     * Convierte un modelo de datos de infraestructura a una entidad del dominio.
     * 
     * @param solicitudData modelo de datos de persistencia
     * @return entidad del dominio
     * @throws IllegalArgumentException si el modelo de datos es null
     */
    public static Solicitud toDomain(SolicitudData solicitudData) {
        if (solicitudData == null) {
            throw new IllegalArgumentException("El modelo de datos no puede ser null");
        }
        
        // Crear value objects del dominio
        SolicitudId id = new SolicitudId(solicitudData.getIdSolicitud());
        Monto monto = Monto.of(solicitudData.getMontoSolicitado());
        Plazo plazo = Plazo.of(solicitudData.getPlazoMeses());
        Email email = Email.of(solicitudData.getEmailSolicitante());
        EstadoId idEstado = new EstadoId(solicitudData.getIdEstado());
        TipoPrestamoId idTipoPrestamo = new TipoPrestamoId(solicitudData.getIdTipoPrestamo());
        
        // Crear entidad del dominio usando el método de reconstrucción
        return Solicitud.from(id, monto, plazo, email, idEstado, idTipoPrestamo, solicitudData.getVersion());
    }
    
    /**
     * Actualiza un modelo de datos existente con los valores de una entidad del dominio.
     * Mantiene los campos de auditoría y solo actualiza los campos de negocio.
     * 
     * @param solicitud entidad del dominio con los nuevos valores
     * @param existingData modelo de datos existente
     * @return modelo de datos actualizado
     * @throws IllegalArgumentException si alguno de los parámetros es null
     */
    public static SolicitudData updateData(Solicitud solicitud, SolicitudData existingData) {
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser null");
        }
        if (existingData == null) {
            throw new IllegalArgumentException("El modelo de datos existente no puede ser null");
        }
        
        return SolicitudData.builder()
                .idSolicitud(existingData.getIdSolicitud()) // No cambiar el ID
                .montoSolicitado(solicitud.monto().valor())
                .plazoMeses(solicitud.plazo().meses())
                .emailSolicitante(solicitud.email().value())
                .idEstado(solicitud.idEstado().value())
                .idTipoPrestamo(solicitud.idTipoPrestamo().value())
                .version(solicitud.version())
                .fechaCreacion(existingData.getFechaCreacion()) // Mantener fecha de creación
                .fechaActualizacion(LocalDateTime.now()) // Actualizar fecha de modificación
                .activo(existingData.getActivo()) // Mantener estado activo
                .build();
    }
}
