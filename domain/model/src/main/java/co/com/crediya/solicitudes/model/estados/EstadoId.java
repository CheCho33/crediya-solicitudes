package co.com.crediya.solicitudes.model.estados;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Identificador único para la entidad Estados.
 * Value Object inmutable que encapsula un UUID.
 */
public record EstadoId(UUID value) {
    
    public EstadoId {
        if (value == null) {
            throw new IllegalArgumentException("UUID requerido para EstadoId");
        }
    }
    
    /**
     * Crea un nuevo EstadoId usando el generador proporcionado.
     * 
     * @param generator proveedor de UUID
     * @return nueva instancia de EstadoId
     */
    public static EstadoId newId(Supplier<UUID> generator) {
        return new EstadoId(generator.get());
    }
    
    /**
     * Crea un nuevo EstadoId con un UUID aleatorio.
     * 
     * @return nueva instancia de EstadoId
     */
    public static EstadoId random() {
        return new EstadoId(UUID.randomUUID());
    }
    
    /**
     * Crea un EstadoId desde un string UUID.
     * 
     * @param uuidString string representando un UUID válido
     * @return nueva instancia de EstadoId
     * @throws IllegalArgumentException si el string no es un UUID válido
     */
    public static EstadoId fromString(String uuidString) {
        if (uuidString == null || uuidString.isBlank()) {
            throw new IllegalArgumentException("String UUID requerido");
        }
        try {
            return new EstadoId(UUID.fromString(uuidString));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Formato de UUID inválido");
        }
    }
}
