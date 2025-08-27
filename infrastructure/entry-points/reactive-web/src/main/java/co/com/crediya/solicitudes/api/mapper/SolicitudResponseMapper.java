package co.com.crediya.solicitudes.api.mapper;

import org.springframework.stereotype.Component;

import co.com.crediya.solicitudes.api.dto.SolicitudResponse;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;

@Component
public class SolicitudResponseMapper {
    
    public SolicitudResponse toResponse(Solicitud solicitud) {
        if (solicitud == null) {
            return null;
        }

        return new SolicitudResponse(
            solicitud.getIdSolicitud(),
                solicitud.getMonto(),
            solicitud.getPlazo(),
            solicitud.getEmail(),
            solicitud.getIdEstado(),
            solicitud.getIdTipoPrestamo()
        );
    }
}
