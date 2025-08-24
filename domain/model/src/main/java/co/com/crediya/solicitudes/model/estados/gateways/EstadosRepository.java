package co.com.crediya.solicitudes.model.estados.gateways;

import co.com.crediya.solicitudes.model.estados.EstadoId;
import co.com.crediya.solicitudes.model.estados.Estados;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Gateway para la persistencia de la entidad Estados.
 * Define los contratos para las operaciones de base de datos relacionadas con estados.
 * 
 * Este gateway sigue los principios de Arquitectura Hexagonal:
 * - Define contratos del dominio sin dependencias de infraestructura
 * - Permite diferentes implementaciones (R2DBC, JPA, etc.)
 * - Mantiene el dominio libre de detalles técnicos de persistencia
 * - Implementa programación reactiva con Project Reactor
 */
public interface EstadosRepository {
    
    /**
     * Guarda un nuevo estado en la base de datos.
     * 
     * @param estado entidad Estados a persistir
     * @return Mono con la entidad guardada con versión actualizada
     * @throws IllegalArgumentException si el estado es null
     */
    Mono<Estados> save(Estados estado);
    
    /**
     * Actualiza un estado existente en la base de datos.
     * Utiliza control de concurrencia optimista basado en versión.
     * 
     * @param estado entidad Estados a actualizar
     * @return Mono con la entidad actualizada con nueva versión
     * @throws IllegalArgumentException si el estado es null
     * @throws IllegalStateException si la versión no coincide (concurrencia)
     */
    Mono<Estados> update(Estados estado);
    
    /**
     * Busca un estado por su identificador único.
     * 
     * @param idEstado identificador del estado a buscar
     * @return Mono con el estado si existe, Mono.empty() si no existe
     * @throws IllegalArgumentException si el idEstado es null
     */
    Mono<Estados> findById(EstadoId idEstado);
    
    /**
     * Busca un estado por su nombre exacto.
     * 
     * @param nombre nombre del estado a buscar
     * @return Mono con el estado si existe, Mono.empty() si no existe
     * @throws IllegalArgumentException si el nombre es null o vacío
     */
    Mono<Estados> findByNombre(String nombre);
    
    /**
     * Busca estados cuyo nombre contenga el texto especificado.
     * La búsqueda es case-insensitive.
     * 
     * @param nombreParcial texto parcial para buscar en nombres
     * @return Flux con los estados que coinciden con el criterio
     * @throws IllegalArgumentException si el nombreParcial es null
     */
    Flux<Estados> findByNombreContaining(String nombreParcial);
    
    /**
     * Busca estados cuya descripción contenga el texto especificado.
     * La búsqueda es case-insensitive.
     * 
     * @param descripcionParcial texto parcial para buscar en descripciones
     * @return Flux con los estados que coinciden con el criterio
     * @throws IllegalArgumentException si la descripcionParcial es null
     */
    Flux<Estados> findByDescripcionContaining(String descripcionParcial);
    
    /**
     * Obtiene todos los estados disponibles.
     * 
     * @return Flux con todos los estados ordenados por nombre
     */
    Flux<Estados> findAll();
    
    /**
     * Obtiene todos los estados ordenados por un criterio específico.
     * 
     * @param ordenCriterio criterio de ordenamiento ("nombre", "descripcion", "id")
     * @param ascendente true para orden ascendente, false para descendente
     * @return Flux con los estados ordenados
     * @throws IllegalArgumentException si el criterio de orden es inválido
     */
    Flux<Estados> findAllOrderedBy(String ordenCriterio, boolean ascendente);
    
    /**
     * Obtiene estados con paginación.
     * 
     * @param pagina número de página (base 0)
     * @param tamanoPagina tamaño de cada página
     * @return Flux con los estados para la página especificada
     * @throws IllegalArgumentException si la página o tamaño son inválidos
     */
    Flux<Estados> findAllPaginated(int pagina, int tamanoPagina);
    
    /**
     * Verifica si existe un estado con el identificador especificado.
     * 
     * @param idEstado identificador del estado a verificar
     * @return Mono<Boolean> con true si existe, false en caso contrario
     * @throws IllegalArgumentException si el idEstado es null
     */
    Mono<Boolean> existsById(EstadoId idEstado);
    
    /**
     * Verifica si existe un estado con el nombre especificado.
     * 
     * @param nombre nombre del estado a verificar
     * @return Mono<Boolean> con true si existe, false en caso contrario
     * @throws IllegalArgumentException si el nombre es null o vacío
     */
    Mono<Boolean> existsByNombre(String nombre);
    
    /**
     * Elimina un estado por su identificador.
     * 
     * @param idEstado identificador del estado a eliminar
     * @return Mono<Boolean> con true si se eliminó correctamente, false si no existía
     * @throws IllegalArgumentException si el idEstado es null
     */
    Mono<Boolean> deleteById(EstadoId idEstado);
    
    /**
     * Cuenta el número total de estados en la base de datos.
     * 
     * @return Mono<Long> con el número total de estados
     */
    Mono<Long> count();
    
    /**
     * Busca estados que coincidan con múltiples criterios.
     * 
     * @param nombreParcial texto parcial para buscar en nombres (opcional)
     * @param descripcionParcial texto parcial para buscar en descripciones (opcional)
     * @return Flux con los estados que coinciden con los criterios especificados
     */
    Flux<Estados> findByCriterios(String nombreParcial, String descripcionParcial);
    
    /**
     * Busca estados activos (no eliminados) que estén disponibles para uso.
     * 
     * @return Flux con los estados activos
     */
    Flux<Estados> findActivos();
    
    /**
     * Busca estados por nombre exacto (case-insensitive).
     * 
     * @param nombre nombre del estado a buscar
     * @return Mono con el estado si existe, Mono.empty() si no existe
     * @throws IllegalArgumentException si el nombre es null o vacío
     */
    Mono<Estados> findByNombreIgnoreCase(String nombre);
    
    /**
     * Obtiene estados ordenados por fecha de creación.
     * 
     * @param ascendente true para orden ascendente, false para descendente
     * @return Flux con los estados ordenados por fecha de creación
     */
    Flux<Estados> findAllOrderedByFechaCreacion(boolean ascendente);
}
