package co.com.crediya.solicitudes.r2dbc.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamoId;
import co.com.crediya.solicitudes.model.valueobjects.Monto;
import co.com.crediya.solicitudes.model.valueobjects.Nombre;
import co.com.crediya.solicitudes.model.valueobjects.TasaInteres;
import co.com.crediya.solicitudes.r2dbc.model.TipoPrestamoData;

@DisplayName("TipoPrestamoInfraMapper Tests")
class TipoPrestamoInfraMapperTest {

    private TipoPrestamoInfraMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TipoPrestamoInfraMapper();
    }

    @Test
    @DisplayName("Debería convertir TipoPrestamo a TipoPrestamoData correctamente")
    void shouldConvertTipoPrestamoToTipoPrestamoData() {
        // Given
        UUID id = UUID.randomUUID();
        TipoPrestamoId tipoPrestamoId = new TipoPrestamoId(id);
        Nombre nombre = new Nombre("Libre Inversión");
        Monto montoMinimo = Monto.of(new BigDecimal("1000000.00"));
        Monto montoMaximo = Monto.of(new BigDecimal("50000000.00"));
        TasaInteres tasaInteres = TasaInteres.of(new BigDecimal("0.15"));
        long version = 2L;

        TipoPrestamo tipoPrestamo = TipoPrestamo.reconstruir(
            tipoPrestamoId, nombre, montoMinimo, montoMaximo, tasaInteres, true, version
        );

        // When
        TipoPrestamoData result = mapper.toData(tipoPrestamo);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.idTipoPrestamo()).isEqualTo(id);
        assertThat(result.nombre()).isEqualTo("Libre Inversión");
        assertThat(result.montoMinimo()).isEqualTo(new BigDecimal("1000000.00"));
        assertThat(result.montoMaximo()).isEqualTo(new BigDecimal("50000000.00"));
        assertThat(result.tasaInteresAnual()).isEqualTo(new BigDecimal("0.15"));
        assertThat(result.validacionAutomatica()).isTrue();
        assertThat(result.version()).isEqualTo(2L);
        assertThat(result.activo()).isTrue();
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando TipoPrestamo es null")
    void shouldThrowExceptionWhenTipoPrestamoIsNull() {
        assertThatThrownBy(() -> mapper.toData(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("TipoPrestamo no puede ser null");
    }

    @Test
    @DisplayName("Debería convertir TipoPrestamoData a TipoPrestamo correctamente")
    void shouldConvertTipoPrestamoDataToTipoPrestamo() {
        // Given
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        TipoPrestamoData data = new TipoPrestamoData(
            id, "Vivienda", new BigDecimal("5000000.00"), new BigDecimal("200000000.00"),
            new BigDecimal("0.12"), false, 1L, now, now, true
        );

        // When
        TipoPrestamo result = mapper.toDomain(data);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id().value()).isEqualTo(id);
        assertThat(result.nombre().valor()).isEqualTo("Vivienda");
        assertThat(result.montoMinimo().valor()).isEqualTo(new BigDecimal("5000000.00"));
        assertThat(result.montoMaximo().valor()).isEqualTo(new BigDecimal("200000000.00"));
        assertThat(result.tasaInteres().valor()).isEqualTo(new BigDecimal("0.12"));
        assertThat(result.validacionAutomatica()).isFalse();
        assertThat(result.version()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando TipoPrestamoData es null")
    void shouldThrowExceptionWhenTipoPrestamoDataIsNull() {
        assertThatThrownBy(() -> mapper.toDomain(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("TipoPrestamoData no puede ser null");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "\n"})
    @DisplayName("Debería lanzar excepción cuando nombre está vacío")
    void shouldThrowExceptionWhenNombreIsBlank(String blankNombre) {
        UUID id = UUID.randomUUID();
        TipoPrestamoData data = new TipoPrestamoData(
            id, blankNombre, new BigDecimal("1000"), new BigDecimal("5000"),
            new BigDecimal("0.15"), true, 0L, LocalDateTime.now(), LocalDateTime.now(), true
        );

        assertThatThrownBy(() -> mapper.toDomain(data))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Nombre de tipo de préstamo no puede ser null o vacío");
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando versión es negativa")
    void shouldThrowExceptionWhenVersionIsNegative() {
        UUID id = UUID.randomUUID();
        TipoPrestamoData data = new TipoPrestamoData(
            id, "Test", new BigDecimal("1000"), new BigDecimal("5000"),
            new BigDecimal("0.15"), true, -1L, LocalDateTime.now(), LocalDateTime.now(), true
        );

        assertThatThrownBy(() -> mapper.toDomain(data))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Versión debe ser un número no negativo");
    }

    @Test
    @DisplayName("Debería convertir UUID a TipoPrestamoId correctamente")
    void shouldConvertUUIDToTipoPrestamoId() {
        // Given
        UUID uuid = UUID.randomUUID();

        // When
        TipoPrestamoId result = mapper.toTipoPrestamoId(uuid);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.value()).isEqualTo(uuid);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando UUID es null")
    void shouldThrowExceptionWhenUUIDIsNull() {
        assertThatThrownBy(() -> mapper.toTipoPrestamoId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("UUID no puede ser null");
    }

    @Test
    @DisplayName("Debería convertir TipoPrestamoId a UUID correctamente")
    void shouldConvertTipoPrestamoIdToUUID() {
        // Given
        UUID uuid = UUID.randomUUID();
        TipoPrestamoId tipoPrestamoId = new TipoPrestamoId(uuid);

        // When
        UUID result = mapper.toUUID(tipoPrestamoId);

        // Then
        assertThat(result).isEqualTo(uuid);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando TipoPrestamoId es null")
    void shouldThrowExceptionWhenTipoPrestamoIdIsNull() {
        assertThatThrownBy(() -> mapper.toUUID(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("TipoPrestamoId no puede ser null");
    }

    @Test
    @DisplayName("Debería convertir Monto a BigDecimal correctamente")
    void shouldConvertMontoToBigDecimal() {
        // Given
        BigDecimal valor = new BigDecimal("1500000.00");
        Monto monto = Monto.of(valor);

        // When
        BigDecimal result = mapper.toBigDecimal(monto);

        // Then
        assertThat(result).isEqualTo(valor);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando Monto es null")
    void shouldThrowExceptionWhenMontoIsNull() {
        assertThatThrownBy(() -> mapper.toBigDecimal(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Monto no puede ser null");
    }

    @Test
    @DisplayName("Debería convertir BigDecimal a Monto correctamente")
    void shouldConvertBigDecimalToMonto() {
        // Given
        BigDecimal valor = new BigDecimal("2500000.00");

        // When
        Monto result = mapper.toMonto(valor);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.valor()).isEqualTo(valor);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando BigDecimal es null")
    void shouldThrowExceptionWhenBigDecimalIsNull() {
        assertThatThrownBy(() -> mapper.toMonto(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Valor no puede ser null");
    }

    @Test
    @DisplayName("Debería ser idempotente en conversión bidireccional")
    void shouldBeIdempotentInBidirectionalConversion() {
        // Given
        UUID id = UUID.randomUUID();
        TipoPrestamoId tipoPrestamoId = new TipoPrestamoId(id);
        Nombre nombre = new Nombre("Test Préstamo");
        Monto montoMinimo = Monto.of(new BigDecimal("1000000"));
        Monto montoMaximo = Monto.of(new BigDecimal("50000000"));
        TasaInteres tasaInteres = TasaInteres.of(new BigDecimal("0.015"));
        long version = 1L;

        TipoPrestamo original = TipoPrestamo.reconstruir(
            tipoPrestamoId, nombre, montoMinimo, montoMaximo, tasaInteres, true, version
        );

        // When
        TipoPrestamoData data = mapper.toData(original);
        TipoPrestamo reconstructed = mapper.toDomain(data);

        // Then
        assertThat(reconstructed.id().value()).isEqualTo(original.id().value());
        assertThat(reconstructed.nombre().valor()).isEqualTo(original.nombre().valor());
        assertThat(reconstructed.montoMinimo().valor()).isEqualTo(original.montoMinimo().valor());
        assertThat(reconstructed.montoMaximo().valor()).isEqualTo(original.montoMaximo().valor());
        assertThat(reconstructed.tasaInteres().valor()).isEqualTo(original.tasaInteres().valor());
        assertThat(reconstructed.validacionAutomatica()).isEqualTo(original.validacionAutomatica());
        assertThat(reconstructed.version()).isEqualTo(original.version());
    }
}
