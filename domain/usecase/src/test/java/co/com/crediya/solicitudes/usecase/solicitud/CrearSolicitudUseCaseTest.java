package co.com.crediya.solicitudes.usecase.solicitud;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.crediya.solicitudes.model.estados.EstadoId;
import co.com.crediya.solicitudes.model.estados.Estados;
import co.com.crediya.solicitudes.model.estados.gateways.EstadosRepository;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamoId;
import co.com.crediya.solicitudes.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.crediya.solicitudes.model.valueobjects.Email;
import co.com.crediya.solicitudes.model.valueobjects.Monto;
import co.com.crediya.solicitudes.model.valueobjects.Nombre;
import co.com.crediya.solicitudes.model.valueobjects.Plazo;
import co.com.crediya.solicitudes.model.valueobjects.TasaInteres;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Tests unitarios para el caso de uso CrearSolicitudUseCase.
 * 
 * Estos tests verifican:
 * - Creación exitosa de solicitud con datos válidos
 * - Validación de tipo de préstamo inexistente
 * - Validación de monto fuera del rango permitido
 * - Validación de estado inicial no disponible
 * - Manejo correcto de errores de negocio
 * - Casos límite (monto mínimo y máximo)
 */
@ExtendWith(MockitoExtension.class)
class CrearSolicitudUseCaseTest {
    
    @Mock
    private SolicitudRepository solicitudRepository;
    
    @Mock
    private TipoPrestamoRepository tipoPrestamoRepository;
    
    @Mock
    private EstadosRepository estadosRepository;
    
    private CrearSolicitudUseCase useCase;
    private Supplier<UUID> uuidGenerator;
    
    // Datos de prueba
    private static final UUID SOLICITUD_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID TIPO_PRESTAMO_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
    private static final UUID ESTADO_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
    
    private Monto montoSolicitado;
    private Plazo plazoMeses;
    private Email emailSolicitante;
    private TipoPrestamoId idTipoPrestamo;
    private TipoPrestamo tipoPrestamo;
    private Estados estadoInicial;
    
    @BeforeEach
    void setUp() {
        uuidGenerator = () -> SOLICITUD_ID;
        useCase = new CrearSolicitudUseCase(
                solicitudRepository, 
                tipoPrestamoRepository, 
                estadosRepository, 
                uuidGenerator
        );
        
        // Configurar datos de prueba
        montoSolicitado = Monto.of(new BigDecimal("5000000")); // 5 millones
        plazoMeses = Plazo.of(24); // 24 meses
        emailSolicitante = Email.of("cliente@test.com");
        idTipoPrestamo = TipoPrestamoId.newId(() -> TIPO_PRESTAMO_ID);
        
        // Configurar tipo de préstamo de prueba
        tipoPrestamo = TipoPrestamo.crear(
                TipoPrestamoId.newId(() -> TIPO_PRESTAMO_ID),
                Nombre.of("Préstamo Personal"),
                Monto.of(new BigDecimal("1000000")), // 1 millón mínimo
                Monto.of(new BigDecimal("10000000")), // 10 millones máximo
                TasaInteres.of(new BigDecimal("15.5")), // 15.5% anual
                true // requiere validación automática
        );
        
        // Configurar estado inicial
        estadoInicial = Estados.create(
                EstadoId.newId(() -> ESTADO_ID),
                "Pendiente de revisión",
                "Solicitud pendiente de revisión por asesor"
        );
    }
    
