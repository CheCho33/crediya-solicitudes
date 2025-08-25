package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.api.dto.CrearSolicitudRequest;
import co.com.crediya.solicitudes.api.dto.SolicitudResponse;
import co.com.crediya.solicitudes.api.mapper.SolicitudEntryMapper;
import co.com.crediya.solicitudes.model.estados.EstadoId;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.SolicitudId;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamoId;
import co.com.crediya.solicitudes.model.valueobjects.Email;
import co.com.crediya.solicitudes.model.valueobjects.Monto;
import co.com.crediya.solicitudes.model.valueobjects.Plazo;
import co.com.crediya.solicitudes.usecase.solicitud.CrearSolicitudUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test sencillo para el endpoint POST /api/v1/solicitud
 * 
 * Valida la creación exitosa de una solicitud de préstamo
 * con mocks para evitar consultas a base de datos real
 * 
 * Configuración moderna SIN @MockBean (eliminado en Spring Boot 3.4.0+)
 * Usa WebTestClient con RouterFunction directamente
 */
@ExtendWith(MockitoExtension.class)
class RouterRestTest {

    @Mock
    private CrearSolicitudUseCase crearSolicitudUseCase;

    private WebTestClient webTestClient;
    private UUID tipoPrestamoId;
    private Solicitud solicitudMock;
    private SolicitudEntryMapper solicitudEntryMapper;

    @BeforeEach
    void setUp() {
        // Inicializar mocks
        MockitoAnnotations.openMocks(this);
        
        // Crear mapper
        solicitudEntryMapper = new SolicitudEntryMapper();
        
        // Crear handler con mocks
        Handler handler = new Handler(crearSolicitudUseCase, solicitudEntryMapper);
        
        // Crear router
        RouterRest routerRest = new RouterRest();
        RouterFunction<ServerResponse> routerFunction = routerRest.routerFunction(handler);
        
        // Crear WebTestClient con el router
        this.webTestClient = WebTestClient
            .bindToRouterFunction(routerFunction)
            .build();
        
        tipoPrestamoId = UUID.randomUUID();
        
        // Crear solicitud mock para el test
        solicitudMock = Solicitud.create(
            SolicitudId.random(),
            Monto.of(BigDecimal.valueOf(5000000.00)),
            Plazo.of(24),
            Email.of("cliente@ejemplo.com"),
            EstadoId.random(),
            new TipoPrestamoId(tipoPrestamoId)
        );
    }

    @Test
    void deberiaCrearSolicitudExitosamente() {
        // Given
        CrearSolicitudRequest request = CrearSolicitudRequest.builder()
            .montoSolicitado(BigDecimal.valueOf(5000000.00).setScale(2, RoundingMode.HALF_UP))
            .plazoMeses(24)
            .emailSolicitante("cliente@ejemplo.com")
            .idTipoPrestamo(tipoPrestamoId)
            .build();

        // Mock del caso de uso para evitar consultas a base de datos
        when(crearSolicitudUseCase.crearSolicitud(
            any(Monto.class), 
            any(Plazo.class), 
            any(Email.class), 
            any(TipoPrestamoId.class)
        )).thenReturn(Mono.just(solicitudMock));

        // When & Then
        webTestClient.post()
            .uri("/api/v1/solicitud")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(SolicitudResponse.class)
            .value(response -> {
                assertThat(response.id()).isNotNull();
                assertThat(response.montoSolicitado()).isEqualByComparingTo(BigDecimal.valueOf(5000000.00));                assertThat(response.plazoMeses()).isEqualTo(24);
                assertThat(response.emailSolicitante()).isEqualTo("cliente@ejemplo.com");
                assertThat(response.idTipoPrestamo()).isEqualTo(tipoPrestamoId);
                assertThat(response.estadoSolicitud()).isNotNull();
                assertThat(response.fechaCreacion()).isNotNull();
            });
    }
}
