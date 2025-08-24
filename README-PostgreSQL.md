# Configuración de PostgreSQL para CrediYa Solicitudes

## 📋 Configuración Actual

La aplicación está configurada para conectarse a PostgreSQL con los siguientes parámetros:

- **Host**: localhost
- **Puerto**: 5438
- **Base de datos**: dbCrediYa
- **Usuario**: postgres
- **Contraseña**: root
- **Schema**: public

## 🗄️ Configuración de la Base de Datos

### 1. Crear la Base de Datos

```sql
-- Conectar como superusuario (postgres)
CREATE DATABASE dbCrediYa;
```

### 2. Conectar a la Base de Datos

```bash
psql -h localhost -p 5438 -U postgres -d dbCrediYa
```

### 3. Ejecutar el Script de Inicialización

```sql
-- Crear extensión para UUIDs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Tabla de estados
CREATE TABLE IF NOT EXISTS estados (
    id_estado UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de tipos de préstamo
CREATE TABLE IF NOT EXISTS tipos_prestamo (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(100) NOT NULL UNIQUE,
    monto_minimo DECIMAL(15,2) NOT NULL,
    monto_maximo DECIMAL(15,2) NOT NULL,
    tasa_interes DECIMAL(5,2) NOT NULL,
    validacion_automatica BOOLEAN DEFAULT TRUE,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_monto_minimo_positivo CHECK (monto_minimo > 0),
    CONSTRAINT chk_monto_maximo_mayor_minimo CHECK (monto_maximo > monto_minimo),
    CONSTRAINT chk_tasa_interes_positiva CHECK (tasa_interes > 0)
);

-- Tabla de solicitudes
CREATE TABLE IF NOT EXISTS solicitudes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    monto DECIMAL(15,2) NOT NULL,
    plazo_meses INTEGER NOT NULL,
    email VARCHAR(254) NOT NULL,
    id_estado UUID NOT NULL,
    id_tipo_prestamo UUID NOT NULL,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_solicitud_estado FOREIGN KEY (id_estado) REFERENCES estados(id_estado),
    CONSTRAINT fk_solicitud_tipo_prestamo FOREIGN KEY (id_tipo_prestamo) REFERENCES tipos_prestamo(id),
    CONSTRAINT chk_monto_positivo CHECK (monto > 0),
    CONSTRAINT chk_plazo_positivo CHECK (plazo_meses > 0),
    CONSTRAINT chk_email_valido CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- Índices para mejorar el rendimiento
CREATE INDEX IF NOT EXISTS idx_solicitudes_email ON solicitudes(email);
CREATE INDEX IF NOT EXISTS idx_solicitudes_estado ON solicitudes(id_estado);
CREATE INDEX IF NOT EXISTS idx_solicitudes_tipo_prestamo ON solicitudes(id_tipo_prestamo);
CREATE INDEX IF NOT EXISTS idx_solicitudes_created_at ON solicitudes(created_at);

-- Insertar datos iniciales
INSERT INTO estados (nombre, descripcion) VALUES
    ('Pendiente de revisión', 'Solicitud pendiente de revisión por asesor'),
    ('Revisión manual', 'Solicitud en proceso de revisión manual'),
    ('Aprobada', 'Solicitud aprobada'),
    ('Rechazada', 'Solicitud rechazada')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO tipos_prestamo (nombre, monto_minimo, monto_maximo, tasa_interes, validacion_automatica) VALUES
    ('Préstamo Personal', 1000000.00, 10000000.00, 15.50, TRUE),
    ('Préstamo Vehículo', 5000000.00, 50000000.00, 12.80, TRUE),
    ('Préstamo Vivienda', 20000000.00, 200000000.00, 10.20, FALSE),
    ('Microcrédito', 500000.00, 2000000.00, 18.00, TRUE)
ON CONFLICT (nombre) DO NOTHING;

-- Función para actualizar timestamps
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers para updated_at
CREATE TRIGGER update_estados_updated_at BEFORE UPDATE ON estados
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tipos_prestamo_updated_at BEFORE UPDATE ON tipos_prestamo
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_solicitudes_updated_at BEFORE UPDATE ON solicitudes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
```

## ⚙️ Configuración de la Aplicación

### Archivo de Configuración

La configuración está en `applications/app-service/src/main/resources/application.yaml`:

```yaml
# Configuración de PostgreSQL
adapters:
  r2dbc:
    host: localhost
    port: 5438
    database: dbCrediYa
    schema: public
    username: postgres
    password: root
```

### Pool de Conexiones

La configuración del pool de conexiones está en `PostgreSQLConnectionPool.java`:

- **Tamaño inicial**: 5 conexiones
- **Tamaño máximo**: 20 conexiones
- **Tiempo máximo de inactividad**: 30 minutos
- **Puerto por defecto**: 5438

## 🔧 Verificación de la Conexión

### 1. Verificar que PostgreSQL esté ejecutándose

```bash
# Verificar si PostgreSQL está ejecutándose en el puerto 5438
netstat -an | grep 5438
```

### 2. Probar la conexión

```bash
# Conectar desde línea de comandos
psql -h localhost -p 5438 -U postgres -d dbCrediYa
```

### 3. Verificar las tablas

```sql
-- Listar las tablas creadas
\dt

-- Verificar los datos iniciales
SELECT * FROM estados;
SELECT * FROM tipos_prestamo;
```

## 🚀 Ejecutar la Aplicación

Una vez configurada la base de datos, puedes ejecutar la aplicación:

```bash
./gradlew :app-service:bootRun
```

La aplicación se conectará automáticamente a PostgreSQL usando la configuración especificada.

## 🔍 Troubleshooting

### Error de Conexión

Si hay problemas de conexión, verificar:

1. **PostgreSQL ejecutándose**: `pg_ctl status`
2. **Puerto correcto**: `netstat -an | grep 5438`
3. **Credenciales**: Verificar usuario y contraseña
4. **Base de datos creada**: `psql -l` para listar bases de datos

### Error de Permisos

Si hay problemas de permisos:

```sql
-- Conectar como superusuario
GRANT ALL PRIVILEGES ON DATABASE dbCrediYa TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres;
```

## 📊 Estructura de la Base de Datos

### Tabla `estados`
- Almacena los diferentes estados de las solicitudes
- Estados iniciales: Pendiente de revisión, Revisión manual, Aprobada, Rechazada

### Tabla `tipos_prestamo`
- Almacena los tipos de préstamo disponibles
- Incluye rangos de montos y tasas de interés
- Tipos iniciales: Personal, Vehículo, Vivienda, Microcrédito

### Tabla `solicitudes`
- Tabla principal que almacena las solicitudes de préstamo
- Relacionada con estados y tipos de préstamo
- Incluye validaciones de monto, plazo y email
