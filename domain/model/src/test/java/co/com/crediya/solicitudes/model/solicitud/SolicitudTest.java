package co.com.crediya.solicitudes.model.solicitud;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.com.crediya.solicitudes.model.estados.EstadoId;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamoId;
import co.com.crediya.solicitudes.model.valueobjects.Email;
import co.com.crediya.solicitudes.model.valueobjects.Monto;
import co.com.crediya.solicitudes.model.valueobjects.Plazo;

@DisplayName("Entidad Solicitud")
class SolicitudTest {
    
    private static final SolicitudId ID_VALIDO = SolicitudId.random();
    private static final Monto MONTO_VALIDO = Monto.of(BigDecimal.valueOf(5000000));
    private static final Plazo PLAZO_VALIDO = Plazo.of(24);
    private static final Email EMAIL_VALIDO = Email.of("cliente@example.com");
    private static final EstadoId ESTADO_VALIDO = EstadoId.random();
    private static final TipoPrestamoId TIPO_PRESTAMO_VALIDO = TipoPrestamoId.random();
    
    @Test
    @DisplayName("Debería crear una solicitud válida")
    void deberiaCrearSolicitudValida() {
        // When
        Solicitud solicitud = Solicitud.create(
            ID_VALIDO, MONTO_VALIDO, PLAZO_VALIDO, EMAIL_VALIDO, 
            ESTADO_VALIDO, TIPO_PRESTAMO_VALIDO
        );
        
        // Then
        assertThat(solicitud.id()).isEqualTo(ID_VALIDO);
        assertThat(solicitud.monto()).isEqualTo(MONTO_VALIDO);
        assertThat(solicitud.plazo()).isEqualTo(PLAZO_VALIDO);
        assertThat(solicitud.email()).isEqualTo(EMAIL_VALIDO);
        assertThat(solicitud.idEstado()).isEqualTo(ESTADO_VALIDO);
        assertThat(solicitud.idTipoPrestamo()).isEqualTo(TIPO_PRESTAMO_VALIDO);
        assertThat(solicitud.version()).isEqualTo(0L);
    }
    
    @Test
    @DisplayName("Debería crear una solicitud desde datos existentes")
    void deberiaCrearSolicitudDesdeDatosExistentes() {
        // Given
        long version = 5L;
        
        // When
        Solicitud solicitud = Solicitud.from(
            ID_VALIDO, MONTO_VALIDO, PLAZO_VALIDO, EMAIL_VALIDO, 
            ESTADO_VALIDO, TIPO_PRESTAMO_VALIDO, version
        );
        
        // Then
        assertThat(solicitud.id()).isEqualTo(ID_VALIDO);
        assertThat(solicitud.monto()).isEqualTo(MONTO_VALIDO);
        assertThat(solicitud.plazo()).isEqualTo(PLAZO_VALIDO);
        assertThat(solicitud.email()).isEqualTo(EMAIL_VALIDO);
        assertThat(solicitud.idEstado()).isEqualTo(ESTADO_VALIDO);
        assertThat(solicitud.idTipoPrestamo()).isEqualTo(TIPO_PRESTAMO_VALIDO);
        assertThat(solicitud.version()).isEqualTo(version);
    }
    
    @Test
    @DisplayName("Debería marcar como persistida con nueva versión")
    void deberiaMarcarComoPersistida() {
        // Given
        Solicitud solicitud = Solicitud.create(
            ID_VALIDO, MONTO_VALIDO, PLAZO_VALIDO, EMAIL_VALIDO, 
            ESTADO_VALIDO, TIPO_PRESTAMO_VALIDO
        );
        long nuevaVersion = 1L;
        
        // When
        solicitud.markPersisted(nuevaVersion);
        
        // Then
        assertThat(solicitud.version()).isEqualTo(nuevaVersion);
    }
    
