package co.com.crediya.solicitudes.r2dbc.adapters;

import co.com.crediya.solicitudes.model.estados.EstadoId;
import co.com.crediya.solicitudes.model.estados.Estados;
import co.com.crediya.solicitudes.r2dbc.mapper.EstadosInfraMapper;
import co.com.crediya.solicitudes.r2dbc.model.EstadosData;
import co.com.crediya.solicitudes.r2dbc.repository.EstadosReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios para EstadosRepositoryAdapter.
 * 
 * Estos tests siguen las reglas de testing de adaptadores secundarios:
 * - Mock de dependencias externas
 * - Testing de flujos reactivos
 * - Cobertura de casos de éxito y error
 * - Validación de mapeo correcto
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EstadosRepositoryAdapter Tests")
class EstadosRepositoryAdapterTest {
    
    @Mock
    private EstadosReactiveRepository repository;
    
    @Mock
    private EstadosInfraMapper mapper;
    
    @InjectMocks
    private EstadosRepositoryAdapter adapter;
    
    private EstadoId estadoId;
    private Estados estado;
    private EstadosData estadosData;
    private UUID uuid;
    
    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        estadoId = new EstadoId(uuid);
        estado = Estados.create(estadoId, "PENDIENTE_REVISION", "Solicitud pendiente de revisión");
        estadosData = new EstadosData(uuid, "PENDIENTE_REVISION", "Solicitud pendiente de revisión", 0L, 
            LocalDateTime.now(), LocalDateTime.now(), true);
    }
    
    @Test
    @DisplayName("Debería guardar un estado exitosamente")
    void shouldSaveEstadoSuccessfully() {
        // Given
        when(mapper.toData(estado)).thenReturn(estadosData);
        when(repository.save(estadosData)).thenReturn(Mono.just(estadosData));
        when(mapper.toDomain(estadosData)).thenReturn(estado);
        
        // When & Then
        StepVerifier.create(adapter.save(estado))
                .expectNext(estado)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería retornar error cuando el estado es null")
    void shouldReturnErrorWhenEstadoIsNull() {
        // When & Then
        StepVerifier.create(adapter.save(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
    
    @Test
    @DisplayName("Debería actualizar un estado exitosamente")
    void shouldUpdateEstadoSuccessfully() {
        // Given
        Estados estadoActualizado = estado.actualizarDescripcion("Nueva descripción");
        EstadosData estadosDataActualizado = new EstadosData(uuid, "PENDIENTE_REVISION", "Nueva descripción", 1L, 
            LocalDateTime.now(), LocalDateTime.now(), true);
        
        when(mapper.toData(estadoActualizado)).thenReturn(estadosDataActualizado.withIncrementedVersion());
        when(repository.save(any(EstadosData.class))).thenReturn(Mono.just(estadosDataActualizado));
        when(mapper.toDomain(estadosDataActualizado)).thenReturn(estadoActualizado);
        
        // When & Then
        StepVerifier.create(adapter.update(estadoActualizado))
                .expectNext(estadoActualizado)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería encontrar un estado por ID exitosamente")
    void shouldFindEstadoByIdSuccessfully() {
        // Given
        when(mapper.toUUID(estadoId)).thenReturn(uuid);
        when(repository.findById(uuid)).thenReturn(Mono.just(estadosData));
        when(mapper.toDomain(estadosData)).thenReturn(estado);
        
        // When & Then
        StepVerifier.create(adapter.findById(estadoId))
                .expectNext(estado)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería retornar empty cuando no encuentra el estado por ID")
    void shouldReturnEmptyWhenEstadoNotFoundById() {
        // Given
        when(mapper.toUUID(estadoId)).thenReturn(uuid);
        when(repository.findById(uuid)).thenReturn(Mono.empty());
        
        // When & Then
        StepVerifier.create(adapter.findById(estadoId))
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería encontrar un estado por nombre exitosamente")
    void shouldFindEstadoByNombreSuccessfully() {
        // Given
        String nombre = "PENDIENTE_REVISION";
        when(repository.findByNombre(nombre)).thenReturn(Mono.just(estadosData));
        when(mapper.toDomain(estadosData)).thenReturn(estado);
        
        // When & Then
        StepVerifier.create(adapter.findByNombre(nombre))
                .expectNext(estado)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería retornar error cuando el nombre es null")
    void shouldReturnErrorWhenNombreIsNull() {
        // When & Then
        StepVerifier.create(adapter.findByNombre(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
    
    @Test
    @DisplayName("Debería encontrar estados por nombre conteniendo texto")
    void shouldFindEstadosByNombreContaining() {
        // Given
        String nombreParcial = "PENDIENTE";
        when(repository.findByNombreContaining(nombreParcial)).thenReturn(Flux.just(estadosData));
        when(mapper.toDomain(estadosData)).thenReturn(estado);
        
        // When & Then
        StepVerifier.create(adapter.findByNombreContaining(nombreParcial))
                .expectNext(estado)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería encontrar estados por descripción conteniendo texto")
    void shouldFindEstadosByDescripcionContaining() {
        // Given
        String descripcionParcial = "revisión";
        when(repository.findByDescripcionContaining(descripcionParcial)).thenReturn(Flux.just(estadosData));
        when(mapper.toDomain(estadosData)).thenReturn(estado);
        
        // When & Then
        StepVerifier.create(adapter.findByDescripcionContaining(descripcionParcial))
                .expectNext(estado)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería obtener todos los estados activos")
    void shouldFindAllActivos() {
        // Given
        when(repository.findActivos()).thenReturn(Flux.just(estadosData));
        when(mapper.toDomain(estadosData)).thenReturn(estado);
        
        // When & Then
        StepVerifier.create(adapter.findAll())
                .expectNext(estado)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería verificar si existe un estado por ID")
    void shouldCheckIfEstadoExistsById() {
        // Given
        when(mapper.toUUID(estadoId)).thenReturn(uuid);
        when(repository.existsById(uuid)).thenReturn(Mono.just(true));
        
        // When & Then
        StepVerifier.create(adapter.existsById(estadoId))
                .expectNext(true)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería verificar si existe un estado por nombre")
    void shouldCheckIfEstadoExistsByNombre() {
        // Given
        String nombre = "PENDIENTE_REVISION";
        when(repository.existsByNombre(nombre)).thenReturn(Mono.just(true));
        
        // When & Then
        StepVerifier.create(adapter.existsByNombre(nombre))
                .expectNext(true)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería eliminar un estado exitosamente")
    void shouldDeleteEstadoSuccessfully() {
        // Given
        when(mapper.toUUID(estadoId)).thenReturn(uuid);
        when(repository.softDeleteById(uuid)).thenReturn(Mono.empty());
        
        // When & Then
        StepVerifier.create(adapter.deleteById(estadoId))
                .expectNext(true)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería contar estados activos")
    void shouldCountActivos() {
        // Given
        when(repository.countActivos()).thenReturn(Mono.just(5L));
        
        // When & Then
        StepVerifier.create(adapter.count())
                .expectNext(5L)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería encontrar estados por criterios")
    void shouldFindEstadosByCriterios() {
        // Given
        String nombreParcial = "PENDIENTE";
        String descripcionParcial = "revisión";
        when(repository.findByCriterios(nombreParcial, descripcionParcial)).thenReturn(Flux.just(estadosData));
        when(mapper.toDomain(estadosData)).thenReturn(estado);
        
        // When & Then
        StepVerifier.create(adapter.findByCriterios(nombreParcial, descripcionParcial))
                .expectNext(estado)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería encontrar estados activos")
    void shouldFindActivos() {
        // Given
        when(repository.findActivos()).thenReturn(Flux.just(estadosData));
        when(mapper.toDomain(estadosData)).thenReturn(estado);
        
        // When & Then
        StepVerifier.create(adapter.findActivos())
                .expectNext(estado)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería encontrar estado por nombre ignorando mayúsculas/minúsculas")
    void shouldFindByNombreIgnoreCase() {
        // Given
        String nombre = "pendiente_revision";
        when(repository.findByNombreIgnoreCase(nombre)).thenReturn(Mono.just(estadosData));
        when(mapper.toDomain(estadosData)).thenReturn(estado);
        
        // When & Then
        StepVerifier.create(adapter.findByNombreIgnoreCase(nombre))
                .expectNext(estado)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería obtener estados ordenados por fecha de creación")
    void shouldFindAllOrderedByFechaCreacion() {
        // Given
        boolean ascendente = true;
        when(repository.findAllOrderedByFechaCreacion("ASC")).thenReturn(Flux.just(estadosData));
        when(mapper.toDomain(estadosData)).thenReturn(estado);
        
        // When & Then
        StepVerifier.create(adapter.findAllOrderedByFechaCreacion(ascendente))
                .expectNext(estado)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería obtener estados con paginación")
    void shouldFindAllPaginated() {
        // Given
        int pagina = 0;
        int tamanoPagina = 10;
        when(repository.findAllPaginated(tamanoPagina, 0)).thenReturn(Flux.just(estadosData));
        when(mapper.toDomain(estadosData)).thenReturn(estado);
        
        // When & Then
        StepVerifier.create(adapter.findAllPaginated(pagina, tamanoPagina))
                .expectNext(estado)
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería retornar error cuando la página es negativa")
    void shouldReturnErrorWhenPaginaIsNegative() {
        // When & Then
        StepVerifier.create(adapter.findAllPaginated(-1, 10))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
    
    @Test
    @DisplayName("Debería retornar error cuando el tamaño de página es inválido")
    void shouldReturnErrorWhenTamanoPaginaIsInvalid() {
        // When & Then
        StepVerifier.create(adapter.findAllPaginated(0, 0))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}
