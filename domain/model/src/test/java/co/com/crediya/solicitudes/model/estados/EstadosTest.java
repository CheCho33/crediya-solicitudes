package co.com.crediya.solicitudes.model.estados;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests unitarios para la entidad Estados.
 * Verifica la creación, validación, mutaciones y comportamiento de la entidad.
 */
@DisplayName("Estados - Entity Tests")
class EstadosTest {

    private static final String NOMBRE_VALIDO = "PENDIENTE_REVISION";
    private static final String DESCRIPCION_VALIDA = "Solicitud pendiente de revisión por asesor";

    @Test
    @DisplayName("Debería crear Estados con datos válidos")
    void deberiaCrearEstadosConDatosValidos() {
        // Given
        EstadoId idEstado = EstadoId.random();
        
        // When
        Estados estados = Estados.create(idEstado, NOMBRE_VALIDO, DESCRIPCION_VALIDA);
        
        // Then
        assertThat(estados.idEstado()).isEqualTo(idEstado);
        assertThat(estados.nombre()).isEqualTo(NOMBRE_VALIDO);
        assertThat(estados.descripcion()).isEqualTo(DESCRIPCION_VALIDA);
        assertThat(estados.version()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Debería rechazar Estados con idEstado null")
    void deberiaRechazarEstadosConIdEstadoNull() {
        // When & Then
        assertThatThrownBy(() -> Estados.create(null, NOMBRE_VALIDO, DESCRIPCION_VALIDA))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Identificador de estado requerido");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("Debería rechazar Estados con nombre inválido")
    void deberiaRechazarEstadosConNombreInvalido(String nombreInvalido) {
        // Given
        EstadoId idEstado = EstadoId.random();
        
        // When & Then
        assertThatThrownBy(() -> Estados.create(idEstado, nombreInvalido, DESCRIPCION_VALIDA))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Nombre de estado requerido");
    }

    @Test
    @DisplayName("Debería rechazar Estados con descripción null")
    void deberiaRechazarEstadosConDescripcionNull() {
        // Given
        EstadoId idEstado = EstadoId.random();
        
        // When & Then
        assertThatThrownBy(() -> Estados.create(idEstado, NOMBRE_VALIDO, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Descripción de estado requerida");
    }

    @Test
    @DisplayName("Debería crear Estados desde persistencia con versión")
    void deberiaCrearEstadosDesdePersistenciaConVersion() {
        // Given
        EstadoId idEstado = EstadoId.random();
        long version = 5L;
        
        // When
        Estados estados = Estados.fromPersistence(idEstado, NOMBRE_VALIDO, DESCRIPCION_VALIDA, version);
        
        // Then
        assertThat(estados.idEstado()).isEqualTo(idEstado);
        assertThat(estados.nombre()).isEqualTo(NOMBRE_VALIDO);
        assertThat(estados.descripcion()).isEqualTo(DESCRIPCION_VALIDA);
        assertThat(estados.version()).isEqualTo(version);
    }

    @Test
    @DisplayName("Debería rechazar Estados desde persistencia con versión negativa")
    void deberiaRechazarEstadosDesdePersistenciaConVersionNegativa() {
        // Given
        EstadoId idEstado = EstadoId.random();
        long versionNegativa = -1L;
        
        // When & Then
        assertThatThrownBy(() -> Estados.fromPersistence(idEstado, NOMBRE_VALIDO, DESCRIPCION_VALIDA, versionNegativa))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Versión no puede ser negativa");
    }

    @Test
    @DisplayName("Debería actualizar descripción correctamente")
    void deberiaActualizarDescripcionCorrectamente() {
        // Given
        Estados estados = Estados.create(EstadoId.random(), NOMBRE_VALIDO, DESCRIPCION_VALIDA);
        String nuevaDescripcion = "Nueva descripción del estado";
        
        // When
        Estados estadosActualizado = estados.actualizarDescripcion(nuevaDescripcion);
        
        // Then
        assertThat(estadosActualizado.descripcion()).isEqualTo(nuevaDescripcion);
        assertThat(estadosActualizado.nombre()).isEqualTo(estados.nombre());
        assertThat(estadosActualizado.idEstado()).isEqualTo(estados.idEstado());
        assertThat(estadosActualizado.version()).isEqualTo(estados.version());
        // Verificar inmutabilidad
        assertThat(estados.descripcion()).isEqualTo(DESCRIPCION_VALIDA);
    }

    @Test
    @DisplayName("Debería rechazar actualización de descripción con null")
    void deberiaRechazarActualizacionDeDescripcionConNull() {
        // Given
        Estados estados = Estados.create(EstadoId.random(), NOMBRE_VALIDO, DESCRIPCION_VALIDA);
        
        // When & Then
        assertThatThrownBy(() -> estados.actualizarDescripcion(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Descripción no puede ser null");
    }

    @Test
    @DisplayName("Debería actualizar nombre correctamente")
    void deberiaActualizarNombreCorrectamente() {
        // Given
        Estados estados = Estados.create(EstadoId.random(), NOMBRE_VALIDO, DESCRIPCION_VALIDA);
        String nuevoNombre = "APROBADA";
        
        // When
        Estados estadosActualizado = estados.actualizarNombre(nuevoNombre);
        
        // Then
        assertThat(estadosActualizado.nombre()).isEqualTo(nuevoNombre);
        assertThat(estadosActualizado.descripcion()).isEqualTo(estados.descripcion());
        assertThat(estadosActualizado.idEstado()).isEqualTo(estados.idEstado());
        assertThat(estadosActualizado.version()).isEqualTo(estados.version());
        // Verificar inmutabilidad
        assertThat(estados.nombre()).isEqualTo(NOMBRE_VALIDO);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("Debería rechazar actualización de nombre con valor inválido")
    void deberiaRechazarActualizacionDeNombreConValorInvalido(String nombreInvalido) {
        // Given
        Estados estados = Estados.create(EstadoId.random(), NOMBRE_VALIDO, DESCRIPCION_VALIDA);
        
        // When & Then
        assertThatThrownBy(() -> estados.actualizarNombre(nombreInvalido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nombre no puede ser null o vacío");
    }

    @Test
    @DisplayName("Debería marcar como persistida con nueva versión")
    void deberiaMarcarComoPersistidaConNuevaVersion() {
        // Given
        Estados estados = Estados.create(EstadoId.random(), NOMBRE_VALIDO, DESCRIPCION_VALIDA);
        long nuevaVersion = 10L;
        
        // When
        Estados estadosPersistido = estados.markPersisted(nuevaVersion);
        
        // Then
        assertThat(estadosPersistido.version()).isEqualTo(nuevaVersion);
        assertThat(estadosPersistido.nombre()).isEqualTo(estados.nombre());
        assertThat(estadosPersistido.descripcion()).isEqualTo(estados.descripcion());
        assertThat(estadosPersistido.idEstado()).isEqualTo(estados.idEstado());
        // Verificar inmutabilidad
        assertThat(estados.version()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Debería rechazar marcar como persistida con versión menor o igual")
    void deberiaRechazarMarcarComoPersistidaConVersionMenorOIgual() {
        // Given
        Estados estados = Estados.create(EstadoId.random(), NOMBRE_VALIDO, DESCRIPCION_VALIDA);
        
        // When & Then
        assertThatThrownBy(() -> estados.markPersisted(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nueva versión debe ser mayor que la actual");
        
        assertThatThrownBy(() -> estados.markPersisted(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nueva versión debe ser mayor que la actual");
    }

    @Test
    @DisplayName("Debería verificar nombre correctamente")
    void deberiaVerificarNombreCorrectamente() {
        // Given
        Estados estados = Estados.create(EstadoId.random(), NOMBRE_VALIDO, DESCRIPCION_VALIDA);
        
        // When & Then
        assertThat(estados.tieneNombre(NOMBRE_VALIDO)).isTrue();
        assertThat(estados.tieneNombre(NOMBRE_VALIDO.toLowerCase())).isTrue();
        assertThat(estados.tieneNombre(NOMBRE_VALIDO.toUpperCase())).isTrue();
        assertThat(estados.tieneNombre("OTRO_NOMBRE")).isFalse();
        assertThat(estados.tieneNombre(null)).isFalse();
    }

    @Test
    @DisplayName("Debería verificar contenido en descripción correctamente")
    void deberiaVerificarContenidoEnDescripcionCorrectamente() {
        // Given
        Estados estados = Estados.create(EstadoId.random(), NOMBRE_VALIDO, DESCRIPCION_VALIDA);
        
        // When & Then
        assertThat(estados.descripcionContiene("revisión")).isTrue();
        assertThat(estados.descripcionContiene("REVISIÓN")).isTrue();
        assertThat(estados.descripcionContiene("asesor")).isTrue();
        assertThat(estados.descripcionContiene("pendiente")).isTrue();
        assertThat(estados.descripcionContiene("no_existe")).isFalse();
        assertThat(estados.descripcionContiene(null)).isFalse();
    }

    @Test
    @DisplayName("Debería tener equals y hashCode basados en idEstado")
    void deberiaTenerEqualsYHashCodeBasadosEnIdEstado() {
        // Given
        EstadoId idEstado = EstadoId.random();
        Estados estados1 = Estados.create(idEstado, NOMBRE_VALIDO, DESCRIPCION_VALIDA);
        Estados estados2 = Estados.create(idEstado, "OTRO_NOMBRE", "OTRA_DESCRIPCION");
        Estados estados3 = Estados.create(EstadoId.random(), NOMBRE_VALIDO, DESCRIPCION_VALIDA);
        
        // When & Then
        assertThat(estados1).isEqualTo(estados2);
        assertThat(estados1).isNotEqualTo(estados3);
        assertThat(estados1.hashCode()).isEqualTo(estados2.hashCode());
        assertThat(estados1.hashCode()).isNotEqualTo(estados3.hashCode());
    }

    @Test
    @DisplayName("Debería tener toString que incluya todos los campos")
    void deberiaTenerToStringQueIncluyaTodosLosCampos() {
        // Given
        EstadoId idEstado = EstadoId.random();
        Estados estados = Estados.create(idEstado, NOMBRE_VALIDO, DESCRIPCION_VALIDA);
        
        // When
        String toString = estados.toString();
        
        // Then
        assertThat(toString).contains("Estados{");
        assertThat(toString).contains("idEstado=" + idEstado);
        assertThat(toString).contains("nombre='" + NOMBRE_VALIDO + "'");
        assertThat(toString).contains("descripcion='" + DESCRIPCION_VALIDA + "'");
        assertThat(toString).contains("version=0");
        assertThat(toString).endsWith("}");
    }

    @Test
    @DisplayName("Debería ser inmutable")
    void deberiaSerInmutable() {
        // Given
        Estados estados = Estados.create(EstadoId.random(), NOMBRE_VALIDO, DESCRIPCION_VALIDA);
        
        // When
        String nombreOriginal = estados.nombre();
        String descripcionOriginal = estados.descripcion();
        long versionOriginal = estados.version();
        
        // Then
        assertThat(estados.nombre()).isEqualTo(nombreOriginal);
        assertThat(estados.descripcion()).isEqualTo(descripcionOriginal);
        assertThat(estados.version()).isEqualTo(versionOriginal);
        
        // Verificar que los campos son finales por diseño
        assertThat(estados.nombre()).isSameAs(nombreOriginal);
        assertThat(estados.descripcion()).isSameAs(descripcionOriginal);
    }

    @Test
    @DisplayName("Debería manejar múltiples actualizaciones correctamente")
    void deberiaManejarMultiplesActualizacionesCorrectamente() {
        // Given
        Estados estados = Estados.create(EstadoId.random(), NOMBRE_VALIDO, DESCRIPCION_VALIDA);
        
        // When
        Estados estadosActualizado1 = estados.actualizarNombre("APROBADA");
        Estados estadosActualizado2 = estadosActualizado1.actualizarDescripcion("Solicitud aprobada por asesor");
        Estados estadosActualizado3 = estadosActualizado2.markPersisted(5L);
        
        // Then
        assertThat(estadosActualizado3.nombre()).isEqualTo("APROBADA");
        assertThat(estadosActualizado3.descripcion()).isEqualTo("Solicitud aprobada por asesor");
        assertThat(estadosActualizado3.version()).isEqualTo(5L);
        
        // Verificar que el original no cambió
        assertThat(estados.nombre()).isEqualTo(NOMBRE_VALIDO);
        assertThat(estados.descripcion()).isEqualTo(DESCRIPCION_VALIDA);
        assertThat(estados.version()).isEqualTo(0L);
    }
}
