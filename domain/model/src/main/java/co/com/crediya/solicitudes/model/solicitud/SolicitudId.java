package co.com.crediya.solicitudes.model.solicitud;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Identificador único para la entidad Solicitud.
 * Value Object inmutable que encapsula un UUID.
 */
public record SolicitudId(UUID value) {
    
    public SolicitudId {
        if (value == null) {
            throw new IllegalArgumentException("UUID requerido para SolicitudId");
        }
    }
    
    /**
     * Crea un nuevo SolicitudId usando el generador proporcionado.
     * 
     * @param generator proveedor de UUID
     * @return nueva instancia de SolicitudId
     */
    public static SolicitudId newId(Supplier<UUID> generator) {
        return new SolicitudId(generator.get());
    }
    
    /**
     * Crea un nuevo SolicitudId con un UUID aleatorio.
     * 
     * @return nueva instancia de SolicitudId
     */
    public static SolicitudId random() {
        return new SolicitudId(UUID.randomUUID());
    }
    
    /**
     * Crea un SolicitudId desde un string UUID.
     * 
     * @param uuidString string representando un UUID válido
     * @return nueva instancia de SolicitudId
     * @throws IllegalArgumentException si el string no es un UUID válido
     */
    public static SolicitudId fromString(String uuidString) {
        if (uuidString == null || uuidString.isBlank()) {
            throw new IllegalArgumentException("String UUID requerido");
        }
        try {
            return new SolicitudId(UUID.fromString(uuidString));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Formato de UUID inválido: " + uuidString);
        }
    }
}

