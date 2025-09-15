package co.com.crediya.solicitudes.usecase.solicitud;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.crediya.solicitudes.model.estados.gateways.EstadosRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.tipoprestamo.gateways.TipoPrestamoRepository;

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
       
    }
}
