package co.com.crediya.solicitudes.model.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Nombre")
class NombreTest {
    
    @Test
    @DisplayName("debería crear nombre válido")
    void deberiaCrearNombreValido() {
        // When
        Nombre nombre = Nombre.of("Préstamo Personal");
        
        // Then
        assertThat(nombre.valor()).isEqualTo("Préstamo Personal");
    }
    
    @Test
    @DisplayName("debería crear nombre con acentos")
    void deberiaCrearNombreConAcentos() {
        // When
        Nombre nombre = Nombre.of("Préstamo Vehícular");
        
        // Then
        assertThat(nombre.valor()).isEqualTo("Préstamo Vehícular");
    }
    
    @Test
    @DisplayName("debería crear nombre con ñ")
    void deberiaCrearNombreConN() {
        // When
        Nombre nombre = Nombre.of("Préstamo Pequeña Empresa");
        
        // Then
        assertThat(nombre.valor()).isEqualTo("Préstamo Pequeña Empresa");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "null"})
    @DisplayName("debería rechazar nombre vacío o nulo")
    void deberiaRechazarNombreVacioONulo(String valor) {
        // When & Then
        if ("null".equals(valor)) {
            assertThatThrownBy(() -> Nombre.of((String) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El nombre no puede ser nulo o vacío");
        } else {
            assertThatThrownBy(() -> Nombre.of(valor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El nombre no puede ser nulo o vacío");
        }
    }
    
    @Test
    @DisplayName("debería rechazar nombre muy largo")
    void deberiaRechazarNombreMuyLargo() {
        // Given
        String nombreLargo = "A".repeat(101);
        
        // When & Then
        assertThatThrownBy(() -> Nombre.of(nombreLargo))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("El nombre no puede tener más de 100 caracteres");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"Préstamo123", "Préstamo-Personal", "Préstamo_Personal", "Préstamo@Personal"})
    @DisplayName("debería rechazar nombre con caracteres inválidos")
    void deberiaRechazarNombreConCaracteresInvalidos(String valor) {
        // When & Then
        assertThatThrownBy(() -> Nombre.of(valor))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("El nombre solo puede contener letras y espacios");
    }
    
    @Test
    @DisplayName("debería convertir a mayúsculas")
    void deberiaConvertirAMayusculas() {
        // Given
        Nombre nombre = Nombre.of("Préstamo Personal");
        
        // When
        String mayusculas = nombre.enMayusculas();
        
        // Then
        assertThat(mayusculas).isEqualTo("PRÉSTAMO PERSONAL");
    }
    
    @Test
    @DisplayName("debería convertir a minúsculas")
    void deberiaConvertirAMinusculas() {
        // Given
        Nombre nombre = Nombre.of("PRÉSTAMO PERSONAL");
        
        // When
        String minusculas = nombre.enMinusculas();
        
        // Then
        assertThat(minusculas).isEqualTo("préstamo personal");
    }
    
    @Test
    @DisplayName("debería convertir primera letra a mayúscula")
    void deberiaConvertirPrimeraLetraAMayuscula() {
        // Given
        Nombre nombre = Nombre.of("préstamo personal");
        
        // When
        String conPrimeraMayuscula = nombre.conPrimeraMayuscula();
        
        // Then
        assertThat(conPrimeraMayuscula).isEqualTo("Préstamo personal");
    }
    
    @Test
    @DisplayName("debería manejar nombre vacío en conPrimeraMayuscula")
    void deberiaManejarNombreVacioEnConPrimeraMayuscula() {
        // Given
        Nombre nombre = Nombre.of("A");
        
        // When
        String conPrimeraMayuscula = nombre.conPrimeraMayuscula();
        
        // Then
        assertThat(conPrimeraMayuscula).isEqualTo("A");
    }
    
    @Test
    @DisplayName("debería verificar si contiene texto")
    void deberiaVerificarSiContieneTexto() {
        // Given
        Nombre nombre = Nombre.of("Préstamo Personal");
        
        // When & Then
        assertThat(nombre.contiene("Préstamo")).isTrue();
        assertThat(nombre.contiene("personal")).isTrue(); // case-insensitive
        assertThat(nombre.contiene("Vehicular")).isFalse();
    }
    
    @Test
    @DisplayName("debería aceptar nombre de exactamente 100 caracteres")
    void deberiaAceptarNombreDeExactamente100Caracteres() {
        // Given
        String nombre100Caracteres = "A".repeat(100);
        
        // When
        Nombre nombre = Nombre.of(nombre100Caracteres);
        
        // Then
        assertThat(nombre.valor()).isEqualTo(nombre100Caracteres);
    }
    
    @Test
    @DisplayName("debería aceptar nombre con múltiples espacios")
    void deberiaAceptarNombreConMultiplesEspacios() {
        // When
        Nombre nombre = Nombre.of("Préstamo   Personal   Vehicular");
        
        // Then
        assertThat(nombre.valor()).isEqualTo("Préstamo   Personal   Vehicular");
    }
}
