package co.com.crediya.solicitudes.model.tipoprestamo.gateways;

import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamoId;
import co.com.crediya.solicitudes.model.valueobjects.Monto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Gateway para la persistencia de la entidad TipoPrestamo.
 * Define los contratos para las operaciones de base de datos relacionadas con tipos de préstamo.
 * 
 * Este gateway sigue los principios de Arquitectura Hexagonal:
 * - Define contratos del dominio sin dependencias de infraestructura
 * - Permite diferentes implementaciones (R2DBC, JPA, etc.)
 * - Mantiene el dominio libre de detalles técnicos de persistencia
 * - Soporta las reglas de negocio específicas de CrediYa
 * - Implementa programación reactiva con Project Reactor
 */
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
     * Actualiza un tipo de préstamo existente en la base de datos.
     * Utiliza control de concurrencia optimista basado en versión.
     * 
     * @param tipoPrestamo entidad TipoPrestamo a actualizar
     * @return Mono con la entidad actualizada con nueva versión
     * @throws IllegalArgumentException si el tipoPrestamo es null
     * @throws IllegalStateException si la versión no coincide (concurrencia)
     */
    Mono<TipoPrestamo> update(TipoPrestamo tipoPrestamo);
    
    /**
     * Busca un tipo de préstamo por su identificador único.
     * 
     * @param idTipoPrestamo identificador del tipo de préstamo a buscar
     * @return Mono con el tipo de préstamo si existe, Mono.empty() si no existe
     * @throws IllegalArgumentException si el idTipoPrestamo es null
     */
    Mono<TipoPrestamo> findById(TipoPrestamoId idTipoPrestamo);
    
    /**
     * Busca un tipo de préstamo por su nombre exacto.
     * 
     * @param nombre nombre del tipo de préstamo a buscar
     * @return Mono con el tipo de préstamo si existe, Mono.empty() si no existe
     * @throws IllegalArgumentException si el nombre es null o vacío
     */
    Mono<TipoPrestamo> findByNombre(String nombre);
    
    /**
     * Busca tipos de préstamo cuyo nombre contenga el texto especificado.
     * La búsqueda es case-insensitive.
     * 
     * @param nombreParcial texto parcial para buscar en nombres
     * @return Flux con los tipos de préstamo que coinciden con el criterio
     * @throws IllegalArgumentException si el nombreParcial es null
     */
    Flux<TipoPrestamo> findByNombreContaining(String nombreParcial);
    
    /**
     * Busca tipos de préstamo que permitan un monto específico.
     * Retorna todos los tipos donde el monto esté dentro del rango permitido.
     * 
     * @param monto monto a verificar
     * @return Flux con los tipos de préstamo que permiten el monto especificado
     * @throws IllegalArgumentException si el monto es null
     */
    Flux<TipoPrestamo> findByMontoPermitido(Monto monto);
    
    /**
     * Busca tipos de préstamo que requieran validación automática.
     * 
     * @param requiereValidacion true para tipos con validación automática, false para los que no
     * @return Flux con los tipos de préstamo que coinciden con el criterio
     */
    Flux<TipoPrestamo> findByValidacionAutomatica(boolean requiereValidacion);
    
    /**
     * Busca tipos de préstamo con tasa de interés dentro de un rango específico.
     * 
     * @param tasaMinima tasa de interés mínima (inclusive)
     * @param tasaMaxima tasa de interés máxima (inclusive)
     * @return Flux con los tipos de préstamo que coinciden con el rango de tasas
     * @throws IllegalArgumentException si las tasas son null o la mínima es mayor que la máxima
     */
    Flux<TipoPrestamo> findByRangoTasaInteres(double tasaMinima, double tasaMaxima);
    
    /**
     * Obtiene todos los tipos de préstamo disponibles.
     * 
     * @return Flux con todos los tipos de préstamo ordenados por nombre
     */
    Flux<TipoPrestamo> findAll();
    
    /**
     * Obtiene todos los tipos de préstamo ordenados por un criterio específico.
     * 
     * @param ordenCriterio criterio de ordenamiento ("nombre", "montoMinimo", "montoMaximo", "tasaInteres")
     * @param ascendente true para orden ascendente, false para descendente
     * @return Flux con los tipos de préstamo ordenados
     * @throws IllegalArgumentException si el criterio de orden es inválido
     */
    Flux<TipoPrestamo> findAllOrderedBy(String ordenCriterio, boolean ascendente);
    
    /**
     * Obtiene tipos de préstamo con paginación.
     * 
     * @param pagina número de página (base 0)
     * @param tamanoPagina tamaño de cada página
     * @return Flux con los tipos de préstamo para la página especificada
     * @throws IllegalArgumentException si la página o tamaño son inválidos
     */
    Flux<TipoPrestamo> findAllPaginated(int pagina, int tamanoPagina);
    
    /**
     * Verifica si existe un tipo de préstamo con el identificador especificado.
     * 
     * @param idTipoPrestamo identificador del tipo de préstamo a verificar
     * @return Mono<Boolean> con true si existe, false en caso contrario
     * @throws IllegalArgumentException si el idTipoPrestamo es null
     */
    Mono<Boolean> existsById(TipoPrestamoId idTipoPrestamo);
    
    /**
     * Verifica si existe un tipo de préstamo con el nombre especificado.
     * 
     * @param nombre nombre del tipo de préstamo a verificar
     * @return Mono<Boolean> con true si existe, false en caso contrario
     * @throws IllegalArgumentException si el nombre es null o vacío
     */
    Mono<Boolean> existsByNombre(String nombre);
    
    /**
     * Verifica si existe algún tipo de préstamo que permita un monto específico.
     * 
     * @param monto monto a verificar
     * @return Mono<Boolean> con true si existe al menos un tipo que permita el monto, false en caso contrario
     * @throws IllegalArgumentException si el monto es null
     */
    Mono<Boolean> existsByMontoPermitido(Monto monto);
    
    /**
     * Elimina un tipo de préstamo por su identificador.
     * 
     * @param idTipoPrestamo identificador del tipo de préstamo a eliminar
     * @return Mono<Boolean> con true si se eliminó correctamente, false si no existía
     * @throws IllegalArgumentException si el idTipoPrestamo es null
     */
    Mono<Boolean> deleteById(TipoPrestamoId idTipoPrestamo);
    
    /**
     * Cuenta el número total de tipos de préstamo en la base de datos.
     * 
     * @return Mono<Long> con el número total de tipos de préstamo
     */
    Mono<Long> count();
    
    /**
     * Cuenta tipos de préstamo que requieren validación automática.
     * 
     * @param requiereValidacion true para contar tipos con validación automática, false para los que no
     * @return Mono<Long> con el número de tipos de préstamo que coinciden con el criterio
     */
    Mono<Long> countByValidacionAutomatica(boolean requiereValidacion);
    
    /**
     * Busca tipos de préstamo que coincidan con múltiples criterios.
     * 
     * @param nombreParcial texto parcial para buscar en nombres (opcional)
     * @param requiereValidacionAutomatica filtro por validación automática (opcional)
     * @param montoMinimo filtro por monto mínimo (opcional)
     * @param montoMaximo filtro por monto máximo (opcional)
     * @return Flux con los tipos de préstamo que coinciden con los criterios especificados
     */
    Flux<TipoPrestamo> findByCriterios(String nombreParcial, 
                                      Boolean requiereValidacionAutomatica,
                                      Monto montoMinimo, 
                                      Monto montoMaximo);
    
    /**
     * Obtiene los tipos de préstamo más populares basado en el número de solicitudes.
     * 
     * @param limite número máximo de tipos de préstamo a retornar
     * @return Flux con los tipos de préstamo ordenados por popularidad
     * @throws IllegalArgumentException si el límite es inválido
     */
    Flux<TipoPrestamo> findMasPopulares(int limite);
    
    /**
     * Busca tipos de préstamo activos (no eliminados) que estén disponibles para nuevas solicitudes.
     * 
     * @return Flux con los tipos de préstamo activos
     */
    Flux<TipoPrestamo> findActivos();
}
