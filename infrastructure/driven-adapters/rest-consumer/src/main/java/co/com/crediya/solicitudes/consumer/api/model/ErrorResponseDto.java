package co.com.crediya.solicitudes.consumer.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
* ErrorResponseDto
*/

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {
    private String timestamp;
    private String error;
    private String message;
}