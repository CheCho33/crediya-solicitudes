package co.com.crediya.solicitudes.api.dto;

public record SolicitudResponse(
    
    Long id,
    
    Double monto,
    
    Double plazo,
    
    String email,
    
    Long estado,
    
    long idTipoPrestamo
    

) {

}
