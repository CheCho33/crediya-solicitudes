package co.com.crediya.solicitudes.r2dbc.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para habilitar las propiedades de R2DBC PostgreSQL.
 * Esta clase permite que Spring Boot lea las propiedades de configuración
 * definidas en application.yaml con el prefijo "adapters.r2dbc".
 */
@Configuration
@EnableConfigurationProperties(PostgresqlConnectionProperties.class)
public class R2dbcConfig {
}
