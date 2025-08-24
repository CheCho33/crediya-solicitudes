package co.com.crediya.solicitudes.model.valueobjects;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Monto")
class MontoTest {
    
    @Test
    @DisplayName("debería crear monto válido")
    void deberiaCrearMontoValido() {
        // When
        Monto monto = Monto.of("1000000.50");
        
        // Then
        assertThat(monto.valor()).isEqualTo(new BigDecimal("1000000.50"));
    }
    
    @Test
    @DisplayName("debería crear monto con BigDecimal")
    void deberiaCrearMontoConBigDecimal() {
        // Given
        BigDecimal valor = new BigDecimal("2500000.75");
        
        // When
        Monto monto = Monto.of(valor);
        
        // Then
        assertThat(monto.valor()).isEqualTo(valor);
    }
    
    @Test
    @DisplayName("debería crear monto cero")
    void deberiaCrearMontoCero() {
        // When
        Monto monto = Monto.cero();
        
        // Then
        assertThat(monto.valor()).isEqualTo(BigDecimal.ZERO);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"", "null"})
    @DisplayName("debería rechazar valor nulo o vacío")
    void deberiaRechazarValorNuloOVacio(String valor) {
        // When & Then
        assertThatThrownBy(() -> Monto.of((String) null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Formato de monto inválido");
    }
    
    @Test
    @DisplayName("debería rechazar monto negativo")
    void deberiaRechazarMontoNegativo() {
        // When & Then
        assertThatThrownBy(() -> Monto.of("-1000"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("El monto no puede ser negativo");
    }
    
    @Test
    @DisplayName("debería rechazar formato inválido")
    void deberiaRechazarFormatoInvalido() {
        // When & Then
        assertThatThrownBy(() -> Monto.of("no-es-un-numero"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Formato de monto inválido");
    }
    
    @Test
    @DisplayName("debería comparar montos correctamente")
    void deberiaCompararMontosCorrectamente() {
        // Given
        Monto monto1 = Monto.of("1000");
        Monto monto2 = Monto.of("2000");
        Monto monto3 = Monto.of("1000");
        
        // When & Then
        assertThat(monto1.esMenorQue(monto2)).isTrue();
        assertThat(monto2.esMayorQue(monto1)).isTrue();
        assertThat(monto1.esIgualA(monto3)).isTrue();
        assertThat(monto1.esIgualA(monto2)).isFalse();
    }
    
    @Test
    @DisplayName("debería validar rango correctamente")
    void deberiaValidarRangoCorrectamente() {
        // Given
        Monto minimo = Monto.of("1000");
        Monto maximo = Monto.of("5000");
        Monto dentroRango = Monto.of("3000");
        Monto fueraRango = Monto.of("6000");
        
        // When & Then
        assertThat(dentroRango.estaEnRango(minimo, maximo)).isTrue();
        assertThat(fueraRango.estaEnRango(minimo, maximo)).isFalse();
        assertThat(minimo.estaEnRango(minimo, maximo)).isTrue(); // límite inferior
        assertThat(maximo.estaEnRango(minimo, maximo)).isTrue(); // límite superior
    }
    
    @Test
    @DisplayName("debería redondear a 2 decimales")
    void deberiaRedondearA2Decimales() {
        // Given
        BigDecimal valorConMasDecimales = new BigDecimal("1000.567");
        
        // When
        Monto monto = Monto.of(valorConMasDecimales);
        
        // Then
        assertThat(monto.valor()).isEqualTo(new BigDecimal("1000.57"));
    }
}
