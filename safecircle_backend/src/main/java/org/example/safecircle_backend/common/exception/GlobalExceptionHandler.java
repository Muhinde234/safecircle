package org.example.safecircle_backend.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.example.safecircle_backend.common.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value={IllegalArgumentException.class})
    public ApiErrorResponse handleIllegalArgumentExceptions (IllegalArgumentException ex, HttpServletRequest request){

        return ApiErrorResponse.builder()
                .timestamp(Instant.now().toString())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.toString())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value={MethodArgumentNotValidException.class})
    public ApiErrorResponse handleValidationExceptions (MethodArgumentNotValidException ex, HttpServletRequest request){
        String message = "Validation Failed";

        if(!ex.getBindingResult().getFieldErrors().isEmpty()){
            var fieldErrors = ex.getBindingResult().getFieldErrors().getFirst();
//            message = fieldErrors.getField() + " : " + fieldErrors.getDefaultMessage();
            message = fieldErrors.getDefaultMessage();

        }

        return ApiErrorResponse.builder()
                .timestamp(Instant.now().toString())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();
    }
}
