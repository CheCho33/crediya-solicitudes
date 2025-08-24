package co.com.crediya.solicitudes.model.valueobjects;

/**
 * Value Object que representa el plazo en meses de un préstamo.
 * Inmutable y auto-validado.
 */
public record Plazo(Integer meses) {
    
    private static final int MINIMO_MESES = 1;
    private static final int MAXIMO_MESES = 120; // 10 años
    
    public Plazo {
        if (meses == null) {
            throw new IllegalArgumentException("El plazo no puede ser nulo");
        }
        if (meses < MINIMO_MESES) {
            throw new IllegalArgumentException("El plazo debe ser al menos " + MINIMO_MESES + " mes");
        }
        if (meses > MAXIMO_MESES) {
            throw new IllegalArgumentException("El plazo no puede exceder " + MAXIMO_MESES + " meses");
        }
    }
    
    /**
     * Crea un plazo con el número de meses especificado.
     */
    public static Plazo of(Integer meses) {
        return new Plazo(meses);
    }
    
    /**
     * Crea un plazo desde un string.
     */
    public static Plazo of(String meses) {
        try {
            return of(Integer.parseInt(meses));
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("Formato de plazo inválido: ");
        }
    }
    
    /**
     * Verifica si el plazo está en el rango válido.
     */
    public boolean estaEnRangoValido() {
        return meses >= MINIMO_MESES && meses <= MAXIMO_MESES;
    }
    
    /**
     * Obtiene el plazo en años (aproximado).
     */
    public double enAnios() {
        return meses / 12.0;
    }
    
    /**
     * Verifica si el plazo es corto (menos de 12 meses).
     */
    public boolean esCorto() {
        return meses < 12;
    }
    
    /**
     * Verifica si el plazo es largo (más de 60 meses).
     */
    public boolean esLargo() {
        return meses > 60;
    }
}

