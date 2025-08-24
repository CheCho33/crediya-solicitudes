package co.com.crediya.solicitudes.model.tipoprestamo;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Identificador único para un tipo de préstamo.
 * Value Object inmutable que encapsula un UUID.
 */
public record TipoPrestamoId(UUID value) {
    
    public TipoPrestamoId {
        if (value == null) {
            throw new IllegalArgumentException("UUID requerido para TipoPrestamoId");
        }
    }
    
    /**
     * Crea un nuevo identificador usando el generador proporcionado.
     * Útil para testing y generación controlada de IDs.
     */
    public static TipoPrestamoId newId(Supplier<UUID> generator) {
        return new TipoPrestamoId(generator.get());
    }
    
    /**
     * Crea un nuevo identificador con un UUID aleatorio.
     */
    public static TipoPrestamoId random() {
        return new TipoPrestamoId(UUID.randomUUID());
    }
}
