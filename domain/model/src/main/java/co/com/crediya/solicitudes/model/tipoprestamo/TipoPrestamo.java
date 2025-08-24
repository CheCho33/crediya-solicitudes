package co.com.crediya.solicitudes.model.tipoprestamo;

import java.math.BigDecimal;

import co.com.crediya.solicitudes.model.valueobjects.Monto;
import co.com.crediya.solicitudes.model.valueobjects.Nombre;
import co.com.crediya.solicitudes.model.valueobjects.TasaInteres;

/**
 * Entidad que representa un tipo de préstamo en el sistema CrediYa.
 * Aggregate Root que encapsula las reglas de negocio para los tipos de préstamo.
 */
public final class TipoPrestamo {
    
    private final TipoPrestamoId id;
    private final Nombre nombre;
    private final Monto montoMinimo;
    private final Monto montoMaximo;
    private final TasaInteres tasaInteres;
    private final boolean validacionAutomatica;
    private long version;
    
    /**
     * Constructor privado para garantizar invariantes.
     */
    private TipoPrestamo(TipoPrestamoId id, Nombre nombre, Monto montoMinimo, 
                        Monto montoMaximo, TasaInteres tasaInteres, 
                        boolean validacionAutomatica, long version) {
        this.id = id;
        this.nombre = nombre;
        this.montoMinimo = montoMinimo;
        this.montoMaximo = montoMaximo;
        this.tasaInteres = tasaInteres;
        this.validacionAutomatica = validacionAutomatica;
        this.version = version;
        validateInvariants();
    }
    
    /**
     * Crea un nuevo tipo de préstamo.
     */
    public static TipoPrestamo crear(TipoPrestamoId id, Nombre nombre, Monto montoMinimo,
                                   Monto montoMaximo, TasaInteres tasaInteres,
                                   boolean validacionAutomatica) {
        return new TipoPrestamo(id, nombre, montoMinimo, montoMaximo, 
                               tasaInteres, validacionAutomatica, 0L);
    }
    
    /**
     * Crea un tipo de préstamo desde datos existentes (para reconstrucción).
     */
    public static TipoPrestamo reconstruir(TipoPrestamoId id, Nombre nombre, Monto montoMinimo,
                                         Monto montoMaximo, TasaInteres tasaInteres,
                                         boolean validacionAutomatica, long version) {
        return new TipoPrestamo(id, nombre, montoMinimo, montoMaximo, 
                               tasaInteres, validacionAutomatica, version);
    }
    
    /**
     * Valida que un monto esté dentro del rango permitido para este tipo de préstamo.
     */
    public boolean montoValido(Monto monto) {
        return monto.estaEnRango(montoMinimo, montoMaximo);
    }
    
    /**
     * Calcula la cuota mensual para un monto y plazo dados.
     */
    public Monto calcularCuotaMensual(Monto monto, int plazoMeses) {
        if (!montoValido(monto)) {
            throw new IllegalArgumentException("El monto no está dentro del rango permitido para este tipo de préstamo");
        }
        if (plazoMeses <= 0) {
            throw new IllegalArgumentException("El plazo debe ser mayor a 0 meses");
        }
        
        // Fórmula de cuota mensual: P * (r * (1 + r)^n) / ((1 + r)^n - 1)
        // Donde: P = principal, r = tasa mensual, n = número de meses
        BigDecimal principal = monto.valor();
        BigDecimal tasaMensual = tasaInteres.aTasaMensual();
        BigDecimal unoMasTasa = BigDecimal.ONE.add(tasaMensual);
        BigDecimal unoMasTasaElevado = unoMasTasa.pow(plazoMeses);
        
        BigDecimal numerador = principal.multiply(tasaMensual).multiply(unoMasTasaElevado);
        BigDecimal denominador = unoMasTasaElevado.subtract(BigDecimal.ONE);
        
        BigDecimal cuota = numerador.divide(denominador, 2, java.math.RoundingMode.HALF_UP);
        
        return Monto.of(cuota);
    }
    
    /**
     * Verifica si este tipo de préstamo requiere validación automática.
     */
    public boolean requiereValidacionAutomatica() {
        return validacionAutomatica;
    }
    
    /**
     * Obtiene el rango de montos permitidos como texto descriptivo.
     */
    public String obtenerRangoMontos() {
        return String.format("Entre $%,.2f y $%,.2f",
                           montoMinimo.valor(), montoMaximo.valor());
    }
    
    /**
     * Valida los invariantes de la entidad.
     */
    private void validateInvariants() {
        if (id == null) {
            throw new IllegalStateException("El ID del tipo de préstamo no puede ser nulo");
        }
        if (nombre == null) {
            throw new IllegalStateException("El nombre del tipo de préstamo no puede ser nulo");
        }
        if (montoMinimo == null) {
            throw new IllegalStateException("El monto mínimo no puede ser nulo");
        }
        if (montoMaximo == null) {
            throw new IllegalStateException("El monto máximo no puede ser nulo");
        }
        if (tasaInteres == null) {
            throw new IllegalStateException("La tasa de interés no puede ser nula");
        }
        if (montoMinimo.esMayorQue(montoMaximo)) {
            throw new IllegalStateException("El monto mínimo no puede ser mayor al monto máximo");
        }
        if (version < 0) {
            throw new IllegalStateException("La versión no puede ser negativa");
        }
    }
    
    /**
     * Marca la entidad como persistida con la nueva versión.
     */
    public void markPersisted(long newVersion) {
        if (newVersion <= version) {
            throw new IllegalArgumentException("La nueva versión debe ser mayor a la actual");
        }
        this.version = newVersion;
    }
    
    // Getters intencionales (no exponer estado mutable)
    public TipoPrestamoId id() { return id; }
    public Nombre nombre() { return nombre; }
    public Monto montoMinimo() { return montoMinimo; }
    public Monto montoMaximo() { return montoMaximo; }
    public TasaInteres tasaInteres() { return tasaInteres; }
    public boolean validacionAutomatica() { return validacionAutomatica; }
    public long version() { return version; }
}
