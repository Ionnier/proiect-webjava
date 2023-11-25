package org.dbahrim.forum.controllers;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ErrorController
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value
            = {ConstraintViolationException.class})
    protected ResponseEntity<Object> handleConstraintViolation (
            ConstraintViolationException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getConstraintViolations().stream().collect(Collectors.toMap(
                        ConstraintViolation::getPropertyPath, ConstraintViolation::getMessage)),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value
            = {ExpiredJwtException.class})
    protected ResponseEntity<Object> handleConstraintViolation (
            ExpiredJwtException ex, WebRequest request) {
        return handleExceptionInternal(ex, "JWT Expired",
                new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(value
            = {NotFoundException.class})
    protected ResponseEntity<Object> notFound (
            NotFoundException ex, WebRequest request) {
        return handleExceptionInternal(ex, null,
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
    
    public static class NotFoundException extends Exception {
        
    }
}
