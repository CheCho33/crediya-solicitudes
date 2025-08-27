package co.com.crediya.solicitudes.api.mapper;

import org.springframework.stereotype.Component;

import co.com.crediya.solicitudes.api.dto.CrearSolicitudRequest;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;

@Component
public class CrearSolicitudRequestMapper {

    public Solicitud toSolicitud(CrearSolicitudRequest request) {
        if (request == null) {
            return null;
        }

        Solicitud solicitud = new Solicitud();
        // ID y estado se asignarán en capas posteriores (persistencia/estado inicial)
        solicitud.setIdSolicitud(null);
        solicitud.setIdEstado(null);

        // Mapeos directos con conversión simple
        solicitud.setMonto(request.monto());
        solicitud.setPlazo(request.plazo());
        solicitud.setEmail(request.email());
        solicitud.setIdTipoPrestamo(request.idTipoPrestamo());

        return solicitud;
    }
}
