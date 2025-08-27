package co.com.crediya.solicitudes.r2dbc.adapters;

import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.r2dbc.mapper.SolicitudMapper;
import co.com.crediya.solicitudes.r2dbc.repository.SolicitudEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SolicitudRepositoryAdapter implements SolicitudRepository {

    private final SolicitudEntityRepository solicitudEntityRepository;

    @Override
    @Transactional
    public Mono<Solicitud> save(Solicitud solicitud) {
        if (solicitud == null) return Mono.error(new IllegalArgumentException("La solicitud no puede ser null"));

        return Mono.just(solicitud)
                .map(SolicitudMapper::toEntity)
                .flatMap(solicitudEntityRepository::save)
                .map(SolicitudMapper::toDomain);
    }

}
