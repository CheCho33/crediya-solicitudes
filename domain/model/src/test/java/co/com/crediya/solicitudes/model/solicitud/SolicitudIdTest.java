package co.com.crediya.solicitudes.model.solicitud;

import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Identificador SolicitudId")
class SolicitudIdTest {
    
    @Test
    @DisplayName("Debería crear SolicitudId con UUID válido")
    void deberiaCrearConUuidValido() {
        // Given
        UUID uuid = UUID.randomUUID();
        
        // When
        SolicitudId solicitudId = new SolicitudId(uuid);
        
        // Then
        assertThat(solicitudId.value()).isEqualTo(uuid);
    }
    
    @Test
    @DisplayName("Debería rechazar UUID nulo")
    void deberiaRechazarUuidNulo() {
        // When & Then
        assertThatThrownBy(() -> new SolicitudId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("UUID requerido para SolicitudId");
    }
    
    @Test
    @DisplayName("Debería crear SolicitudId aleatorio")
    void deberiaCrearAleatorio() {
        // When
        SolicitudId solicitudId = SolicitudId.random();
        
        // Then
        assertThat(solicitudId.value()).isNotNull();
        assertThat(solicitudId.value()).isInstanceOf(UUID.class);
    }
    
    @Test
    @DisplayName("Debería crear SolicitudId con generador personalizado")
    void deberiaCrearConGeneradorPersonalizado() {
        // Given
        UUID uuidEspecifico = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Supplier<UUID> generador = () -> uuidEspecifico;
        
        // When
        SolicitudId solicitudId = SolicitudId.newId(generador);
        
        // Then
        assertThat(solicitudId.value()).isEqualTo(uuidEspecifico);
    }
    
    @Test
    @DisplayName("Debería crear SolicitudId desde string UUID válido")
    void deberiaCrearDesdeStringValido() {
        // Given
        String uuidString = "550e8400-e29b-41d4-a716-446655440000";
        
        // When
        SolicitudId solicitudId = SolicitudId.fromString(uuidString);
        
        // Then
        assertThat(solicitudId.value()).isEqualTo(UUID.fromString(uuidString));
    }
    
    @Test
    @DisplayName("Debería rechazar string UUID nulo")
    void deberiaRechazarStringUuidNulo() {
        // When & Then
        assertThatThrownBy(() -> SolicitudId.fromString(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("String UUID requerido");
    }
    
    @Test
    @DisplayName("Debería rechazar string UUID vacío")
    void deberiaRechazarStringUuidVacio() {
        // When & Then
        assertThatThrownBy(() -> SolicitudId.fromString(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("String UUID requerido");
    }
    
    @Test
    @DisplayName("Debería rechazar string UUID con espacios")
    void deberiaRechazarStringUuidConEspacios() {
        // When & Then
        assertThatThrownBy(() -> SolicitudId.fromString("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("String UUID requerido");
    }
    
    @Test
    @DisplayName("Debería rechazar string UUID inválido")
    void deberiaRechazarStringUuidInvalido() {
        // When & Then
        assertThatThrownBy(() -> SolicitudId.fromString("uuid-invalido"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Formato de UUID inválido: uuid-invalido");
    }
    
    @Test
    @DisplayName("Debería ser igual a otro SolicitudId con mismo UUID")
    void deberiaSerIgualConMismoUuid() {
        // Given
        UUID uuid = UUID.randomUUID();
        SolicitudId id1 = new SolicitudId(uuid);
        SolicitudId id2 = new SolicitudId(uuid);
        
        // When & Then
        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }
    
    @Test
    @DisplayName("Debería ser diferente a otro SolicitudId con UUID diferente")
    void deberiaSerDiferenteConUuidDiferente() {
        // Given
        SolicitudId id1 = SolicitudId.random();
        SolicitudId id2 = SolicitudId.random();
        
        // When & Then
        assertThat(id1).isNotEqualTo(id2);
    }
    
    @Test
    @DisplayName("Debería tener representación string consistente")
    void deberiaTenerRepresentacionStringConsistente() {
        // Given
        UUID uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        SolicitudId solicitudId = new SolicitudId(uuid);
        
        // When
        String representacion = solicitudId.toString();
        
        // Then
        assertThat(representacion).contains("550e8400-e29b-41d4-a716-446655440000");
    }
}

