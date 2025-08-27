package co.com.crediya.solicitudes.r2dbc.adapters;

import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.solicitudes.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.crediya.solicitudes.r2dbc.mapper.EstadoEntityMapper;
import co.com.crediya.solicitudes.r2dbc.mapper.TipoPrestamoEntityMapper;
import co.com.crediya.solicitudes.r2dbc.repository.TipoPrestamoEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TipoPrestamoRepositoryAdapter implements TipoPrestamoRepository {

    private final TipoPrestamoEntityRepository tipoPrestamoEntityRepository;

    @Override
    public Mono<TipoPrestamo> save(TipoPrestamo tipoPrestamo) {
        if (tipoPrestamo == null) return Mono.error(new IllegalArgumentException("Tipo de préstamo no puede ser null"));

        return Mono.just(tipoPrestamo)
                .map(TipoPrestamoEntityMapper::toEntity)
                .flatMap(tipoPrestamoEntityRepository::save)
                .map(TipoPrestamoEntityMapper::toDomain);
    }


    @Override
    public Mono<TipoPrestamo> findById(Long idTipoPrestamo) {
        if (idTipoPrestamo == null) return Mono.error(new IllegalArgumentException("ID no puede ser null"));

        return tipoPrestamoEntityRepository.findById(idTipoPrestamo)
                .map(TipoPrestamoEntityMapper::toDomain);
    }

}
