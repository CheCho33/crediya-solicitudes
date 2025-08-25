package co.com.crediya.solicitudes.api.config;

import co.com.crediya.solicitudes.api.Handler;
import co.com.crediya.solicitudes.api.RouterRest;
import co.com.crediya.solicitudes.usecase.solicitud.CrearSolicitudUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(controllers = {RouterRest.class, Handler.class})
@Import({CorsConfig.class, SecurityHeadersConfig.class, ConfigTest.MockConfig.class})
@ExtendWith(MockitoExtension.class)
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @SpringBootApplication
    static class TestApp {
    }

    @TestConfiguration
    static class MockConfig {
        @Bean
        public CrearSolicitudUseCase crearSolicitudUseCase() {
            return org.mockito.Mockito.mock(CrearSolicitudUseCase.class);
        }
    }

    @Test
    void corsConfigurationShouldAllowOrigins() {
        webTestClient.post()
                .uri("/api/v1/solicitud")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}