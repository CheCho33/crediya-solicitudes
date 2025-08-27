package co.com.crediya.solicitudes.model.tipoprestamo.gateways;

import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import reactor.core.publisher.Mono;


public interface TipoPrestamoRepository {

    /**
     * Guarda un nuevo tipo de préstamo en la base de datos.
     *
     * @param tipoPrestamo entidad TipoPrestamo a persistir
     * @return Mono con la entidad guardada con versión actualizada
     * @throws IllegalArgumentException si el tipoPrestamo es null
     */
    Mono<TipoPrestamo> save(TipoPrestamo tipoPrestamo);

    /**
     * Busca un tipo de préstamo por su identificador único.
     *
     * @param idTipoPrestamo identificador del tipo de préstamo a buscar
     * @return Mono con el tipo de préstamo si existe, Mono.empty() si no existe
     * @throws IllegalArgumentException si el idTipoPrestamo es null
     */
    Mono<TipoPrestamo> findById(Long idTipoPrestamo);


}
