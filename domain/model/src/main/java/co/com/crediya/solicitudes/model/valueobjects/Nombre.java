package co.com.crediya.solicitudes.model.valueobjects;

/**
 * Value Object que representa un nombre.
 * Inmutable y auto-validado.
 */
public record Nombre(String valor) {
    
    public Nombre {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        }
        if (valor.length() > 100) {
            throw new IllegalArgumentException("El nombre no puede tener más de 100 caracteres");
        }
        if (!valor.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            throw new IllegalArgumentException("El nombre solo puede contener letras y espacios");
        }
    }
    
    /**
     * Crea un nombre con el valor especificado.
     */
    public static Nombre of(String valor) {
        return new Nombre(valor);
    }
    
    /**
     * Obtiene el nombre en mayúsculas.
     */
    public String enMayusculas() {
        return valor.toUpperCase();
    }
    
    /**
     * Obtiene el nombre en minúsculas.
     */
    public String enMinusculas() {
        return valor.toLowerCase();
    }
    
    /**
     * Obtiene el nombre con la primera letra en mayúscula.
     */
    public String conPrimeraMayuscula() {
        if (valor.isEmpty()) {
            return valor;
        }
        return valor.substring(0, 1).toUpperCase() + valor.substring(1).toLowerCase();
    }
    
    /**
     * Verifica si el nombre contiene el texto especificado (case-insensitive).
     */
    public boolean contiene(String texto) {
        return valor.toLowerCase().contains(texto.toLowerCase());
    }
}
