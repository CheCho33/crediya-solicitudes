package co.com.crediya.solicitudes.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import org.springframework.web.reactive.function.server.RouterFunction;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Configuración de rutas para los endpoints de solicitudes de préstamo.
 * 
 * Este router define las rutas funcionales de WebFlux para:
 * - POST /api/v1/solicitud - Crear nueva solicitud de préstamo
 * - GET /api/v1/solicitud - Listar solicitudes (pendiente)
 * - PUT /api/v1/solicitud/{id} - Actualizar estado de solicitud (pendiente)
 */
@Configuration
public class RouterRest {
    
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/solicitud"), handler::crearSolicitud)
                .andRoute(GET("/api/v1/solicitud"), handler::listarSolicitudesPendientes)
                .andRoute(GET("/api/usecase/path"), handler::listenGETUseCase);
    }
}
