package co.com.crediya.solicitudes.model.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Value Object Email")
class EmailTest {
    
    @ParameterizedTest
    @ValueSource(strings = {
        "usuario@example.com",
        "test.email@domain.co",
        "user123@test.org",
        "a@b.c"
    })
    @DisplayName("Debería crear email válido")
    void deberiaCrearEmailValido(String emailValue) {
        // When
        Email email = Email.of(emailValue);
        
        // Then
        assertThat(email.value()).isEqualTo(emailValue);
    }
    
    @Test
    @DisplayName("Debería rechazar email nulo")
    void deberiaRechazarEmailNulo() {
        // When & Then
        assertThatThrownBy(() -> Email.of(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("El email no puede ser nulo o vacío");
    }
    
    @Test
    @DisplayName("Debería rechazar email vacío")
    void deberiaRechazarEmailVacio() {
        // When & Then
        assertThatThrownBy(() -> Email.of(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("El email no puede ser nulo o vacío");
    }
    
    @Test
    @DisplayName("Debería rechazar email con más de 254 caracteres")
    void deberiaRechazarEmailMuyLargo() {
        // Given
        String emailLargo = "a".repeat(250) + "@b.com";
        
        // When & Then
        assertThatThrownBy(() -> Email.of(emailLargo))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("El email no puede tener más de 254 caracteres");
    }
    
    @Test
    @DisplayName("Debería obtener dominio correctamente")
    void deberiaObtenerDominio() {
        // Given
        Email email = Email.of("usuario@example.com");
        
        // When
        String dominio = email.domain();
        
        // Then
        assertThat(dominio).isEqualTo("example.com");
    }
    
    @Test
    @DisplayName("Debería obtener parte local correctamente")
    void deberiaObtenerParteLocal() {
        // Given
        Email email = Email.of("usuario@example.com");
        
        // When
        String parteLocal = email.localPart();
        
        // Then
        assertThat(parteLocal).isEqualTo("usuario");
    }
    
    @Test
    @DisplayName("Debería verificar pertenencia a dominio")
    void deberiaVerificarPertenenciaADominio() {
        // Given
        Email email = Email.of("usuario@example.com");
        
        // When & Then
        assertThat(email.belongsToDomain("example.com")).isTrue();
        assertThat(email.belongsToDomain("otro.com")).isFalse();
        assertThat(email.belongsToDomain("EXAMPLE.COM")).isTrue(); // Case insensitive
    }

}