    @Test
    @DisplayName("Debería crear solicitud exitosamente cuando todos los datos son válidos")
    void deberiaCrearSolicitudExitosamente() {
        // Given
        when(tipoPrestamoRepository.findById(idTipoPrestamo))
                .thenReturn(Mono.just(tipoPrestamo));
        when(estadosRepository.findByNombre("Pendiente de revisión"))
                .thenReturn(Mono.just(estadoInicial));
        when(solicitudRepository.save(any(Solicitud.class)))
                .thenAnswer(invocation -> {
                    Solicitud solicitud = invocation.getArgument(0);
                    return Mono.just(solicitud);
                });
        
        // When & Then
        StepVerifier.create(useCase.crearSolicitud(montoSolicitado, plazoMeses, emailSolicitante, idTipoPrestamo))
                .expectNextMatches(solicitud -> {
                    assertThat(solicitud.id().value()).isEqualTo(SOLICITUD_ID);
                    assertThat(solicitud.monto()).isEqualTo(montoSolicitado);
                    assertThat(solicitud.plazo()).isEqualTo(plazoMeses);
                    assertThat(solicitud.email()).isEqualTo(emailSolicitante);
                    assertThat(solicitud.idEstado()).isEqualTo(estadoInicial.idEstado());
                    assertThat(solicitud.idTipoPrestamo()).isEqualTo(tipoPrestamo.id());
                    return true;
                })
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería fallar cuando el tipo de préstamo no existe")
    void deberiaFallarCuandoTipoPrestamoNoExiste() {
        // Given
        when(tipoPrestamoRepository.findById(idTipoPrestamo))
                .thenReturn(Mono.empty());
        
        // When & Then
        StepVerifier.create(useCase.crearSolicitud(montoSolicitado, plazoMeses, emailSolicitante, idTipoPrestamo))
                .expectErrorMatches(error -> 
                        error instanceof IllegalStateException &&
                        error.getMessage().contains("El tipo de préstamo con ID " + TIPO_PRESTAMO_ID + " no existe"))
                .verify();
    }
    
    @Test
    @DisplayName("Debería fallar cuando el monto está fuera del rango permitido (mayor al máximo)")
    void deberiaFallarCuandoMontoFueraDeRangoMayor() {
        // Given
        Monto montoFueraDeRango = Monto.of(new BigDecimal("15000000")); // 15 millones (fuera del rango 1-10M)
        
        when(tipoPrestamoRepository.findById(idTipoPrestamo))
                .thenReturn(Mono.just(tipoPrestamo));
        
        // When & Then
        StepVerifier.create(useCase.crearSolicitud(montoFueraDeRango, plazoMeses, emailSolicitante, idTipoPrestamo))
                .expectErrorMatches(error -> 
                        error instanceof IllegalArgumentException &&
                        error.getMessage().contains("no está dentro del rango permitido") &&
                        error.getMessage().contains("Préstamo Personal"))
                .verify();
    }
    
    @Test
    @DisplayName("Debería fallar cuando el monto está fuera del rango permitido (menor al mínimo)")
    void deberiaFallarCuandoMontoFueraDeRangoMenor() {
        // Given
        Monto montoMenorAlMinimo = Monto.of(new BigDecimal("500000")); // 500k (menor al mínimo de 1M)
        
        when(tipoPrestamoRepository.findById(idTipoPrestamo))
                .thenReturn(Mono.just(tipoPrestamo));
        
        // When & Then
        StepVerifier.create(useCase.crearSolicitud(montoMenorAlMinimo, plazoMeses, emailSolicitante, idTipoPrestamo))
                .expectErrorMatches(error -> 
                        error instanceof IllegalArgumentException &&
                        error.getMessage().contains("no está dentro del rango permitido"))
                .verify();
    }
    
    @Test
    @DisplayName("Debería fallar cuando el estado inicial no está disponible")
    void deberiaFallarCuandoEstadoInicialNoDisponible() {
        // Given
        when(tipoPrestamoRepository.findById(idTipoPrestamo))
                .thenReturn(Mono.just(tipoPrestamo));
        when(estadosRepository.findByNombre("Pendiente de revisión"))
                .thenReturn(Mono.empty());
        
        // When & Then
        StepVerifier.create(useCase.crearSolicitud(montoSolicitado, plazoMeses, emailSolicitante, idTipoPrestamo))
                .expectErrorMatches(error -> 
                        error instanceof IllegalStateException &&
                        error.getMessage().contains("El estado inicial 'Pendiente de revisión' no está disponible"))
                .verify();
    }
    
    @Test
    @DisplayName("Debería crear solicitud con monto mínimo válido")
    void deberiaCrearSolicitudConMontoMinimo() {
        // Given
        Monto montoMinimo = Monto.of(new BigDecimal("1000000")); // 1 millón (mínimo permitido)
        
        when(tipoPrestamoRepository.findById(idTipoPrestamo))
                .thenReturn(Mono.just(tipoPrestamo));
        when(estadosRepository.findByNombre("Pendiente de revisión"))
                .thenReturn(Mono.just(estadoInicial));
        when(solicitudRepository.save(any(Solicitud.class)))
                .thenAnswer(invocation -> {
                    Solicitud solicitud = invocation.getArgument(0);
                    return Mono.just(solicitud);
                });
        
        // When & Then
        StepVerifier.create(useCase.crearSolicitud(montoMinimo, plazoMeses, emailSolicitante, idTipoPrestamo))
                .expectNextMatches(solicitud -> {
                    assertThat(solicitud.monto()).isEqualTo(montoMinimo);
                    return true;
                })
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería crear solicitud con monto máximo válido")
    void deberiaCrearSolicitudConMontoMaximo() {
        // Given
        Monto montoMaximo = Monto.of(new BigDecimal("10000000")); // 10 millones (máximo permitido)
        
        when(tipoPrestamoRepository.findById(idTipoPrestamo))
                .thenReturn(Mono.just(tipoPrestamo));
        when(estadosRepository.findByNombre("Pendiente de revisión"))
                .thenReturn(Mono.just(estadoInicial));
        when(solicitudRepository.save(any(Solicitud.class)))
                .thenAnswer(invocation -> {
                    Solicitud solicitud = invocation.getArgument(0);
                    return Mono.just(solicitud);
                });
        
        // When & Then
        StepVerifier.create(useCase.crearSolicitud(montoMaximo, plazoMeses, emailSolicitante, idTipoPrestamo))
                .expectNextMatches(solicitud -> {
                    assertThat(solicitud.monto()).isEqualTo(montoMaximo);
                    return true;
                })
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería crear solicitud con plazo mínimo válido")
    void deberiaCrearSolicitudConPlazoMinimo() {
        // Given
        Plazo plazoMinimo = Plazo.of(1); // 1 mes (mínimo permitido)
        
        when(tipoPrestamoRepository.findById(idTipoPrestamo))
                .thenReturn(Mono.just(tipoPrestamo));
        when(estadosRepository.findByNombre("Pendiente de revisión"))
                .thenReturn(Mono.just(estadoInicial));
        when(solicitudRepository.save(any(Solicitud.class)))
                .thenAnswer(invocation -> {
                    Solicitud solicitud = invocation.getArgument(0);
                    return Mono.just(solicitud);
                });
        
        // When & Then
        StepVerifier.create(useCase.crearSolicitud(montoSolicitado, plazoMinimo, emailSolicitante, idTipoPrestamo))
                .expectNextMatches(solicitud -> {
                    assertThat(solicitud.plazo()).isEqualTo(plazoMinimo);
                    return true;
                })
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería crear solicitud con plazo máximo válido")
    void deberiaCrearSolicitudConPlazoMaximo() {
        // Given
        Plazo plazoMaximo = Plazo.of(120); // 120 meses (máximo permitido)
        
        when(tipoPrestamoRepository.findById(idTipoPrestamo))
                .thenReturn(Mono.just(tipoPrestamo));
        when(estadosRepository.findByNombre("Pendiente de revisión"))
                .thenReturn(Mono.just(estadoInicial));
        when(solicitudRepository.save(any(Solicitud.class)))
                .thenAnswer(invocation -> {
                    Solicitud solicitud = invocation.getArgument(0);
                    return Mono.just(solicitud);
                });
        
        // When & Then
        StepVerifier.create(useCase.crearSolicitud(montoSolicitado, plazoMaximo, emailSolicitante, idTipoPrestamo))
                .expectNextMatches(solicitud -> {
                    assertThat(solicitud.plazo()).isEqualTo(plazoMaximo);
                    return true;
                })
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería crear solicitud con email válido")
    void deberiaCrearSolicitudConEmailValido() {
        // Given
        Email emailValido = Email.of("usuario.test@dominio.com");
        
        when(tipoPrestamoRepository.findById(idTipoPrestamo))
                .thenReturn(Mono.just(tipoPrestamo));
        when(estadosRepository.findByNombre("Pendiente de revisión"))
                .thenReturn(Mono.just(estadoInicial));
        when(solicitudRepository.save(any(Solicitud.class)))
                .thenAnswer(invocation -> {
                    Solicitud solicitud = invocation.getArgument(0);
                    return Mono.just(solicitud);
                });
        
        // When & Then
        StepVerifier.create(useCase.crearSolicitud(montoSolicitado, plazoMeses, emailValido, idTipoPrestamo))
                .expectNextMatches(solicitud -> {
                    assertThat(solicitud.email()).isEqualTo(emailValido);
                    return true;
                })
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Debería fallar cuando el repositorio de solicitudes falla")
    void deberiaFallarCuandoRepositorioSolicitudesFalla() {
        // Given
        RuntimeException errorRepositorio = new RuntimeException("Error de conexión a base de datos");
        
        when(tipoPrestamoRepository.findById(idTipoPrestamo))
                .thenReturn(Mono.just(tipoPrestamo));
        when(estadosRepository.findByNombre("Pendiente de revisión"))
                .thenReturn(Mono.just(estadoInicial));
        when(solicitudRepository.save(any(Solicitud.class)))
                .thenReturn(Mono.error(errorRepositorio));
        
        // When & Then
        StepVerifier.create(useCase.crearSolicitud(montoSolicitado, plazoMeses, emailSolicitante, idTipoPrestamo))
                .expectError(RuntimeException.class)
                .verify();
    }
    
    @Test
    @DisplayName("Debería fallar cuando el repositorio de tipos de préstamo falla")
    void deberiaFallarCuandoRepositorioTiposPrestamoFalla() {
        // Given
        RuntimeException errorRepositorio = new RuntimeException("Error de conexión a base de datos");
        
        when(tipoPrestamoRepository.findById(idTipoPrestamo))
                .thenReturn(Mono.error(errorRepositorio));
        
        // When & Then
        StepVerifier.create(useCase.crearSolicitud(montoSolicitado, plazoMeses, emailSolicitante, idTipoPrestamo))
                .expectError(RuntimeException.class)
                .verify();
    }
    
    @Test
    @DisplayName("Debería fallar cuando el repositorio de estados falla")
    void deberiaFallarCuandoRepositorioEstadosFalla() {
        // Given
        RuntimeException errorRepositorio = new RuntimeException("Error de conexión a base de datos");
        
        when(tipoPrestamoRepository.findById(idTipoPrestamo))
                .thenReturn(Mono.just(tipoPrestamo));
        when(estadosRepository.findByNombre("Pendiente de revisión"))
                .thenReturn(Mono.error(errorRepositorio));
        
        // When & Then
        StepVerifier.create(useCase.crearSolicitud(montoSolicitado, plazoMeses, emailSolicitante, idTipoPrestamo))
                .expectError(RuntimeException.class)
                .verify();
    }
    
    @Test
    @DisplayName("Debería generar un ID único para cada solicitud")
    void deberiaGenerarIdUnicoParaCadaSolicitud() {
        // Given
        UUID idUnico = UUID.fromString("987fcdeb-51a2-43d1-9f12-345678901234");
        Supplier<UUID> generadorIdUnico = () -> idUnico;
        
        CrearSolicitudUseCase useCaseConIdUnico = new CrearSolicitudUseCase(
                solicitudRepository, 
                tipoPrestamoRepository, 
                estadosRepository, 
                generadorIdUnico
        );
        
        when(tipoPrestamoRepository.findById(idTipoPrestamo))
                .thenReturn(Mono.just(tipoPrestamo));
        when(estadosRepository.findByNombre("Pendiente de revisión"))
                .thenReturn(Mono.just(estadoInicial));
        when(solicitudRepository.save(any(Solicitud.class)))
                .thenAnswer(invocation -> {
                    Solicitud solicitud = invocation.getArgument(0);
                    return Mono.just(solicitud);
                });
        
        // When & Then
        StepVerifier.create(useCaseConIdUnico.crearSolicitud(montoSolicitado, plazoMeses, emailSolicitante, idTipoPrestamo))
                .expectNextMatches(solicitud -> {
                    assertThat(solicitud.id().value()).isEqualTo(idUnico);
                    return true;
                })
                .verifyComplete();
    }
}
