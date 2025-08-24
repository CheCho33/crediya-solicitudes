package co.com.crediya.solicitudes.model.tipoprestamo;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TipoPrestamoId")
class TipoPrestamoIdTest {
    
    @Test
    @DisplayName("debería crear identificador válido")
    void deberiaCrearIdentificadorValido() {
        // Given
        UUID uuid = UUID.randomUUID();
        
        // When
        TipoPrestamoId id = new TipoPrestamoId(uuid);
        
        // Then
        assertThat(id.value()).isEqualTo(uuid);
    }
    
    @Test
    @DisplayName("debería rechazar UUID nulo")
    void deberiaRechazarUuidNulo() {
        // When & Then
        assertThatThrownBy(() -> new TipoPrestamoId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("UUID requerido para TipoPrestamoId");
    }
    
    @Test
    @DisplayName("debería crear identificador con generador")
    void deberiaCrearIdentificadorConGenerador() {
        // Given
        UUID uuidEspecifico = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        
        // When
        TipoPrestamoId id = TipoPrestamoId.newId(() -> uuidEspecifico);
        
        // Then
        assertThat(id.value()).isEqualTo(uuidEspecifico);
    }
    
    @Test
    @DisplayName("debería crear identificador aleatorio")
    void deberiaCrearIdentificadorAleatorio() {
        // When
        TipoPrestamoId id1 = TipoPrestamoId.random();
        TipoPrestamoId id2 = TipoPrestamoId.random();
        
        // Then
        assertThat(id1.value()).isNotNull();
        assertThat(id2.value()).isNotNull();
        assertThat(id1.value()).isNotEqualTo(id2.value());
    }
    
    @Test
    @DisplayName("debería ser igual a otro con mismo UUID")
    void deberiaSerIgualAOtroConMismoUuid() {
        // Given
        UUID uuid = UUID.randomUUID();
        TipoPrestamoId id1 = new TipoPrestamoId(uuid);
        TipoPrestamoId id2 = new TipoPrestamoId(uuid);
        
        // When & Then
        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }
    
    @Test
    @DisplayName("debería ser diferente a otro con UUID diferente")
    void deberiaSerDiferenteAOtroConUuidDiferente() {
        // Given
        TipoPrestamoId id1 = TipoPrestamoId.random();
        TipoPrestamoId id2 = TipoPrestamoId.random();
        
        // When & Then
        assertThat(id1).isNotEqualTo(id2);
        assertThat(id1.hashCode()).isNotEqualTo(id2.hashCode());
    }
    
    @Test
    @DisplayName("debería tener representación de string consistente")
    void deberiaTenerRepresentacionDeStringConsistente() {
        // Given
        UUID uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        TipoPrestamoId id = new TipoPrestamoId(uuid);
        
        // When
        String stringRepresentation = id.toString();
        
        // Then
        assertThat(stringRepresentation).contains("550e8400-e29b-41d4-a716-446655440000");
    }
}
