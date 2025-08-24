package co.com.crediya.solicitudes.model.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Value Object Plazo")
class PlazoTest {
    
    @ParameterizedTest
    @ValueSource(ints = {1, 12, 24, 60, 120})
    @DisplayName("Debería crear plazo válido")
    void deberiaCrearPlazoValido(Integer meses) {
        // When
        Plazo plazo = Plazo.of(meses);
        
        // Then
        assertThat(plazo.meses()).isEqualTo(meses);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"1", "12", "24", "60", "120"})
    @DisplayName("Debería crear plazo desde string válido")
    void deberiaCrearPlazoDesdeStringValido(String meses) {
        // When
        Plazo plazo = Plazo.of(meses);
        
        // Then
        assertThat(plazo.meses()).isEqualTo(Integer.parseInt(meses));
    }
    
    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    @DisplayName("Debería rechazar plazo menor al mínimo")
    void deberiaRechazarPlazoMenorAlMinimo(Integer meses) {
        // When & Then
        assertThatThrownBy(() -> Plazo.of(meses))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("El plazo debe ser al menos 1 mes");
    }
    
    @ParameterizedTest
    @ValueSource(ints = {121, 150, 200})
    @DisplayName("Debería rechazar plazo mayor al máximo")
    void deberiaRechazarPlazoMayorAlMaximo(Integer meses) {
        // When & Then
        assertThatThrownBy(() -> Plazo.of(meses))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("El plazo no puede exceder 120 meses");
    }
    
    @Test
    @DisplayName("Debería rechazar plazo nulo")
    void deberiaRechazarPlazoNulo() {
        // When & Then
        assertThatThrownBy(() -> Plazo.of((Integer) null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("El plazo no puede ser nulo");
    }

    @Test
    @DisplayName("Debería verificar rango válido")
    void deberiaVerificarRangoValido() {
        // Given
        Plazo plazo = Plazo.of(24);
        
        // When & Then
        assertThat(plazo.estaEnRangoValido()).isTrue();
    }
    
    @Test
    @DisplayName("Debería calcular años correctamente")
    void deberiaCalcularAnios() {
        // Given
        Plazo plazo12Meses = Plazo.of(12);
        Plazo plazo24Meses = Plazo.of(24);
        Plazo plazo6Meses = Plazo.of(6);
        
        // When & Then
        assertThat(plazo12Meses.enAnios()).isEqualTo(1.0);
        assertThat(plazo24Meses.enAnios()).isEqualTo(2.0);
        assertThat(plazo6Meses.enAnios()).isEqualTo(0.5);
    }
    
    @Test
    @DisplayName("Debería identificar plazo corto")
    void deberiaIdentificarPlazoCorto() {
        // Given
        Plazo plazoCorto = Plazo.of(6);
        Plazo plazoLargo = Plazo.of(24);
        
        // When & Then
        assertThat(plazoCorto.esCorto()).isTrue();
        assertThat(plazoLargo.esCorto()).isFalse();
    }
    
    @Test
    @DisplayName("Debería identificar plazo largo")
    void deberiaIdentificarPlazoLargo() {
        // Given
        Plazo plazoCorto = Plazo.of(24);
        Plazo plazoLargo = Plazo.of(72);
        
        // When & Then
        assertThat(plazoCorto.esLargo()).isFalse();
        assertThat(plazoLargo.esLargo()).isTrue();
    }
    
    @Test
    @DisplayName("Debería manejar límites correctamente")
    void deberiaManejarLimites() {
        // Given & When
        Plazo plazoMinimo = Plazo.of(1);
        Plazo plazoMaximo = Plazo.of(120);
        
        // Then
        assertThat(plazoMinimo.meses()).isEqualTo(1);
        assertThat(plazoMaximo.meses()).isEqualTo(120);
        assertThat(plazoMinimo.estaEnRangoValido()).isTrue();
        assertThat(plazoMaximo.estaEnRangoValido()).isTrue();
    }
}

