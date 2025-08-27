package co.com.crediya.solicitudes.r2dbc.mapper;

import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.r2dbc.entities.SolicitudEntity;

/**
 * Mapper para convertir entre la entidad del dominio Solicitud y el modelo de datos SolicitudData.
 * Usa tipos simples del dominio actual (Long/Double/String).
 */
public class SolicitudMapper {

    public static SolicitudEntity toEntity(Solicitud solicitud) {
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser null");
        }

        return SolicitudEntity.builder()
                .idSolicitud(solicitud.getIdSolicitud())
                .monto(solicitud.getMonto())
                .plazo(solicitud.getPlazo())
                .email(solicitud.getEmail())
                .idEstado(solicitud.getIdEstado())
                .idTipoPrestamo(solicitud.getIdTipoPrestamo())
                .build();
    }

    public static Solicitud toDomain(SolicitudEntity data) {
        if (data == null) {
            throw new IllegalArgumentException("El modelo de datos no puede ser null");
        }
        return new Solicitud(
                data.getIdSolicitud(),
                data.getMonto(),
                data.getPlazo(),
                data.getEmail(),
                data.getIdEstado(),
                data.getIdTipoPrestamo()
        );
    }
}
