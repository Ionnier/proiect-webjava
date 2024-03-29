package org.dbahrim.forum.controllers;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex, ex.getDetailMessageArguments(),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value
            = {ExpiredJwtException.class})
    protected ResponseEntity<Object> handleConstraintViolation (
            ExpiredJwtException ex, WebRequest request) {
        return handleExceptionInternal(ex, "JWT Expired",
                new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(value = {SimpleCodeSetter.class})
    protected ResponseEntity<Object> simpleCodeSetter (
            SimpleCodeSetter ex, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex instanceof NotFoundException) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (ex instanceof BadRequest) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof Forbidden) {
            httpStatus = HttpStatus.FORBIDDEN;
        }
        return handleExceptionInternal(ex, null,
                new HttpHeaders(), httpStatus, request);
    }

    @ExceptionHandler(value = {MessagedException.class})
    protected ResponseEntity<Object> simpleCodeSetter (
            MessagedException ex, WebRequest request) {
        return handleExceptionInternal(ex, new MessageClass(ex.message),
                new HttpHeaders(), ex.httpStatus, request);
    }

    private abstract static class SimpleCodeSetter extends Exception {};
    public static class NotFoundException extends SimpleCodeSetter { }
    public static class BadRequest extends SimpleCodeSetter { }
    public static class Forbidden extends SimpleCodeSetter { }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageClass {
        public String message;
    }

    @AllArgsConstructor
    public static class MessagedException extends Exception {
        public String message;
        public HttpStatus httpStatus;
    }
}
