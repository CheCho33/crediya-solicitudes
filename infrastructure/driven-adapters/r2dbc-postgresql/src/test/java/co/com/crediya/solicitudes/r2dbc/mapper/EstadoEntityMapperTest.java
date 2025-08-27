package co.com.crediya.solicitudes.r2dbc.mapper;

import co.com.crediya.solicitudes.model.estados.Estados;
import co.com.crediya.solicitudes.r2dbc.entities.EstadoEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstadoEntityMapperTest {

    @Test
    void toEntity_conEstadosValido_debeRetornarEstadoEntity() {
        Estados estados = new Estados(1L, "Pendiente", "Solicitud pendiente de revisión");

        EstadoEntity entity = EstadoEntityMapper.toEntity(estados);

        assertNotNull(entity);
        assertEquals(estados.getIdEstado(), entity.getIdEstado());
        assertEquals(estados.getNombre(), entity.getNombre());
        assertEquals(estados.getDescripcion(), entity.getDescripcion());
    }

    @Test
    void toEntity_conEstadosNulo_debeLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            EstadoEntityMapper.toEntity(null);
        });
    }

    @Test
    void toDomain_conEstadoEntityValido_debeRetornarEstados() {
        EstadoEntity entity = EstadoEntity.builder()
                .idEstado(1L)
                .nombre("Aprobado")
                .descripcion("Solicitud aprobada")
                .build();

        Estados estados = EstadoEntityMapper.toDomain(entity);

        assertNotNull(estados);
        assertEquals(entity.getIdEstado(), estados.getIdEstado());
        assertEquals(entity.getNombre(), estados.getNombre());
        assertEquals(entity.getDescripcion(), estados.getDescripcion());
    }

    @Test
    void toDomain_conEstadoEntityNulo_debeLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            EstadoEntityMapper.toDomain(null);
        });
    }
}

