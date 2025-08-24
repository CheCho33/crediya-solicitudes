package co.com.crediya.solicitudes.r2dbc.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import co.com.crediya.solicitudes.model.estados.EstadoId;
import co.com.crediya.solicitudes.model.estados.Estados;
import co.com.crediya.solicitudes.r2dbc.model.EstadosData;

/**
 * Mapper para convertir entre el modelo de dominio Estados y el modelo de persistencia EstadosData.
 * 
 * Este mapper sigue las reglas de adaptadores secundarios:
 * - Conversión pura sin side effects
 * - Validación de datos de entrada
 * - Manejo seguro de valores null
 * - No contiene lógica de negocio
 */
@Component
public class EstadosInfraMapper {
    
    /**
     * Convierte un modelo de dominio Estados a un modelo de persistencia EstadosData.
     * 
     * @param estados entidad del dominio
     * @return modelo de persistencia
     * @throws IllegalArgumentException si el parámetro es null
     */
    public EstadosData toData(Estados estados) {
        if (estados == null) {
            throw new IllegalArgumentException("Estados no puede ser null");
        }
        
        return new EstadosData(
            estados.idEstado().value(),
            estados.nombre(),
            estados.descripcion(),
            estados.version(),
            null, // fecha_creacion se maneja en la base de datos
            null, // fecha_actualizacion se maneja en la base de datos
            true  // activo por defecto
        );
    }
    
    /**
     * Convierte un modelo de persistencia EstadosData a un modelo de dominio Estados.
     * 
     * @param estadosData modelo de persistencia
     * @return entidad del dominio
     * @throws IllegalArgumentException si el parámetro es null o los datos son inválidos
     */
    public Estados toDomain(EstadosData estadosData) {
        if (estadosData == null) {
            throw new IllegalArgumentException("EstadosData no puede ser null");
        }
        
        if (estadosData.idEstado() == null) {
            throw new IllegalArgumentException("ID de estado no puede ser null");
        }
        
        if (estadosData.nombre() == null || estadosData.nombre().isBlank()) {
            throw new IllegalArgumentException("Nombre de estado no puede ser null o vacío");
        }
        
        if (estadosData.descripcion() == null) {
            throw new IllegalArgumentException("Descripción de estado no puede ser null");
        }
        
        if (estadosData.version() == null || estadosData.version() < 0) {
            throw new IllegalArgumentException("Versión debe ser un número no negativo");
        }
        
        EstadoId estadoId = new EstadoId(estadosData.idEstado());
        
        return Estados.fromPersistence(
            estadoId,
            estadosData.nombre(),
            estadosData.descripcion(),
            estadosData.version()
        );
    }
    
    /**
     * Convierte un UUID a EstadoId del dominio.
     * 
     * @param uuid UUID del estado
     * @return EstadoId del dominio
     * @throws IllegalArgumentException si el UUID es null
     */
    public EstadoId toEstadoId(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID no puede ser null");
        }
        return new EstadoId(uuid);
    }
    
    /**
     * Convierte un EstadoId del dominio a UUID.
     * 
     * @param estadoId EstadoId del dominio
     * @return UUID para persistencia
     * @throws IllegalArgumentException si el EstadoId es null
     */
    public UUID toUUID(EstadoId estadoId) {
        if (estadoId == null) {
            throw new IllegalArgumentException("EstadoId no puede ser null");
        }
        return estadoId.value();
    }
}
