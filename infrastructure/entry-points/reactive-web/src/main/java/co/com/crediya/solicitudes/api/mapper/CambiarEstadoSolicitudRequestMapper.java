package co.com.crediya.solicitudes.api.mapper;

import org.springframework.stereotype.Component;

import co.com.crediya.solicitudes.api.dto.CambiarEstadoSolicitudRequest;

/**
 * Mapper para convertir el DTO de cambio de estado a los parámetros del caso de uso.
 */
@Component
public class CambiarEstadoSolicitudRequestMapper {

    /**
     * Extrae el email del DTO.
     */
    public String getEmail(CambiarEstadoSolicitudRequest request) {
        return request != null ? request.email() : null;
    }

    /**
     * Extrae el nuevo estado del DTO.
     */
    public String getNuevoEstado(CambiarEstadoSolicitudRequest request) {
        return request != null ? request.nuevoEstado() : null;
    }
}
