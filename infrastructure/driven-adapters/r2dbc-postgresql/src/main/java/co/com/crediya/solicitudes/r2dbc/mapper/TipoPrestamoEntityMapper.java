package co.com.crediya.solicitudes.r2dbc.mapper;

import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.solicitudes.r2dbc.entities.TipoPrestamoEntity;

/**
 * Mapper entre el dominio TipoPrestamo y la entidad de persistencia TipoPrestamoEntity.
 * Conversión 1:1 con tipos simples (Long, String, Double, Boolean).
 */
public class  TipoPrestamoEntityMapper {

    public static TipoPrestamoEntity toEntity(TipoPrestamo tipoPrestamo) {
        if (tipoPrestamo == null) {
            throw new IllegalArgumentException("TipoPrestamo no puede ser null");
        }
        return TipoPrestamoEntity.builder()
                .idTipoPrestamo(tipoPrestamo.getIdTipoPrestamo())
                .nombre(tipoPrestamo.getNombre())
                .montoMinimo(tipoPrestamo.getMontoMinimo())
                .montoMaximo(tipoPrestamo.getMontoMaximo())
                .tasaInteres(tipoPrestamo.getTasaInteres())
                .validacionAutomatica(tipoPrestamo.getValidacionAutomatica())
                .build();
    }

    public static TipoPrestamo toDomain(TipoPrestamoEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("TipoPrestamoEntity no puede ser null");
        }

        return new TipoPrestamo(
                entity.getIdTipoPrestamo(),
                entity.getNombre(),
                entity.getMontoMinimo(),
                entity.getMontoMaximo(),
                entity.getTasaInteres(),
                entity.getValidacionAutomatica()
        );
    }
}
