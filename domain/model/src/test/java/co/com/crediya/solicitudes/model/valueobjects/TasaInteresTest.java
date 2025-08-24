package co.com.crediya.solicitudes.model.valueobjects;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("TasaInteres")
class TasaInteresTest {
    
    @Test
    @DisplayName("debería crear tasa de interés válida")
    void deberiaCrearTasaInteresValida() {
        // When
        TasaInteres tasa = TasaInteres.of("15.5");
        
        // Then
        assertThat(tasa.valor()).isEqualTo(new BigDecimal("15.50"));
    }
    
    @Test
    @DisplayName("debería crear tasa de interés con BigDecimal")
    void deberiaCrearTasaInteresConBigDecimal() {
        // Given
        BigDecimal valor = new BigDecimal("12.75");
        
        // When
        TasaInteres tasa = TasaInteres.of(valor);
        
        // Then
        assertThat(tasa.valor()).isEqualTo(valor);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"", "null"})
    @DisplayName("debería rechazar valor nulo o vacío")
    void deberiaRechazarValorNuloOVacio(String valor) {
        // When & Then
        assertThatThrownBy(() -> TasaInteres.of(valor))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Formato de tasa de interés inválido");
    }
    
    @Test
    @DisplayName("debería rechazar tasa de interés negativa")
    void deberiaRechazarTasaInteresNegativa() {
        // When & Then
        assertThatThrownBy(() -> TasaInteres.of("-5.5"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("La tasa de interés no puede ser negativa");
    }
    
    @Test
    @DisplayName("debería rechazar tasa de interés mayor al 100%")
    void deberiaRechazarTasaInteresMayorAl100() {
        // When & Then
        assertThatThrownBy(() -> TasaInteres.of("150.5"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("La tasa de interés no puede ser mayor al 100%");
    }

    
    @Test
    @DisplayName("debería rechazar formato inválido")
    void deberiaRechazarFormatoInvalido() {
        // When & Then
        assertThatThrownBy(() -> TasaInteres.of("no-es-un-numero"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Formato de tasa de interés inválido");
    }
    
    @Test
    @DisplayName("debería convertir a tasa mensual correctamente")
    void deberiaConvertirATasaMensualCorrectamente() {
        // Given
        TasaInteres tasaAnual = TasaInteres.of("12.0"); // 12% anual
        
        // When
        BigDecimal tasaMensual = tasaAnual.aTasaMensual();
        
        // Then
        assertThat(tasaMensual).isEqualTo(new BigDecimal("1.000000")); // 12% / 12 = 1%
    }
    
    @Test
    @DisplayName("debería convertir a decimal correctamente")
    void deberiaConvertirADecimalCorrectamente() {
        // Given
        TasaInteres tasa = TasaInteres.of("15.5"); // 15.5%
        
        // When
        BigDecimal decimal = tasa.aDecimal();
        
        // Then
        assertThat(decimal).isEqualTo(new BigDecimal("0.155000")); // 15.5% = 0.155
    }
    
    @Test
    @DisplayName("debería comparar tasas correctamente")
    void deberiaCompararTasasCorrectamente() {
        // Given
        TasaInteres tasa1 = TasaInteres.of("10.0");
        TasaInteres tasa2 = TasaInteres.of("20.0");
        TasaInteres tasa3 = TasaInteres.of("10.0");
        
        // When & Then
        assertThat(tasa1.esMenorQue(tasa2)).isTrue();
        assertThat(tasa2.esMayorQue(tasa1)).isTrue();
        assertThat(tasa1.esIgualA(tasa3)).isTrue();
        assertThat(tasa1.esIgualA(tasa2)).isFalse();
    }
    
    @Test
    @DisplayName("debería redondear a 2 decimales")
    void deberiaRedondearA2Decimales() {
        // Given
        BigDecimal valorConMasDecimales = new BigDecimal("15.56789");
        
        // When
        TasaInteres tasa = TasaInteres.of(valorConMasDecimales);
        
        // Then
        assertThat(tasa.valor()).isEqualTo(new BigDecimal("15.57"));
    }
    
    @Test
    @DisplayName("debería aceptar tasa de interés del 100%")
    void deberiaAceptarTasaInteresDel100() {
        // When
        TasaInteres tasa = TasaInteres.of("100.0");
        
        // Then
        assertThat(tasa.valor()).isEqualTo(new BigDecimal("100.00"));
    }
    
    @Test
    @DisplayName("debería aceptar tasa de interés cero")
    void deberiaAceptarTasaInteresCero() {
        // Then
        assertThatThrownBy(() -> TasaInteres.of("0.0"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La tasa de interés no puede ser negativa o cero");
    }
}
