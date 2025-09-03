package co.com.crediya.solicitudes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = {
    "co.com.crediya.solicitudes.config",
    "co.com.crediya.solicitudes.r2dbc",
    "co.com.crediya.solicitudes.api",
    "co.com.crediya.solicitudes.consumer"
})
@ConfigurationPropertiesScan
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
