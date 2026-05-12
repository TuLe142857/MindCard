package vn.edu.ptithcm.mindcard.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import org.springframework.web.servlet.resource.NoResourceFoundException;
import vn.edu.ptithcm.mindcard.common.response.APIResponse;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<APIResponse<Void>> handleNotFound(NoResourceFoundException exc) {
        APIResponse<Void> response = APIResponse.error(ErrorCode.NOT_FOUND, exc.getMessage());
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<APIResponse<Void>> handleUnexpectedException(Exception exc){
        exc.printStackTrace();
        APIResponse<Void> response = APIResponse.error(ErrorCode.SERVER_ERROR, "Something went wrong: " + exc.getMessage());
        return ResponseEntity.status(500).body(response);
    }
}
