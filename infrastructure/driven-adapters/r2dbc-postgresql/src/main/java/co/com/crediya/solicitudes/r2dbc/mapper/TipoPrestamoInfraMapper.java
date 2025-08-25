package co.com.crediya.solicitudes.r2dbc.mapper;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamoId;
import co.com.crediya.solicitudes.model.valueobjects.Monto;
import co.com.crediya.solicitudes.model.valueobjects.Nombre;
import co.com.crediya.solicitudes.model.valueobjects.TasaInteres;
import co.com.crediya.solicitudes.r2dbc.model.TipoPrestamoData;

/**
 * Mapper para convertir entre el modelo de dominio TipoPrestamo y el modelo de persistencia TipoPrestamoData.
 * 
 * Este mapper sigue las reglas de adaptadores secundarios:
 * - Conversión pura sin side effects
 * - Validación de datos de entrada
 * - Manejo seguro de valores null
 * - No contiene lógica de negocio
 */
@Component
public class TipoPrestamoInfraMapper {
    
    /**
     * Convierte un modelo de dominio TipoPrestamo a un modelo de persistencia TipoPrestamoData.
     * 
     * @param tipoPrestamo entidad del dominio
     * @return modelo de persistencia
     * @throws IllegalArgumentException si el parámetro es null
     */
    public TipoPrestamoData toData(TipoPrestamo tipoPrestamo) {
        if (tipoPrestamo == null) {
            throw new IllegalArgumentException("TipoPrestamo no puede ser null");
        }
        
        return new TipoPrestamoData(
            tipoPrestamo.id().value(),
            tipoPrestamo.nombre().valor(),
            tipoPrestamo.montoMinimo().valor(),
            tipoPrestamo.montoMaximo().valor(),
            tipoPrestamo.tasaInteres().valor(),
            tipoPrestamo.validacionAutomatica(),
            tipoPrestamo.version(),
            null, // fecha_creacion se maneja en la base de datos
            null, // fecha_actualizacion se maneja en la base de datos
            true  // activo por defecto
        );
    }
    
    /**
     * Convierte un modelo de persistencia TipoPrestamoData a un modelo de dominio TipoPrestamo.
     * 
     * @param tipoPrestamoData modelo de persistencia
     * @return entidad del dominio
     * @throws IllegalArgumentException si el parámetro es null o los datos son inválidos
     */
    public TipoPrestamo toDomain(TipoPrestamoData tipoPrestamoData) {
        if (tipoPrestamoData == null) {
            throw new IllegalArgumentException("TipoPrestamoData no puede ser null");
        }
        
        if (tipoPrestamoData.idTipoPrestamo() == null) {
            throw new IllegalArgumentException("ID de tipo de préstamo no puede ser null");
        }
        
        if (tipoPrestamoData.nombre() == null || tipoPrestamoData.nombre().isBlank()) {
            throw new IllegalArgumentException("Nombre de tipo de préstamo no puede ser null o vacío");
        }
        
        if (tipoPrestamoData.montoMinimo() == null) {
            throw new IllegalArgumentException("Monto mínimo no puede ser null");
        }
        
        if (tipoPrestamoData.montoMaximo() == null) {
            throw new IllegalArgumentException("Monto máximo no puede ser null");
        }
        
        if (tipoPrestamoData.tasaInteresAnual() == null) {
            throw new IllegalArgumentException("Tasa de interés no puede ser null");
        }
        
        if (tipoPrestamoData.validacionAutomatica() == null) {
            throw new IllegalArgumentException("Validación automática no puede ser null");
        }
        
        if (tipoPrestamoData.version() == null || tipoPrestamoData.version() < 0) {
            throw new IllegalArgumentException("Versión debe ser un número no negativo");
        }
        
        TipoPrestamoId tipoPrestamoId = new TipoPrestamoId(tipoPrestamoData.idTipoPrestamo());
        Nombre nombre = new Nombre(tipoPrestamoData.nombre());
        Monto montoMinimo = Monto.of(tipoPrestamoData.montoMinimo());
        Monto montoMaximo = Monto.of(tipoPrestamoData.montoMaximo());
        TasaInteres tasaInteres = TasaInteres.of(tipoPrestamoData.tasaInteresAnual());
        
        return TipoPrestamo.reconstruir(
            tipoPrestamoId,
            nombre,
            montoMinimo,
            montoMaximo,
            tasaInteres,
            tipoPrestamoData.validacionAutomatica(),
            tipoPrestamoData.version()
        );
    }
    
    /**
     * Convierte un UUID a TipoPrestamoId del dominio.
     * 
     * @param uuid UUID del tipo de préstamo
     * @return TipoPrestamoId del dominio
     * @throws IllegalArgumentException si el UUID es null
     */
    public TipoPrestamoId toTipoPrestamoId(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID no puede ser null");
        }
        return new TipoPrestamoId(uuid);
    }
    
    /**
     * Convierte un TipoPrestamoId del dominio a UUID.
     * 
     * @param tipoPrestamoId TipoPrestamoId del dominio
     * @return UUID para persistencia
     * @throws IllegalArgumentException si el TipoPrestamoId es null
     */
    public UUID toUUID(TipoPrestamoId tipoPrestamoId) {
        if (tipoPrestamoId == null) {
            throw new IllegalArgumentException("TipoPrestamoId no puede ser null");
        }
        return tipoPrestamoId.value();
    }
    
    /**
     * Convierte un Monto del dominio a BigDecimal para persistencia.
     * 
     * @param monto Monto del dominio
     * @return BigDecimal para persistencia
     * @throws IllegalArgumentException si el Monto es null
     */
    public BigDecimal toBigDecimal(Monto monto) {
        if (monto == null) {
            throw new IllegalArgumentException("Monto no puede ser null");
        }
        return monto.valor();
    }
    
    /**
     * Convierte un BigDecimal de persistencia a Monto del dominio.
     * 
     * @param valor BigDecimal de persistencia
     * @return Monto del dominio
     * @throws IllegalArgumentException si el BigDecimal es null
     */
    public Monto toMonto(BigDecimal valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Valor no puede ser null");
        }
        return Monto.of(valor);
    }
}
