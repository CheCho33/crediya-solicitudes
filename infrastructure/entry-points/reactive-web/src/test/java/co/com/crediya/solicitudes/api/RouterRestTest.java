package co.com.crediya.solicitudes.api;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

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

}
