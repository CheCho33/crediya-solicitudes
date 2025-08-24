# Adaptador R2DBC para EstadosRepository

## Descripción

Este adaptador implementa el gateway `EstadosRepository` del dominio para la persistencia de la entidad usando R2DBC con PostgreSQL. Sigue los principios de Arquitectura Hexagonal y programación reactiva.

## Estructura del Adaptador

```
r2dbc-postgresql/
├── src/main/java/co/com/crediya/solicitudes/r2dbc/
│   ├── adapters/
│   │   └── EstadosRepositoryAdapter.java          # Implementación del gateway
│   ├── mapper/
│   │   └── EstadosInfraMapper.java                # Conversión dominio ↔ persistencia
│   ├── model/
│   │   └── EstadosData.java                       # Modelo de persistencia
│   └── repository/
│       └── EstadosReactiveRepository.java         # Repositorio R2DBC
├── src/test/java/co/com/crediya/solicitudes/r2dbc/
│   ├── adapters/
│   │   └── EstadosRepositoryAdapterTest.java      # Tests del adaptador
│   └── mapper/
│       └── EstadosInfraMapperTest.java            # Tests del mapper
└── src/main/resources/sql/
    └── V1__create_estados_table.sql               # Migración de base de datos
```

## Componentes

### 1. EstadosRepositoryAdapter

**Responsabilidad**: Implementa el gateway `EstadosRepository` del dominio.

**Características**:
- Programación reactiva pura con Project Reactor
- Logging estructurado para observabilidad
- Validación de entrada
- Manejo de errores técnicos
- No contiene lógica de negocio

**Métodos principales**:
- `save(Estados)`: Guarda un nuevo estado
- `update(Estados)`: Actualiza un estado existente con control de concurrencia
- `findById(EstadoId)`: Busca por identificador único
- `findByNombre(String)`: Búsqueda por nombre exacto
- `findAll()`: Obtiene todos los estados activos
- `deleteById(EstadoId)`: Eliminación suave (soft delete)

### 2. EstadosInfraMapper

**Responsabilidad**: Conversión bidireccional entre modelos de dominio y persistencia.

**Características**:
- Conversión pura sin side effects
- Validación de datos de entrada
- Manejo seguro de valores null
- Idempotencia en conversiones bidireccionales

**Métodos**:
- `toData(Estados)`: Dominio → Persistencia
- `toDomain(EstadosData)`: Persistencia → Dominio
- `toEstadoId(UUID)`: UUID → EstadoId
- `toUUID(EstadoId)`: EstadoId → UUID

### 3. EstadosData

**Responsabilidad**: Modelo de datos para persistencia en PostgreSQL.

**Características**:
- Record inmutable de Java 21
- Anotaciones de Spring Data R2DBC
- Métodos de conveniencia para transformaciones
- No contiene lógica de negocio

**Campos**:
- `idEstado`: UUID primario
- `nombre`: Nombre del estado (único)
- `descripcion`: Descripción detallada
- `version`: Control de concurrencia optimista
- `fechaCreacion`: Timestamp de creación
- `fechaActualizacion`: Timestamp de última actualización
- `activo`: Soft delete flag

### 4. EstadosReactiveRepository

**Responsabilidad**: Operaciones de base de datos usando R2DBC.

**Características**:
- Queries nativas optimizadas
- Parametrización obligatoria
- Índices para rendimiento
- Soft delete implementado
- Búsquedas case-insensitive

**Queries principales**:
- Búsqueda por nombre exacto
- Búsqueda por texto parcial
- Paginación
- Ordenamiento dinámico
- Filtros por criterios múltiples

## Base de Datos

### Tabla: `estados`

```sql
CREATE TABLE estados (
    id_estado UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT true,
    
    CONSTRAINT uk_estados_nombre UNIQUE (nombre),
    CONSTRAINT ck_estados_nombre_not_empty CHECK (nombre != ''),
    CONSTRAINT ck_estados_version_positive CHECK (version >= 0)
);
```

### Índices

- `idx_estados_activo`: Para filtrar estados activos
- `idx_estados_nombre_lower`: Para búsquedas case-insensitive
- `idx_estados_fecha_creacion`: Para ordenamiento por fecha

### Datos Iniciales

El script de migración incluye estados comunes del sistema:
- PENDIENTE_REVISION
- REVISION_MANUAL
- APROBADA
- RECHAZADA
- CANCELADA
- DESEMBOLSADA
- EN_MORA
- PAGADO

## Testing

### Cobertura de Tests

- **EstadosRepositoryAdapterTest**: Tests unitarios del adaptador
- **EstadosInfraMapperTest**: Tests del mapper con casos límite
- Cobertura objetivo: >85%

### Casos de Prueba

