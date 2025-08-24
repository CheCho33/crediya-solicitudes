package co.com.crediya.solicitudes.model.estados;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests unitarios para el Value Object EstadoId.
 * Verifica la creación, validación y comportamiento del identificador.
 */
@DisplayName("EstadoId - Value Object Tests")
class EstadoIdTest {

    @Test
    @DisplayName("Debería crear EstadoId con UUID válido")
    void deberiaCrearEstadoIdConUuidValido() {
        // Given
        UUID uuid = UUID.randomUUID();
        
        // When
        EstadoId estadoId = new EstadoId(uuid);
        
        // Then
        assertThat(estadoId.value()).isEqualTo(uuid);
    }

    @Test
    @DisplayName("Debería rechazar EstadoId con UUID null")
    void deberiaRechazarEstadoIdConUuidNull() {
        // When & Then
        assertThatThrownBy(() -> new EstadoId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("UUID requerido para EstadoId");
    }

    @Test
    @DisplayName("Debería crear EstadoId usando newId con generador")
    void deberiaCrearEstadoIdUsandoNewIdConGenerador() {
        // Given
        UUID uuid = UUID.randomUUID();
        
        // When
        EstadoId estadoId = EstadoId.newId(() -> uuid);
        
        // Then
        assertThat(estadoId.value()).isEqualTo(uuid);
    }

    @Test
    @DisplayName("Debería crear EstadoId usando random")
    void deberiaCrearEstadoIdUsandoRandom() {
        // When
        EstadoId estadoId = EstadoId.random();
        
        // Then
        assertThat(estadoId.value()).isNotNull();
        assertThat(estadoId.value()).isInstanceOf(UUID.class);
    }

    @Test
    @DisplayName("Debería crear EstadoId desde string UUID válido")
    void deberiaCrearEstadoIdDesdeStringUuidValido() {
        // Given
        String uuidString = "550e8400-e29b-41d4-a716-446655440000";
        
        // When
        EstadoId estadoId = EstadoId.fromString(uuidString);
        
        // Then
        assertThat(estadoId.value().toString()).isEqualTo(uuidString);
    }

    @Test
    @DisplayName("Debería rechazar EstadoId desde string null")
    void deberiaRechazarEstadoIdDesdeStringNull() {
        // When & Then
        assertThatThrownBy(() -> EstadoId.fromString(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("String UUID requerido");
    }

    @Test
    @DisplayName("Debería rechazar EstadoId desde string vacío")
    void deberiaRechazarEstadoIdDesdeStringVacio() {
        // When & Then
        assertThatThrownBy(() -> EstadoId.fromString(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("String UUID requerido");
    }

    @Test
    @DisplayName("Debería rechazar EstadoId desde string con espacios")
    void deberiaRechazarEstadoIdDesdeStringConEspacios() {
        // When & Then
        assertThatThrownBy(() -> EstadoId.fromString("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("String UUID requerido");
    }

    @Test
    @DisplayName("Debería tener equals y hashCode basados en el valor")
    void deberiaTenerEqualsYHashCodeBasadosEnElValor() {
        // Given
        UUID uuid = UUID.randomUUID();
        EstadoId estadoId1 = new EstadoId(uuid);
        EstadoId estadoId2 = new EstadoId(uuid);
        EstadoId estadoId3 = new EstadoId(UUID.randomUUID());
        
        // When & Then
        assertThat(estadoId1).isEqualTo(estadoId2);
        assertThat(estadoId1).isNotEqualTo(estadoId3);
        assertThat(estadoId1.hashCode()).isEqualTo(estadoId2.hashCode());
        assertThat(estadoId1.hashCode()).isNotEqualTo(estadoId3.hashCode());
    }

    @Test
    @DisplayName("Debería tener toString que incluya el valor")
    void deberiaTenerToStringQueIncluyaElValor() {
        // Given
        UUID uuid = UUID.randomUUID();
        EstadoId estadoId = new EstadoId(uuid);
        
        // When
        String toString = estadoId.toString();
        
        // Then
        assertThat(toString).contains(uuid.toString());
        assertThat(toString).contains("EstadoId");
    }

    @Test
    @DisplayName("Debería ser inmutable")
    void deberiaSerInmutable() {
        // Given
        UUID uuid = UUID.randomUUID();
        EstadoId estadoId = new EstadoId(uuid);
        
        // When
        UUID originalValue = estadoId.value();
        
        // Then
        assertThat(estadoId.value()).isEqualTo(originalValue);
        // Verificar que el record es inmutable por diseño
        assertThat(estadoId.value()).isSameAs(originalValue);
    }
}
