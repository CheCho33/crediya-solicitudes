package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.api.dto.CrearSolicitudRequest;
import co.com.crediya.solicitudes.api.dto.SolicitudResponse;
import co.com.crediya.solicitudes.api.mapper.CrearSolicitudRequestMapper;
import co.com.crediya.solicitudes.api.mapper.SolicitudResponseMapper;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
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
    private Solicitud solicitudMock;

    @BeforeEach
    void setUp() {
        // Inicializar mocks
        MockitoAnnotations.openMocks(this);


        // Crear handler con mocks
        Handler handler = new Handler(
            crearSolicitudUseCase,
            new SolicitudResponseMapper(),
            new CrearSolicitudRequestMapper()
        );

        // Crear router
        RouterRest routerRest = new RouterRest();
        RouterFunction<ServerResponse> routerFunction = routerRest.routerFunction(handler);

        // Crear WebTestClient con el router
        this.webTestClient = WebTestClient
            .bindToRouterFunction(routerFunction)
            .build();

        // Crear solicitud mock para el test
        solicitudMock = new Solicitud();
        solicitudMock.setIdSolicitud(1l);
        solicitudMock.setPlazo(2.0);
        solicitudMock.setMonto(5000000.00);
        solicitudMock.setIdEstado(1l);
        solicitudMock.setIdTipoPrestamo(1l);
        solicitudMock.setEmail("test@Test");

    }

    @Test
    void deberiaCrearSolicitudExitosamente() {
        // Given
        CrearSolicitudRequest request = new CrearSolicitudRequest(5000000.0, 2.0, "test@test.com", 1L);

        // Mock del caso de uso para evitar consultas a base de datos
        when(crearSolicitudUseCase.crearSolicitud(
            any(Double.class),
            any(Double.class),
            any(String.class),
            any(Long.class)
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
            });
    }
}
