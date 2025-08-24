package co.com.crediya.solicitudes.model.estados;

/**
 * Entidad que representa un estado en el sistema de solicitudes.
 * Aggregate Root que encapsula la información de un estado con sus invariantes.
 */
public final class Estados {
    
    private final EstadoId idEstado;
    private final String nombre;
    private final String descripcion;
    private final long version;
    
    /**
     * Constructor privado para garantizar invariantes.
     * 
     * @param idEstado identificador único del estado
     * @param nombre nombre del estado
     * @param descripcion descripción del estado
     * @param version versión para control de concurrencia optimista
     */
    private Estados(EstadoId idEstado, String nombre, String descripcion, long version) {
        this.idEstado = idEstado;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.version = version;
        validateInvariants();
    }
    
    /**
     * Crea una nueva instancia de Estados.
     * 
     * @param idEstado identificador único del estado
     * @param nombre nombre del estado (no puede ser null o vacío)
     * @param descripcion descripción del estado (no puede ser null)
     * @return nueva instancia de Estados
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    public static Estados create(EstadoId idEstado, String nombre, String descripcion) {
        return new Estados(idEstado, nombre, descripcion, 0L);
    }
    
    /**
     * Crea una nueva instancia de Estados con versión específica.
     * Usado principalmente para reconstruir desde persistencia.
     * 
     * @param idEstado identificador único del estado
     * @param nombre nombre del estado
     * @param descripcion descripción del estado
     * @param version versión actual
     * @return nueva instancia de Estados
     */
    public static Estados fromPersistence(EstadoId idEstado, String nombre, String descripcion, long version) {
        return new Estados(idEstado, nombre, descripcion, version);
    }
    
    /**
     * Actualiza la descripción del estado.
     * 
     * @param nuevaDescripcion nueva descripción (no puede ser null)
     * @return nueva instancia con la descripción actualizada
     * @throws IllegalArgumentException si la descripción es inválida
     */
    public Estados actualizarDescripcion(String nuevaDescripcion) {
        if (nuevaDescripcion == null) {
            throw new IllegalArgumentException("Descripción no puede ser null");
        }
        return new Estados(this.idEstado, this.nombre, nuevaDescripcion, this.version);
    }
    
    /**
     * Actualiza el nombre del estado.
     * 
     * @param nuevoNombre nuevo nombre (no puede ser null o vacío)
     * @return nueva instancia con el nombre actualizado
     * @throws IllegalArgumentException si el nombre es inválido
     */
    public Estados actualizarNombre(String nuevoNombre) {
        if (nuevoNombre == null || nuevoNombre.isBlank()) {
            throw new IllegalArgumentException("Nombre no puede ser null o vacío");
        }
        return new Estados(this.idEstado, nuevoNombre, this.descripcion, this.version);
    }
    
    /**
     * Marca la entidad como persistida con una nueva versión.
     * 
     * @param nuevaVersion nueva versión (debe ser mayor que la actual)
     * @return nueva instancia con la versión actualizada
     * @throws IllegalArgumentException si la versión es inválida
     */
    public Estados markPersisted(long nuevaVersion) {
        if (nuevaVersion <= version) {
            throw new IllegalArgumentException("Nueva versión debe ser mayor que la actual");
        }
        return new Estados(this.idEstado, this.nombre, this.descripcion, nuevaVersion);
    }
    
    /**
     * Valida que todos los invariantes se cumplan.
     * 
     * @throws IllegalStateException si algún invariante no se cumple
     */
    private void validateInvariants() {
        if (idEstado == null) {
            throw new IllegalStateException("Identificador de estado requerido");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalStateException("Nombre de estado requerido");
        }
        if (descripcion == null) {
            throw new IllegalStateException("Descripción de estado requerida");
        }
        if (version < 0) {
            throw new IllegalStateException("Versión no puede ser negativa");
        }
    }
    
    /**
     * Verifica si el estado tiene un nombre específico.
     * 
     * @param nombreBuscado nombre a verificar
     * @return true si el nombre coincide (ignorando mayúsculas/minúsculas)
     */
    public boolean tieneNombre(String nombreBuscado) {
        if (nombreBuscado == null) {
            return false;
        }
        return nombre.equalsIgnoreCase(nombreBuscado);
    }
    
    /**
     * Verifica si la descripción contiene un texto específico.
     * 
     * @param texto texto a buscar en la descripción
     * @return true si la descripción contiene el texto (ignorando mayúsculas/minúsculas)
     */
    public boolean descripcionContiene(String texto) {
        if (texto == null) {
            return false;
        }
        return descripcion.toLowerCase().contains(texto.toLowerCase());
    }
    
    // Getters intencionales (no exponer estado mutable)
    
    /**
     * @return identificador único del estado
     */
    public EstadoId idEstado() {
        return idEstado;
    }
    
    /**
     * @return nombre del estado
     */
    public String nombre() {
        return nombre;
    }
    
    /**
     * @return descripción del estado
     */
    public String descripcion() {
        return descripcion;
    }
    
    /**
     * @return versión actual para control de concurrencia
     */
    public long version() {
        return version;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Estados estados = (Estados) obj;
        return idEstado.equals(estados.idEstado);
    }
    
    @Override
    public int hashCode() {
        return idEstado.hashCode();
    }
    
    @Override
    public String toString() {
        return "Estados{" +
                "idEstado=" + idEstado +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", version=" + version +
                '}';
    }
}
