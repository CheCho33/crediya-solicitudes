package co.com.crediya.solicitudes.model.exceptions;

/**
 * Excepción base para todas las excepciones del dominio.
 * Proporciona una estructura común para el manejo de errores de negocio.
 */
public class CrediYautentiateException extends RuntimeException {

    /**
     * Constructor con mensaje y código de error.
     * 
     * @param message Mensaje descriptivo del error
     */
    public CrediYautentiateException(String message) {
        super(message);
    }
}
