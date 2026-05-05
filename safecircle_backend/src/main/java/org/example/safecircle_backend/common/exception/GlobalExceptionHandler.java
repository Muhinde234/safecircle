package org.example.safecircle_backend.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value={IllegalArgumentException.class})
    public Map<String,String> handleIllegalArgumentExceptions (IllegalArgumentException ex){

        Map<String,String> errorResponse = new HashMap<>();

        errorResponse.put("Timestamp", Instant.now().toString());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
        errorResponse.put("message", ex.getMessage());

        return errorResponse;
    }
}
