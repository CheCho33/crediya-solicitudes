package co.com.crediya.solicitudes.model.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value Object que representa una tasa de interés anual.
 * Inmutable y auto-validado.
 */
public record TasaInteres(BigDecimal valor) {
    
    public TasaInteres {
        if (valor == null) {
            throw new IllegalArgumentException("El valor de la tasa de interés no puede ser nulo");
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La tasa de interés no puede ser negativa o cero");
        }
        if (valor.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("La tasa de interés no puede ser mayor al 100%");
        }
    }
    
    /**
     * Crea una tasa de interés con el valor especificado.
     */
    public static TasaInteres of(BigDecimal valor) {
        return new TasaInteres(valor.setScale(2, RoundingMode.HALF_UP));
    }
    
    /**
     * Crea una tasa de interés con el valor especificado como String.
     */
    public static TasaInteres of(String valor) {
        try {
            return of(new BigDecimal(valor));
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("Formato de tasa de interés inválido");
        }
    }
    
    /**
     * Crea una tasa de interés cero.
     */
    public static TasaInteres cero() {
        return new TasaInteres(BigDecimal.ZERO);
    }
    
    /**
     * Convierte la tasa anual a tasa mensual.
     */
    public BigDecimal aTasaMensual() {
        return valor.divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);
    }
    
    /**
     * Convierte la tasa anual a decimal (ej: 15.5% -> 0.155).
     */
    public BigDecimal aDecimal() {
        return valor.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
    }
    
    /**
     * Verifica si la tasa es mayor que otra.
     */
    public boolean esMayorQue(TasaInteres otra) {
        return this.valor.compareTo(otra.valor) > 0;
    }
    
    /**
     * Verifica si la tasa es menor que otra.
     */
    public boolean esMenorQue(TasaInteres otra) {
        return this.valor.compareTo(otra.valor) < 0;
    }
    
    /**
     * Verifica si la tasa es igual a otra.
     */
    public boolean esIgualA(TasaInteres otra) {
        return this.valor.compareTo(otra.valor) == 0;
    }
}
