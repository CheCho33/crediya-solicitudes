package co.com.crediya.solicitudes.config;

import co.com.crediya.solicitudes.model.estados.gateways.EstadosRepository;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.crediya.solicitudes.usecase.solicitud.CrearSolicitudUseCase;
import co.com.crediya.solicitudes.usecase.solicitud.ListarSolicitudesPendientesUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "co.com.crediya.solicitudes.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {



    @Bean
    public CrearSolicitudUseCase crearSolicitudUseCase(SolicitudRepository solicitudRepository, TipoPrestamoRepository tipoPrestamoRepository, EstadosRepository estadosRepository) {
        return new CrearSolicitudUseCase( solicitudRepository, tipoPrestamoRepository, estadosRepository);
    }
    
    @Bean
    public ListarSolicitudesPendientesUseCase listarSolicitudesPendientesUseCase(SolicitudRepository solicitudRepository, EstadosRepository estadosRepository) {
        return new ListarSolicitudesPendientesUseCase(solicitudRepository, estadosRepository);
    }

}
