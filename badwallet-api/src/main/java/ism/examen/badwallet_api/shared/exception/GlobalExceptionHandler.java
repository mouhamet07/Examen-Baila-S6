package ism.examen.badwallet_api.shared.exception;

import ism.examen.badwallet_api.shared.response.RestResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityExistException.class)
    public ResponseEntity<RestResponse<Void>> handleEntityExist(EntityExistException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(RestResponse.error(exception.getMessage(), HttpStatus.CONFLICT));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<RestResponse<Void>> handleEntityNotFound(EntityNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(RestResponse.error(exception.getMessage(), HttpStatus.NOT_FOUND));
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<RestResponse<Void>> handleBadRequest(BadRequestException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(RestResponse.error(exception.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RestResponse<Void>> handleDataIntegrityViolation() {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(RestResponse.error("Wallet existe deja.", HttpStatus.CONFLICT));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<RestResponse<Void>> handleMissingRequestParameter(
            MissingServletRequestParameterException exception
    ) {
        return ResponseEntity
                .badRequest()
                .body(RestResponse.error("Parametre manquant: " + exception.getParameterName(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RestResponse<Void>> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity
                .badRequest()
                .body(RestResponse.error(exception.getMessage(), HttpStatus.BAD_REQUEST));
    }
}
