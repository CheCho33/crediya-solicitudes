package co.com.crediya.solicitudes.r2dbc.mapper;

import org.springframework.stereotype.Component;

import co.com.crediya.solicitudes.model.estados.Estados;
import co.com.crediya.solicitudes.r2dbc.entities.EstadoEntity;

/**
 * Mapper entre el modelo de dominio Estados y el modelo de persistencia EstadoEntity.
 * Conversión directa 1:1 (id, nombre, descripcion).
 */
public class EstadoEntityMapper {

    public static EstadoEntity toEntity(Estados estados) {
        if (estados == null) { throw new IllegalArgumentException("Estados no puede ser null");}
        return EstadoEntity.builder()
                .idEstado(estados.getIdEstado())
                .nombre(estados.getNombre())
                .descripcion(estados.getDescripcion())
                .build();
    }

    public static Estados toDomain(EstadoEntity estadoEntity) {
        if (estadoEntity == null) {
            throw new IllegalArgumentException("EstadoEntity no puede ser null");
        }

        return new Estados(
                estadoEntity.getIdEstado(),
                estadoEntity.getNombre(),
                estadoEntity.getDescripcion()
        );
    }
}