    @Test
    @DisplayName("Debería rechazar marcar como persistida con versión menor o igual")
    void deberiaRechazarVersionMenorOIgual() {
        // Given
        Solicitud solicitud = Solicitud.create(
            ID_VALIDO, MONTO_VALIDO, PLAZO_VALIDO, EMAIL_VALIDO, 
            ESTADO_VALIDO, TIPO_PRESTAMO_VALIDO
        );
        
        // When & Then
        assertThatThrownBy(() -> solicitud.markPersisted(0L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("La nueva versión debe ser mayor que la actual");
            
        assertThatThrownBy(() -> solicitud.markPersisted(-1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("La nueva versión debe ser mayor que la actual");
    }
    
    @Test
    @DisplayName("Debería rechazar crear solicitud con ID nulo")
    void deberiaRechazarIdNulo() {
        // When & Then
        assertThatThrownBy(() -> Solicitud.create(
            null, MONTO_VALIDO, PLAZO_VALIDO, EMAIL_VALIDO, 
            ESTADO_VALIDO, TIPO_PRESTAMO_VALIDO
        )).isInstanceOf(IllegalStateException.class)
          .hasMessage("El identificador de solicitud no puede ser nulo");
    }
    
    @Test
    @DisplayName("Debería rechazar crear solicitud con monto nulo")
    void deberiaRechazarMontoNulo() {
        // When & Then
        assertThatThrownBy(() -> Solicitud.create(
            ID_VALIDO, null, PLAZO_VALIDO, EMAIL_VALIDO, 
            ESTADO_VALIDO, TIPO_PRESTAMO_VALIDO
        )).isInstanceOf(IllegalStateException.class)
          .hasMessage("El monto no puede ser nulo");
    }
    
    @Test
    @DisplayName("Debería rechazar crear solicitud con plazo nulo")
    void deberiaRechazarPlazoNulo() {
        // When & Then
        assertThatThrownBy(() -> Solicitud.create(
            ID_VALIDO, MONTO_VALIDO, null, EMAIL_VALIDO, 
            ESTADO_VALIDO, TIPO_PRESTAMO_VALIDO
        )).isInstanceOf(IllegalStateException.class)
          .hasMessage("El plazo no puede ser nulo");
    }
    
    @Test
    @DisplayName("Debería rechazar crear solicitud con email nulo")
    void deberiaRechazarEmailNulo() {
        // When & Then
        assertThatThrownBy(() -> Solicitud.create(
            ID_VALIDO, MONTO_VALIDO, PLAZO_VALIDO, null, 
            ESTADO_VALIDO, TIPO_PRESTAMO_VALIDO
        )).isInstanceOf(IllegalStateException.class)
          .hasMessage("El email no puede ser nulo");
    }
    
    @Test
    @DisplayName("Debería rechazar crear solicitud con estado nulo")
    void deberiaRechazarEstadoNulo() {
        // When & Then
        assertThatThrownBy(() -> Solicitud.create(
            ID_VALIDO, MONTO_VALIDO, PLAZO_VALIDO, EMAIL_VALIDO, 
            null, TIPO_PRESTAMO_VALIDO
        )).isInstanceOf(IllegalStateException.class)
          .hasMessage("El identificador de estado no puede ser nulo");
    }
    
    @Test
    @DisplayName("Debería rechazar crear solicitud con tipo de préstamo nulo")
    void deberiaRechazarTipoPrestamoNulo() {
        // When & Then
        assertThatThrownBy(() -> Solicitud.create(
            ID_VALIDO, MONTO_VALIDO, PLAZO_VALIDO, EMAIL_VALIDO, 
            ESTADO_VALIDO, null
        )).isInstanceOf(IllegalStateException.class)
          .hasMessage("El identificador de tipo de préstamo no puede ser nulo");
    }
    
    @Test
    @DisplayName("Debería rechazar crear solicitud con versión negativa")
    void deberiaRechazarVersionNegativa() {
        // When & Then
        assertThatThrownBy(() -> Solicitud.from(
            ID_VALIDO, MONTO_VALIDO, PLAZO_VALIDO, EMAIL_VALIDO, 
            ESTADO_VALIDO, TIPO_PRESTAMO_VALIDO, -1L
        )).isInstanceOf(IllegalStateException.class)
          .hasMessage("La versión no puede ser negativa");
    }
}

