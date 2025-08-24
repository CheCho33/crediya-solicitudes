package co.com.crediya.solicitudes.model.valueobjects;

import java.util.regex.Pattern;

/**
 * Value Object que representa una dirección de correo electrónico.
 * Inmutable y auto-validado.
 */
public record Email(String value) {
    
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[^@]+@[^@]+\\.[^@]+$");
    
    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El email no puede ser nulo o vacío");
        }
        if (!EMAIL_REGEX.matcher(value).matches()) {
            throw new IllegalArgumentException("Formato de email inválido: " + value);
        }
        if (value.length() > 254) {
            throw new IllegalArgumentException("El email no puede tener más de 254 caracteres");
        }
    }
    
    /**
     * Crea un email con el valor especificado.
     */
    public static Email of(String value) {
        return new Email(value);
    }
    
    /**
     * Obtiene el dominio del email.
     */
    public String domain() {
        return value.substring(value.indexOf('@') + 1);
    }
    
    /**
     * Obtiene la parte local del email (antes del @).
     */
    public String localPart() {
        return value.substring(0, value.indexOf('@'));
    }
    
    /**
     * Verifica si el email pertenece a un dominio específico.
     */
    public boolean belongsToDomain(String domain) {
        return this.domain().equalsIgnoreCase(domain);
    }
}

