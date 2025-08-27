package co.com.crediya.solicitudes.r2dbc.adapters;

import co.com.crediya.solicitudes.model.estados.Estados;
import co.com.crediya.solicitudes.model.estados.gateways.EstadosRepository;
import co.com.crediya.solicitudes.r2dbc.mapper.EstadoEntityMapper;
import co.com.crediya.solicitudes.r2dbc.repository.EstadosEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EstadosRepositoryAdapter implements EstadosRepository {

    private final EstadosEntityRepository estadosEntityRepository;

    @Override
    @Transactional
    public Mono<Estados> save(Estados estado) {
        if (estado == null) return Mono.error(new IllegalArgumentException("Estado no puede ser null"));

        return Mono.just(estado)
                .map(EstadoEntityMapper::toEntity)
                .flatMap(estadosEntityRepository::save)
                .map(EstadoEntityMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Estados> findById(Long idEstado) {
        if (idEstado == null) return Mono.error(new IllegalArgumentException("ID de estado no puede ser null"));

        return estadosEntityRepository.findById(idEstado)
                .map(EstadoEntityMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Estados> findByNombre(String nombre) {
        if (nombre == null) return Mono.error(new IllegalArgumentException("Nombre de estado no puede ser null"));

        return estadosEntityRepository.findByNombre(nombre)
                .map(EstadoEntityMapper::toDomain);
    }


}
