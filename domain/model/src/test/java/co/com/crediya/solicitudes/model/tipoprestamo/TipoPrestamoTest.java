package co.com.crediya.solicitudes.model.tipoprestamo;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import co.com.crediya.solicitudes.model.valueobjects.Monto;
import co.com.crediya.solicitudes.model.valueobjects.Nombre;
import co.com.crediya.solicitudes.model.valueobjects.TasaInteres;

@DisplayName("TipoPrestamo")
class TipoPrestamoTest {
    
    private TipoPrestamoId id;
    private Nombre nombre;
    private Monto montoMinimo;
    private Monto montoMaximo;
    private TasaInteres tasaInteres;
    
    @BeforeEach
    void setUp() {
        id = TipoPrestamoId.random();
        nombre = Nombre.of("Préstamo Personal");
        montoMinimo = Monto.of("1000000");
        montoMaximo = Monto.of("50000000");
        tasaInteres = TasaInteres.of("15.5");
    }
    
    @Test
    @DisplayName("debería crear un tipo de préstamo válido")
    void deberiaCrearTipoPrestamoValido() {
        // When
        TipoPrestamo tipoPrestamo = TipoPrestamo.crear(id, nombre, montoMinimo, montoMaximo, tasaInteres, true);
        
        // Then
        assertThat(tipoPrestamo.id()).isEqualTo(id);
        assertThat(tipoPrestamo.nombre()).isEqualTo(nombre);
        assertThat(tipoPrestamo.montoMinimo()).isEqualTo(montoMinimo);
        assertThat(tipoPrestamo.montoMaximo()).isEqualTo(montoMaximo);
        assertThat(tipoPrestamo.tasaInteres()).isEqualTo(tasaInteres);
        assertThat(tipoPrestamo.validacionAutomatica()).isTrue();
        assertThat(tipoPrestamo.version()).isZero();
    }
    
    @Test
    @DisplayName("debería rechazar monto mínimo mayor al máximo")
    void deberiaRechazarMontoMinimoMayorAlMaximo() {
        // Given
        Monto montoMinimoMayor = Monto.of("60000000");
        
        // When & Then
        assertThatThrownBy(() -> 
            TipoPrestamo.crear(id, nombre, montoMinimoMayor, montoMaximo, tasaInteres, true)
        ).isInstanceOf(IllegalStateException.class)
         .hasMessageContaining("El monto mínimo no puede ser mayor al monto máximo");
    }
    
    @Test
    @DisplayName("debería validar monto dentro del rango")
    void deberiaValidarMontoDentroDelRango() {
        // Given
        TipoPrestamo tipoPrestamo = TipoPrestamo.crear(id, nombre, montoMinimo, montoMaximo, tasaInteres, true);
        Monto montoValido = Monto.of("25000000");
        
        // When
        boolean esValido = tipoPrestamo.montoValido(montoValido);
        
        // Then
        assertThat(esValido).isTrue();
    }
    
    @Test
    @DisplayName("debería rechazar monto fuera del rango")
    void deberiaRechazarMontoFueraDelRango() {
        // Given
        TipoPrestamo tipoPrestamo = TipoPrestamo.crear(id, nombre, montoMinimo, montoMaximo, tasaInteres, true);
        Monto montoInvalido = Monto.of("60000000");
        
        // When
        boolean esValido = tipoPrestamo.montoValido(montoInvalido);
        
        // Then
        assertThat(esValido).isFalse();
    }
    
    @Test
    @DisplayName("debería calcular cuota mensual correctamente")
    void deberiaCalcularCuotaMensualCorrectamente() {
        // Given
        TipoPrestamo tipoPrestamo = TipoPrestamo.crear(id, nombre, montoMinimo, montoMaximo, tasaInteres, true);
        Monto monto = Monto.of("10000000");
        int plazoMeses = 24;
        
        // When
        Monto cuota = tipoPrestamo.calcularCuotaMensual(monto, plazoMeses);
        
        // Then
        assertThat(cuota).isNotNull();
        assertThat(cuota.valor()).isPositive();
        // La cuota debe ser mayor que el monto dividido por el plazo (sin intereses)
        assertThat(cuota.valor()).isGreaterThan(monto.valor().divide(BigDecimal.valueOf(plazoMeses), 2, BigDecimal.ROUND_HALF_UP));
    }
    
