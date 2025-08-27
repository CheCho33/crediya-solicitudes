package co.com.crediya.solicitudes.r2dbc.mapper;

import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.r2dbc.entities.SolicitudEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SolicitudMapperTest {

    @Test
    void toEntity_conSolicitudValida_debeRetornarSolicitudEntity() {
        Solicitud solicitud = new Solicitud(1L, 5000.0, 12.0, "test@test.com", 1L, 2L);

        SolicitudEntity entity = SolicitudMapper.toEntity(solicitud);

        assertNotNull(entity);
        assertEquals(solicitud.getIdSolicitud(), entity.getIdSolicitud());
        assertEquals(solicitud.getMonto(), entity.getMonto());
        assertEquals(solicitud.getPlazo(), entity.getPlazo());
        assertEquals(solicitud.getEmail(), entity.getEmail());
        assertEquals(solicitud.getIdEstado(), entity.getIdEstado());
        assertEquals(solicitud.getIdTipoPrestamo(), entity.getIdTipoPrestamo());
    }

    @Test
    void toEntity_conSolicitudNula_debeLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            SolicitudMapper.toEntity(null);
        });
    }

    @Test
    void toDomain_conSolicitudEntityValida_debeRetornarSolicitud() {
        SolicitudEntity entity = SolicitudEntity.builder()
                .idSolicitud(1L)
                .monto(10000.0)
                .plazo(24.0)
                .email("domain@test.com")
                .idEstado(2L)
                .idTipoPrestamo(3L)
                .build();

        Solicitud solicitud = SolicitudMapper.toDomain(entity);

        assertNotNull(solicitud);
        assertEquals(entity.getIdSolicitud(), solicitud.getIdSolicitud());
        assertEquals(entity.getMonto(), solicitud.getMonto());
        assertEquals(entity.getPlazo(), solicitud.getPlazo());
        assertEquals(entity.getEmail(), solicitud.getEmail());
        assertEquals(entity.getIdEstado(), solicitud.getIdEstado());
        assertEquals(entity.getIdTipoPrestamo(), solicitud.getIdTipoPrestamo());
    }

    @Test
    void toDomain_conSolicitudEntityNula_debeLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            SolicitudMapper.toDomain(null);
        });
    }
}

