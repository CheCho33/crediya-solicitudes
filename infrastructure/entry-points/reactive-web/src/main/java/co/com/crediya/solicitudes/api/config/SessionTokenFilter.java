package co.com.crediya.solicitudes.api.config;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import co.com.crediya.solicitudes.consumer.api.UsuariosApi;
import co.com.crediya.solicitudes.consumer.api.model.UsuarioResponseDto;
import co.com.crediya.solicitudes.model.exceptions.CrediYautentiateException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SessionTokenFilter implements WebFilter {

    private final UsuariosApi usuariosApi;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // No aplicar el filtro para el endpoint de login, swagger y docs
        if (path.contains("swagger") || path.contains("docs")) {
            return chain.filter(exchange);
        }
        
        String token = exchange.getRequest().getHeaders().getFirst("x-Token");
        
        // Si no hay token, generar error
        if (token == null || token.trim().isEmpty()) {
            return Mono.error(new CrediYautentiateException("No tienen permisos para este servicio"));
        }

        // Validar el token y buscar la sesión
        return validarPermisoSesion(token)
            .flatMap(usuario -> {
                exchange.getAttributes().put("usuario", usuario);
                return chain.filter(exchange);
            })
            .onErrorResume(error -> {
                return Mono.error(new CrediYautentiateException("No tiene permisos para consumir este servicio"));
            });
    }

    private Mono<UsuarioResponseDto> validarPermisoSesion(String token) {
        return usuariosApi.getUsuarioRequest(token);
    }
}
