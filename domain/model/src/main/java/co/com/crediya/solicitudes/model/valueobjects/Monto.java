package co.com.crediya.solicitudes.model.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value Object que representa un monto monetario.
 * Inmutable y auto-validado.
 */
public record Monto(BigDecimal valor) {
    
    public Monto {
        if (valor == null) {
            throw new IllegalArgumentException("El valor del monto no puede ser nulo");
        }
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto no puede ser negativo");
        }
        if (valor.scale() > 2) {
            throw new IllegalArgumentException("El monto no puede tener más de 2 decimales");
        }
    }
    
    /**
     * Crea un monto con el valor especificado.
     */
    public static Monto of(BigDecimal valor) {
        return new Monto(valor.setScale(2, RoundingMode.HALF_UP));
    }
    
    /**
     * Crea un monto con el valor especificado como String.
     */
    public static Monto of(String valor) {
        try {
            return of(new BigDecimal(valor));
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("Formato de monto inválido");
        }
    }
    
    /**
     * Crea un monto cero.
     */
    public static Monto cero() {
        return new Monto(BigDecimal.ZERO);
    }
    
    /**
     * Verifica si el monto es mayor que otro.
     */
    public boolean esMayorQue(Monto otro) {
        return this.valor.compareTo(otro.valor) > 0;
    }
    
    /**
     * Verifica si el monto es menor que otro.
     */
    public boolean esMenorQue(Monto otro) {
        return this.valor.compareTo(otro.valor) < 0;
    }
    
    /**
     * Verifica si el monto es igual a otro.
     */
    public boolean esIgualA(Monto otro) {
        return this.valor.compareTo(otro.valor) == 0;
    }
    
    /**
     * Verifica si el monto está en el rango especificado (inclusive).
     */
    public boolean estaEnRango(Monto minimo, Monto maximo) {
        return this.valor.compareTo(minimo.valor) >= 0 && 
               this.valor.compareTo(maximo.valor) <= 0;
    }
}
