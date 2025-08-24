package co.com.crediya.solicitudes.r2dbc.mapper;

import co.com.crediya.solicitudes.model.estados.EstadoId;
import co.com.crediya.solicitudes.model.estados.Estados;
import co.com.crediya.solicitudes.r2dbc.model.EstadosData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests unitarios para EstadosInfraMapper.
 * 
 * Estos tests siguen las reglas de testing de mappers:
 * - Validación de conversión bidireccional
 * - Testing de casos límite y valores inválidos
 * - Cobertura completa de métodos
 * - Validación de invariantes
 */
@DisplayName("EstadosInfraMapper Tests")
class EstadosInfraMapperTest {
    
    private EstadosInfraMapper mapper;
    private UUID uuid;
    private EstadoId estadoId;
    private Estados estado;
    private EstadosData estadosData;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    
    @BeforeEach
    void setUp() {
        mapper = new EstadosInfraMapper();
        uuid = UUID.randomUUID();
        estadoId = new EstadoId(uuid);
        estado = Estados.create(estadoId, "PENDIENTE_REVISION", "Solicitud pendiente de revisión");
        fechaCreacion = LocalDateTime.now().minusDays(1);
        fechaActualizacion = LocalDateTime.now();
        estadosData = new EstadosData(uuid, "PENDIENTE_REVISION", "Solicitud pendiente de revisión", 0L, 
            fechaCreacion, fechaActualizacion, true);
    }
    