**Adaptador**:
- Operaciones CRUD exitosas
- Manejo de errores de validación
- Flujos reactivos correctos
- Logging estructurado

**Mapper**:
- Conversión bidireccional
- Validación de datos inválidos
- Casos límite (null, vacío, caracteres especiales)
- Idempotencia

## Configuración

### Dependencias Requeridas

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-r2dbc'
    implementation 'org.postgresql:r2dbc-postgresql'
    implementation 'org.flywaydb:flyway-core'
    implementation 'org.flywaydb:flyway-database-postgresql'
    
    testImplementation 'org.testcontainers:postgresql'
    testImplementation 'org.testcontainers:junit-jupiter'
}
```

### Properties

```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/crediya_solicitudes
    username: postgres
    password: password
  
  flyway:
    enabled: true
    locations: classpath:sql
    baseline-on-migrate: true
```

## Uso

### Inyección de Dependencias

```java
@Service
public class EstadosService {
    private final EstadosRepository estadosRepository;
    
    public EstadosService(EstadosRepository estadosRepository) {
        this.estadosRepository = estadosRepository;
    }
    
    public Mono<Estados> crearEstado(String nombre, String descripcion) {
        EstadoId id = EstadoId.random();
        Estados estado = Estados.create(id, nombre, descripcion);
        return estadosRepository.save(estado);
    }
}
```

### Ejemplos de Uso

```java
// Crear estado
Estados nuevoEstado = Estados.create(EstadoId.random(), "NUEVO_ESTADO", "Descripción");
Mono<Estados> guardado = estadosRepository.save(nuevoEstado);

// Buscar por nombre
Mono<Estados> encontrado = estadosRepository.findByNombre("PENDIENTE_REVISION");

// Listar todos activos
Flux<Estados> todos = estadosRepository.findAll();

// Búsqueda con filtros
Flux<Estados> filtrados = estadosRepository.findByCriterios("PENDIENTE", "revisión");

// Paginación
Flux<Estados> paginados = estadosRepository.findAllPaginated(0, 10);
```

## Observabilidad

### Logging Estructurado

El adaptador incluye logging estructurado con el formato:
```
event=estados.save action=create estadoId=uuid status=success
```

**Eventos registrados**:
- `estados.save`: Creación de estados
- `estados.update`: Actualización de estados
- `estados.findById`: Búsqueda por ID
- `estados.findByNombre`: Búsqueda por nombre
- `estados.deleteById`: Eliminación de estados

### Métricas Recomendadas

- `adapter.estados.latency`: Latencia de operaciones
- `adapter.estados.errors`: Contador de errores por tipo
- `adapter.estados.operations`: Contador de operaciones por tipo

## Reglas de Desarrollo

### Principios Aplicados

1. **Inversión de Dependencias**: El adaptador implementa el puerto del dominio
2. **Programación Reactiva**: Uso exclusivo de Mono/Flux
3. **Queries Nativas**: Optimización de rendimiento con SQL nativo
4. **Soft Delete**: Eliminación lógica sin pérdida de datos
5. **Control de Concurrencia**: Versiones para optimistic locking
6. **Validación**: Validación de entrada en todos los métodos
7. **Observabilidad**: Logging estructurado y métricas

### Anti-Patrones Evitados

- ❌ No usar `block()` o `subscribe()`
- ❌ No concatenar strings en SQL
- ❌ No exponer modelos de infraestructura
- ❌ No incluir lógica de negocio
- ❌ No usar `SELECT *`
- ❌ No retornar `null` en APIs públicas

### Checklist de PR

- [ ] Tests unitarios con cobertura >85%
- [ ] Queries nativas parametrizadas
- [ ] Logging estructurado implementado
- [ ] Validación de entrada completa
- [ ] Manejo de errores técnicos
- [ ] Documentación actualizada
- [ ] Migración de base de datos incluida

## Evolución

### Versionado

- Cambios incompatibles requieren nueva versión del gateway
- Migraciones de base de datos con Flyway
- Documentación de breaking changes

### Migración de Datos

Para cambios estructurales:
1. Crear nueva migración Flyway
2. Implementar lógica de migración de datos
3. Actualizar tests de integración
4. Documentar cambios en ADR

## Troubleshooting

### Problemas Comunes

1. **Error de conexión R2DBC**: Verificar configuración de base de datos
2. **Queries lentas**: Revisar índices y EXPLAIN ANALYZE
3. **Errores de concurrencia**: Verificar control de versiones
4. **Logs excesivos**: Ajustar niveles de logging

### Debugging

```java
// Habilitar SQL logging
logging.level.org.springframework.r2dbc=DEBUG

// Habilitar logs del adaptador
logging.level.co.com.crediya.solicitudes.r2dbc.adapters=DEBUG
```
