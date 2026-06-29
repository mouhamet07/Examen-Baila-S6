package ism.examen.badwallet_api.shared.response;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public record RestResponse<T>(
        boolean success,
        int status,
        String message,
        T data,
        LocalDateTime timestamp
) {
    public RestResponse(boolean success, HttpStatus status, String message, T data) {
        this(success, status.value(), message, data, LocalDateTime.now());
    }

    public static <T> RestResponse<T> success(String message, T data) {
        return new RestResponse<>(true, HttpStatus.OK, message, data);
    }

    public static <T> RestResponse<T> error(String message, HttpStatus status) {
        return new RestResponse<>(false, status, message, null);
    }
}