    @Test
    @DisplayName("Debería convertir Estados a EstadosData correctamente")
    void shouldConvertEstadosToEstadosData() {
        // When
        EstadosData result = mapper.toData(estado);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.idEstado()).isEqualTo(uuid);
        assertThat(result.nombre()).isEqualTo("PENDIENTE_REVISION");
        assertThat(result.descripcion()).isEqualTo("Solicitud pendiente de revisión");
        assertThat(result.version()).isEqualTo(0L);
        assertThat(result.activo()).isTrue();
    }
    
    @Test
    @DisplayName("Debería lanzar excepción cuando Estados es null")
    void shouldThrowExceptionWhenEstadosIsNull() {
        // When & Then
        assertThatThrownBy(() -> mapper.toData(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Estados no puede ser null");
    }
    
    @Test
    @DisplayName("Debería convertir EstadosData a Estados correctamente")
    void shouldConvertEstadosDataToEstados() {
        // When
        Estados result = mapper.toDomain(estadosData);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.idEstado().value()).isEqualTo(uuid);
        assertThat(result.nombre()).isEqualTo("PENDIENTE_REVISION");
        assertThat(result.descripcion()).isEqualTo("Solicitud pendiente de revisión");
        assertThat(result.version()).isEqualTo(0L);
    }
    
    @Test
    @DisplayName("Debería lanzar excepción cuando EstadosData es null")
    void shouldThrowExceptionWhenEstadosDataIsNull() {
        // When & Then
        assertThatThrownBy(() -> mapper.toDomain(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("EstadosData no puede ser null");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("Debería lanzar excepción cuando nombre está vacío")
    void shouldThrowExceptionWhenNombreIsEmpty(String nombre) {
        // Given
        EstadosData invalidData = new EstadosData(uuid, nombre, "descripción", 0L, 
            fechaCreacion, fechaActualizacion, true);
        
        // When & Then
        assertThatThrownBy(() -> mapper.toDomain(invalidData))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nombre de estado no puede ser null o vacío");
    }
    
    @Test
    @DisplayName("Debería lanzar excepción cuando descripción es null")
    void shouldThrowExceptionWhenDescripcionIsNull() {
        // Given
        EstadosData invalidData = new EstadosData(uuid, "nombre", null, 0L, 
            fechaCreacion, fechaActualizacion, true);
        
        // When & Then
        assertThatThrownBy(() -> mapper.toDomain(invalidData))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Descripción de estado no puede ser null");
    }
    
    @Test
    @DisplayName("Debería lanzar excepción cuando versión es negativa")
    void shouldThrowExceptionWhenVersionIsNegative() {
        // Given
        EstadosData invalidData = new EstadosData(uuid, "nombre", "descripción", -1L, 
            fechaCreacion, fechaActualizacion, true);
        
        // When & Then
        assertThatThrownBy(() -> mapper.toDomain(invalidData))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Versión debe ser un número no negativo");
    }
    
    @Test
    @DisplayName("Debería convertir UUID a EstadoId correctamente")
    void shouldConvertUUIDToEstadoId() {
        // When
        EstadoId result = mapper.toEstadoId(uuid);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.value()).isEqualTo(uuid);
    }
    
    @Test
    @DisplayName("Debería lanzar excepción cuando UUID es null")
    void shouldThrowExceptionWhenUUIDIsNull() {
        // When & Then
        assertThatThrownBy(() -> mapper.toEstadoId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("UUID no puede ser null");
    }
    
    @Test
    @DisplayName("Debería convertir EstadoId a UUID correctamente")
    void shouldConvertEstadoIdToUUID() {
        // When
        UUID result = mapper.toUUID(estadoId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(uuid);
    }
    
    @Test
    @DisplayName("Debería lanzar excepción cuando EstadoId es null")
    void shouldThrowExceptionWhenEstadoIdIsNull() {
        // When & Then
        assertThatThrownBy(() -> mapper.toUUID(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("EstadoId no puede ser null");
    }
    
    @Test
    @DisplayName("Debería mantener idempotencia en conversión bidireccional")
    void shouldMaintainIdempotencyInBidirectionalConversion() {
        // Given
        Estados originalEstado = estado;
        
        // When
        EstadosData estadosData = mapper.toData(originalEstado);
        Estados convertedEstado = mapper.toDomain(estadosData);
        
        // Then
        assertThat(convertedEstado.idEstado().value()).isEqualTo(originalEstado.idEstado().value());
        assertThat(convertedEstado.nombre()).isEqualTo(originalEstado.nombre());
        assertThat(convertedEstado.descripcion()).isEqualTo(originalEstado.descripcion());
        assertThat(convertedEstado.version()).isEqualTo(originalEstado.version());
    }
    
    @Test
    @DisplayName("Debería manejar estados con versión mayor a cero")
    void shouldHandleEstadosWithVersionGreaterThanZero() {
        // Given
        Estados estadoConVersion = Estados.fromPersistence(estadoId, "APROBADA", "Solicitud aprobada", 5L);
        EstadosData estadosDataConVersion = new EstadosData(uuid, "APROBADA", "Solicitud aprobada", 5L, 
            fechaCreacion, fechaActualizacion, true);
        
        // When
        EstadosData resultData = mapper.toData(estadoConVersion);
        Estados resultEstado = mapper.toDomain(estadosDataConVersion);
        
        // Then
        assertThat(resultData.version()).isEqualTo(5L);
        assertThat(resultEstado.version()).isEqualTo(5L);
    }
    
    @Test
    @DisplayName("Debería manejar estados inactivos")
    void shouldHandleInactiveEstados() {
        // Given
        EstadosData estadosDataInactivo = new EstadosData(uuid, "CANCELADA", "Solicitud cancelada", 0L, 
            fechaCreacion, fechaActualizacion, false);
        
        // When
        Estados result = mapper.toDomain(estadosDataInactivo);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.nombre()).isEqualTo("CANCELADA");
        assertThat(result.descripcion()).isEqualTo("Solicitud cancelada");
    }
    
    @Test
    @DisplayName("Debería manejar nombres con caracteres especiales")
    void shouldHandleNamesWithSpecialCharacters() {
        // Given
        String nombreEspecial = "REVISIÓN_MANUAL_2024";
        String descripcionEspecial = "Estado para revisión manual con acentos: áéíóú";
        Estados estadoEspecial = Estados.create(estadoId, nombreEspecial, descripcionEspecial);
        EstadosData estadosDataEspecial = new EstadosData(uuid, nombreEspecial, descripcionEspecial, 0L, 
            fechaCreacion, fechaActualizacion, true);
        
        // When
        EstadosData resultData = mapper.toData(estadoEspecial);
        Estados resultEstado = mapper.toDomain(estadosDataEspecial);
        
        // Then
        assertThat(resultData.nombre()).isEqualTo(nombreEspecial);
        assertThat(resultData.descripcion()).isEqualTo(descripcionEspecial);
        assertThat(resultEstado.nombre()).isEqualTo(nombreEspecial);
        assertThat(resultEstado.descripcion()).isEqualTo(descripcionEspecial);
    }
}
