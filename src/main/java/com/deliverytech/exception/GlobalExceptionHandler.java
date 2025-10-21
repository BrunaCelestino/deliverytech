package com.deliverytech.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(MethodArgumentNotValidException.class) 
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> details = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            details.put(error.getField(), error.getDefaultMessage());

        });

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(), 
            "Erro de validação", 
            "Campos inválidos na requisição",
             request.getDescription(false).replace("uri=", ""), 
             details);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class) 
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(EntityNotFoundException ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(), 
            "Recurso não encontrado", 
            ex.getMessage(),
             request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class) 
    public ResponseEntity<ErrorResponse> handleConflictExceptions(ConflictException ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.CONFLICT.value(), 
            "Conflito de dados", 
            ex.getMessage(),
             request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

      @ExceptionHandler(Exception.class) 
    public ResponseEntity<ErrorResponse> handleGenericExceptions(Exception ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), 
            "Erro interno no servidor", 
            "Ocorreu um erro inesperado. Tente novamente mais tarde.",
             request.getDescription(false).replace("uri=", ""));

        log.error("Exceção inesperada: ", ex);
             
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
