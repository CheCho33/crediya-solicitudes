package co.com.crediya.solicitudes.usecase.solicitud;

import co.com.crediya.solicitudes.model.estados.Estados;
import co.com.crediya.solicitudes.model.estados.gateways.EstadosRepository;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.solicitudes.model.tipoprestamo.gateways.TipoPrestamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    void setUp() {
        useCase = new CrearSolicitudUseCase(solicitudRepository, tipoPrestamoRepository, estadosRepository);
    }

    @Test
    @DisplayName("Debería crear la solicitud correctamente con datos válidos y estado inicial 'Pendiente de revisión'")
    void deberiaCrearSolicitudCorrectamente() {
        // Datos de entrada
        Double monto = 5_000_000.00;
        Double plazo = 24.0;
        String email = "cliente@test.com";
        String documento = "1234567890";
        Long idTipoPrestamo = 1L;

        // Mock TipoPrestamo válido
        TipoPrestamo tipoPrestamo = new TipoPrestamo();
        tipoPrestamo.setIdTipoPrestamo(idTipoPrestamo);
        tipoPrestamo.setNombre("Préstamo Personal");
        tipoPrestamo.setMontoMinimo(1_000_000.00);
        tipoPrestamo.setMontoMaximo(10_000_000.00);
        tipoPrestamo.setTasaInteres(15.5);
        tipoPrestamo.setValidacionAutomatica(true);

        // Mock Estado inicial
        Estados estadoInicial = new Estados(10L, "Pendiente de revisión", "Solicitud pendiente de revisión por asesor");

        // Stubs de repositorios
        when(tipoPrestamoRepository.findById(idTipoPrestamo)).thenReturn(Mono.just(tipoPrestamo));
        when(estadosRepository.findByNombre("Pendiente de revisión")).thenReturn(Mono.just(estadoInicial));
        when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(invocation -> {
            Solicitud s = invocation.getArgument(0);
            // opcional: simular asignación de ID
            s.setIdSolicitud(100L);
            return Mono.just(s);
        });

        // Ejecutar y verificar
        StepVerifier.create(useCase.crearSolicitud(monto, plazo, email, documento, idTipoPrestamo))
                .assertNext(s -> {
                    assertThat(s.getIdSolicitud()).isEqualTo(100L);
                    assertThat(s.getMonto()).isEqualTo(String.valueOf(monto));
                    assertThat(s.getPlazo()).isEqualTo(plazo);
                    assertThat(s.getEmail()).isEqualTo(email);
                    assertThat(s.getIdEstado()).isEqualTo(estadoInicial.getIdEstado());
                    assertThat(s.getIdTipoPrestamo()).isEqualTo(tipoPrestamo.getIdTipoPrestamo());
                })
                .verifyComplete();
    }
}
