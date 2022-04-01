package com.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class GenericExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public final ResponseEntity<GenericExceptionResponse> handleAllException(Exception ex, WebRequest request) throws Exception {
        GenericExceptionResponse response = new GenericExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<GenericExceptionResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public final ResponseEntity<GenericExceptionResponse> handleResourceNotFoundException(Exception ex, WebRequest request) throws Exception {
        GenericExceptionResponse response = new GenericExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<GenericExceptionResponse>(response, HttpStatus.NOT_FOUND);
    }
}