package co.com.crediya.solicitudes.usecase.solicitud;

import co.com.crediya.solicitudes.model.exceptions.CrediYautentiateException;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

public class SolicitudValidator {

    public static Mono<Solicitud> validar(Double monto,Double plazo,String email,Long idTipoPrestamo) {

        if (monto == null || monto <= 0) {
            return Mono.error(new CrediYautentiateException("El monto solicitado debe ser un número positivo"));
        }
        if (plazo == null || plazo <= 0) {
            return Mono.error(new CrediYautentiateException("El plazo en meses debe ser un número positivo"));
        }
        if (email == null || email.trim().isEmpty()) {
            return Mono.error(new CrediYautentiateException("El email del solicitante es obligatorio"));
        }
        if (idTipoPrestamo == null) {
            return Mono.error(new CrediYautentiateException("El identificador del tipo de préstamo es obligatorio"));
        }

        return Mono.just(new Solicitud(null, monto, plazo, email, null, idTipoPrestamo));

    }



}
