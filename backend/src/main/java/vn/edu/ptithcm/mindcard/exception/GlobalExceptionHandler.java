package vn.edu.ptithcm.mindcard.exception;

import java.util.HashMap;
import java.util.Map;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import vn.edu.ptithcm.mindcard.dto.response.common.APIResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<APIResponse<Void>> handleAppException(AppException exc){
        APIResponse<Void> response = APIResponse.error(exc.getErrorCode(), exc.getMessage());
        return ResponseEntity.status(exc.getErrorCode().getHttpStatusCode()).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<APIResponse<Void>> handleNotFound(NoResourceFoundException exc) {
        APIResponse<Void> response = APIResponse.error(ErrorCode.NOT_FOUND, exc.getMessage());
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Void>> handleValidationError(MethodArgumentNotValidException exc){
        Map<String, String> errorDetails = new HashMap<>();
        exc.getBindingResult()
                .getFieldErrors()
                .forEach(
                        err -> {errorDetails.putIfAbsent(err.getField(), err.getDefaultMessage());}

                );
        APIResponse<Void> response = APIResponse.error(ErrorCode.VALIDATION_ERROR, errorDetails);
        return ResponseEntity
                .status(ErrorCode.VALIDATION_ERROR.getHttpStatusCode())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<APIResponse<Void>> handleUnexpectedException(Exception exc){
        exc.printStackTrace();
        APIResponse<Void> response = APIResponse.error(ErrorCode.SERVER_ERROR, "Something went wrong: " + exc.getMessage());
        return ResponseEntity.status(500).body(response);
    }
}