    @Test
    @DisplayName("debería rechazar cálculo de cuota con monto inválido")
    void deberiaRechazarCalculoCuotaConMontoInvalido() {
        // Given
        TipoPrestamo tipoPrestamo = TipoPrestamo.crear(id, nombre, montoMinimo, montoMaximo, tasaInteres, true);
        Monto montoInvalido = Monto.of("60000000");
        
        // When & Then
        assertThatThrownBy(() -> 
            tipoPrestamo.calcularCuotaMensual(montoInvalido, 24)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("El monto no está dentro del rango permitido");
    }
    
    @ParameterizedTest
    @ValueSource(ints = {0, -1, -12})
    @DisplayName("debería rechazar plazo inválido")
    void deberiaRechazarPlazoInvalido(int plazoInvalido) {
        // Given
        TipoPrestamo tipoPrestamo = TipoPrestamo.crear(id, nombre, montoMinimo, montoMaximo, tasaInteres, true);
        Monto monto = Monto.of("10000000");
        
        // When & Then
        assertThatThrownBy(() -> 
            tipoPrestamo.calcularCuotaMensual(monto, plazoInvalido)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("El plazo debe ser mayor a 0 meses");
    }
    
    @Test
    @DisplayName("debería obtener rango de montos como texto")
    void deberiaObtenerRangoMontosComoTexto() {
        // Given
        TipoPrestamo tipoPrestamo = TipoPrestamo.crear(id, nombre, montoMinimo, montoMaximo, tasaInteres, true);
        
        // When
        String rango = tipoPrestamo.obtenerRangoMontos();
        
        // Then
        assertThat(rango).contains("1.000.000,00");
        assertThat(rango).contains("50.000.000,00");
    }
    
    @Test
    @DisplayName("debería indicar si requiere validación automática")
    void deberiaIndicarSiRequiereValidacionAutomatica() {
        // Given
        TipoPrestamo tipoPrestamoConValidacion = TipoPrestamo.crear(id, nombre, montoMinimo, montoMaximo, tasaInteres, true);
        TipoPrestamo tipoPrestamoSinValidacion = TipoPrestamo.crear(id, nombre, montoMinimo, montoMaximo, tasaInteres, false);
        
        // When & Then
        assertThat(tipoPrestamoConValidacion.requiereValidacionAutomatica()).isTrue();
        assertThat(tipoPrestamoSinValidacion.requiereValidacionAutomatica()).isFalse();
    }
    
    @Test
    @DisplayName("debería marcar como persistida con nueva versión")
    void deberiaMarcarComoPersistidaConNuevaVersion() {
        // Given
        TipoPrestamo tipoPrestamo = TipoPrestamo.crear(id, nombre, montoMinimo, montoMaximo, tasaInteres, true);
        long nuevaVersion = 1L;
        
        // When
        tipoPrestamo.markPersisted(nuevaVersion);
        
        // Then
        assertThat(tipoPrestamo.version()).isEqualTo(nuevaVersion);
    }
    
    @Test
    @DisplayName("debería rechazar marcar como persistida con versión menor o igual")
    void deberiaRechazarMarcarComoPersistidaConVersionMenorOIgual() {
        // Given
        TipoPrestamo tipoPrestamo = TipoPrestamo.crear(id, nombre, montoMinimo, montoMaximo, tasaInteres, true);
        tipoPrestamo.markPersisted(1L);
        
        // When & Then
        assertThatThrownBy(() -> tipoPrestamo.markPersisted(0L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("La nueva versión debe ser mayor a la actual");
            
        assertThatThrownBy(() -> tipoPrestamo.markPersisted(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("La nueva versión debe ser mayor a la actual");
    }
    
    @Test
    @DisplayName("debería reconstruir tipo de préstamo con versión")
    void deberiaReconstruirTipoPrestamoConVersion() {
        // Given
        long version = 5L;
        
        // When
        TipoPrestamo tipoPrestamo = TipoPrestamo.reconstruir(id, nombre, montoMinimo, montoMaximo, tasaInteres, true, version);
        
        // Then
        assertThat(tipoPrestamo.id()).isEqualTo(id);
        assertThat(tipoPrestamo.version()).isEqualTo(version);
    }
}
