package co.com.crediya.solicitudes.r2dbc.adapters;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamoId;
import co.com.crediya.solicitudes.model.valueobjects.Monto;
import co.com.crediya.solicitudes.r2dbc.mapper.TipoPrestamoInfraMapper;
import co.com.crediya.solicitudes.r2dbc.model.TipoPrestamoData;
import co.com.crediya.solicitudes.r2dbc.repository.TipoPrestamoReactiveRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class TipoPrestamoRepositoryAdapterTest {

    @Mock
    private TipoPrestamoReactiveRepository repository;

    @Mock
    private TipoPrestamoInfraMapper mapper;

    private TipoPrestamoRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new TipoPrestamoRepositoryAdapter(repository, mapper);        // Inyectar dependencias manualmente usando reflection
        try {
            java.lang.reflect.Field repositoryField = TipoPrestamoRepositoryAdapter.class.getDeclaredField("repository");
            repositoryField.setAccessible(true);
            repositoryField.set(adapter, repository);
            
            java.lang.reflect.Field mapperField = TipoPrestamoRepositoryAdapter.class.getDeclaredField("mapper");
            mapperField.setAccessible(true);
            mapperField.set(adapter, mapper);
        } catch (Exception e) {
            throw new RuntimeException("Error setting up test", e);
        }
    }



    private TipoPrestamoData data(UUID id, String nombre, long version) {
        return new TipoPrestamoData(
                id,
                nombre,
                new BigDecimal("1000.00"),
                new BigDecimal("5000.00"),
                new BigDecimal("0.15"),
                Boolean.TRUE,
                version,
                null,
                null,
                Boolean.TRUE);
    }

    @Test
    void save_happyPath() {
        UUID id = UUID.randomUUID();
        
        // Mock del dominio solo con los métodos que realmente se usan en save()
        TipoPrestamo domainIn = mock(TipoPrestamo.class);
        TipoPrestamoId tipoPrestamoId = new TipoPrestamoId(id);
        when(domainIn.id()).thenReturn(tipoPrestamoId);
        
        TipoPrestamoData toSave = data(id, "Libre Inversión", 0L);
        TipoPrestamoData saved = data(id, "Libre Inversión", 0L);
        TipoPrestamo domainOut = mock(TipoPrestamo.class);
        when(domainOut.id()).thenReturn(tipoPrestamoId);

        when(mapper.toData(domainIn)).thenReturn(toSave);
        when(repository.save(toSave)).thenReturn(Mono.just(saved));
        when(mapper.toDomain(saved)).thenReturn(domainOut);

        StepVerifier.create(adapter.save(domainIn))
                .expectNext(domainOut)
                .verifyComplete();

        verify(repository).save(toSave);
        verify(mapper).toDomain(saved);
    }

    @Test
    void save_null_throws() {
        StepVerifier.create(adapter.save(null))
                .expectError(IllegalArgumentException.class)
                .verify();
        verifyNoInteractions(repository);
    }

    @Test
    void update_happyPath() {
        UUID id = UUID.randomUUID();
        
        // Mock del dominio solo con los métodos que realmente se usan en update()
        TipoPrestamo domainIn = mock(TipoPrestamo.class);
        TipoPrestamoId tipoPrestamoId = new TipoPrestamoId(id);
        when(domainIn.id()).thenReturn(tipoPrestamoId);
        when(domainIn.version()).thenReturn(1L);
        
        TipoPrestamoData toUpdate = data(id, "Libre Inversión", 1L);
        TipoPrestamoData updated = data(id, "Libre Inversión", 2L);
        TipoPrestamo domainOut = mock(TipoPrestamo.class);
        when(domainOut.id()).thenReturn(tipoPrestamoId);
        when(domainOut.version()).thenReturn(2L);

        when(mapper.toData(domainIn)).thenReturn(toUpdate);
        when(repository.save(any(TipoPrestamoData.class))).thenReturn(Mono.just(updated));
        when(mapper.toDomain(updated)).thenReturn(domainOut);

        StepVerifier.create(adapter.update(domainIn))
                .expectNext(domainOut)
                .verifyComplete();

        verify(repository).save(any(TipoPrestamoData.class));
        verify(mapper).toDomain(updated);
    }

    @Test
    void update_null_throws() {
        StepVerifier.create(adapter.update(null))
                .expectError(IllegalArgumentException.class)
                .verify();
        verifyNoInteractions(repository);
    }

    @Test
    void findById_success() {
        UUID id = UUID.randomUUID();
        TipoPrestamoId domainId = new TipoPrestamoId(id);
        TipoPrestamoData found = data(id, "Educación", 2L);
        TipoPrestamo domain = mock(TipoPrestamo.class);

        when(mapper.toUUID(domainId)).thenReturn(id);
        when(repository.findById(id)).thenReturn(Mono.just(found));
        when(mapper.toDomain(found)).thenReturn(domain);

        StepVerifier.create(adapter.findById(domainId))
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void findById_null_throws() {
        StepVerifier.create(adapter.findById(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void findByNombre_success() {
        String nombre = "Hipotecario";
        UUID id = UUID.randomUUID();
        TipoPrestamoData found = data(id, nombre, 0L);
        TipoPrestamo domain = mock(TipoPrestamo.class);

        when(repository.findByNombre(nombre)).thenReturn(Mono.just(found));
        when(mapper.toDomain(found)).thenReturn(domain);

        StepVerifier.create(adapter.findByNombre(nombre))
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void findByNombre_blank_throws() {
        StepVerifier.create(adapter.findByNombre(" "))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void findByNombreContaining_success() {
        when(repository.findByNombreContaining("pre")).thenReturn(Flux.just(
                data(UUID.randomUUID(), "Préstamo A", 0L),
                data(UUID.randomUUID(), "Préstamo B", 0L)
        ));
        when(mapper.toDomain(any(TipoPrestamoData.class))).thenReturn(mock(TipoPrestamo.class));

        StepVerifier.create(adapter.findByNombreContaining("pre"))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findByNombreContaining_null_throws() {
        StepVerifier.create(adapter.findByNombreContaining(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void findByMontoPermitido_success() {
        Monto monto = Monto.of(new BigDecimal("3000"));
        BigDecimal bd = new BigDecimal("3000");
        when(mapper.toBigDecimal(monto)).thenReturn(bd);
        when(repository.findByMontoPermitido(bd)).thenReturn(Flux.just(
                data(UUID.randomUUID(), "X", 0L)));
        when(mapper.toDomain(any(TipoPrestamoData.class))).thenReturn(mock(TipoPrestamo.class));

        StepVerifier.create(adapter.findByMontoPermitido(monto))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findByMontoPermitido_null_throws() {
        StepVerifier.create(adapter.findByMontoPermitido(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void findByValidacionAutomatica_success() {
        when(repository.findByValidacionAutomatica(true)).thenReturn(Flux.just(
                data(UUID.randomUUID(), "A", 0L), data(UUID.randomUUID(), "B", 0L)));
        when(mapper.toDomain(any(TipoPrestamoData.class))).thenReturn(mock(TipoPrestamo.class));

        StepVerifier.create(adapter.findByValidacionAutomatica(true))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findByRangoTasaInteres_invalidRange_throws() {
        StepVerifier.create(adapter.findByRangoTasaInteres(0.2, 0.1))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
    
    @Test
    void findAll_success() {
        when(repository.findActivos()).thenReturn(Flux.just(data(UUID.randomUUID(), "A", 0L)));
        when(mapper.toDomain(any(TipoPrestamoData.class))).thenReturn(mock(TipoPrestamo.class));

        StepVerifier.create(adapter.findAll())
            .expectNextCount(1)
            .verifyComplete();
    }

    @Test
    void findAllOrderedBy_blank_throws() {
        StepVerifier.create(adapter.findAllOrderedBy("", true))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void findAllOrderedBy_success() {
        when(repository.findAllOrderedBy("nombre", true)).thenReturn(Flux.just(
                data(UUID.randomUUID(), "A", 0L), data(UUID.randomUUID(), "B", 0L)));
        when(mapper.toDomain(any(TipoPrestamoData.class))).thenReturn(mock(TipoPrestamo.class));

        StepVerifier.create(adapter.findAllOrderedBy("nombre", true))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Nested
    class PaginationTests {
        @Test
        void findAllPaginated_invalidPage_throws() {
            StepVerifier.create(adapter.findAllPaginated(-1, 10))
                    .expectError(IllegalArgumentException.class)
                    .verify();
        }

        @Test
        void findAllPaginated_invalidSize_throws() {
            StepVerifier.create(adapter.findAllPaginated(0, 0))
                    .expectError(IllegalArgumentException.class)
                    .verify();
        }

        @Test
        void findAllPaginated_success_calculatesOffset() {
            ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);
            ArgumentCaptor<Integer> offsetCaptor = ArgumentCaptor.forClass(Integer.class);

            when(repository.findAllPaginated(anyInt(), anyInt()))
                    .thenReturn(Flux.just(data(UUID.randomUUID(), "A", 0L)));
            when(mapper.toDomain(any(TipoPrestamoData.class))).thenReturn(mock(TipoPrestamo.class));

            StepVerifier.create(adapter.findAllPaginated(3, 25))
                    .expectNextCount(1)
                    .verifyComplete();

            verify(repository).findAllPaginated(limitCaptor.capture(), offsetCaptor.capture());
            assertThat(limitCaptor.getValue()).isEqualTo(25);
            assertThat(offsetCaptor.getValue()).isEqualTo(75); // 3 * 25
        }
    }

    @Test
    void existsById_success() {
        UUID id = UUID.randomUUID();
        when(mapper.toUUID(any(TipoPrestamoId.class))).thenReturn(id);
        when(repository.existsById(id)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsById(new TipoPrestamoId(id)))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsById_null_throws() {
        StepVerifier.create(adapter.existsById(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void existsByNombre_success() {
        when(repository.existsByNombre("test")).thenReturn(Mono.just(false));

        StepVerifier.create(adapter.existsByNombre("test"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void existsByNombre_blank_throws() {
        StepVerifier.create(adapter.existsByNombre(""))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void existsByMontoPermitido_success() {
        Monto monto = Monto.of(new BigDecimal("1500"));
        BigDecimal bd = new BigDecimal("1500");
        when(mapper.toBigDecimal(monto)).thenReturn(bd);
        when(repository.existsByMontoPermitido(bd)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByMontoPermitido(monto))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByMontoPermitido_null_throws() {
        StepVerifier.create(adapter.existsByMontoPermitido(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void deleteById_success() {
        UUID id = UUID.randomUUID();
        TipoPrestamoId domainId = new TipoPrestamoId(id);
        when(mapper.toUUID(domainId)).thenReturn(id);
        when(repository.softDeleteById(id)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteById(domainId))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void deleteById_null_throws() {
        StepVerifier.create(adapter.deleteById(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void count_success() {
        when(repository.countActivos()).thenReturn(Mono.just(5L));

        StepVerifier.create(adapter.count())
                .expectNext(5L)
                .verifyComplete();
    }

    @Test
    void countByValidacionAutomatica_success() {
        when(repository.countByValidacionAutomatica(true)).thenReturn(Mono.just(2L));

        StepVerifier.create(adapter.countByValidacionAutomatica(true))
                .expectNext(2L)
                .verifyComplete();
    }

    @Test
    void findByCriterios_success() {
        when(mapper.toBigDecimal(Monto.of(new BigDecimal("1000"))))
                .thenReturn(new BigDecimal("1000"));
        when(mapper.toBigDecimal(Monto.of(new BigDecimal("4000"))))
                .thenReturn(new BigDecimal("4000"));

        when(repository.findByCriterios(eq("pre"), eq(Boolean.TRUE), eq(new BigDecimal("1000")), eq(new BigDecimal("4000"))))
                .thenReturn(Flux.just(data(UUID.randomUUID(), "A", 0L)));
        when(mapper.toDomain(any(TipoPrestamoData.class))).thenReturn(mock(TipoPrestamo.class));

        StepVerifier.create(adapter.findByCriterios("pre", true, Monto.of(new BigDecimal("1000")), Monto.of(new BigDecimal("4000"))))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findMasPopulares_invalidLimit_throws() {
        StepVerifier.create(adapter.findMasPopulares(0))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void findMasPopulares_success() {
        when(repository.findMasPopulares(3)).thenReturn(Flux.just(
                data(UUID.randomUUID(), "A", 0L), data(UUID.randomUUID(), "B", 0L), data(UUID.randomUUID(), "C", 0L)));
        when(mapper.toDomain(any(TipoPrestamoData.class))).thenReturn(mock(TipoPrestamo.class));

        StepVerifier.create(adapter.findMasPopulares(3))
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findActivos_success() {
        when(repository.findActivos()).thenReturn(Flux.just(data(UUID.randomUUID(), "A", 0L)));
        when(mapper.toDomain(any(TipoPrestamoData.class))).thenReturn(mock(TipoPrestamo.class));

        StepVerifier.create(adapter.findActivos())
                .expectNextCount(1)
                .verifyComplete();
    }
}
