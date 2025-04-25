package com.example.bookstore.controller;


import com.example.bookstore.dto.ErrorResponse;
import com.example.bookstore.exception.BookNotFoundException;
import com.example.bookstore.exception.RoleNotFoundException;
import com.example.bookstore.exception.UserAlreadyPresentException;
import com.example.bookstore.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class ExceptionController {
    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationErrors(MethodArgumentNotValidException ex) {
        var errorMessages = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(error -> error.getField() + " " + error.getDefaultMessage())
            .toList();
        return new ErrorResponse("Validation failed", errorMessages);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(BookNotFoundException.class)
    public ErrorResponse handleBookNotFoundException(BookNotFoundException ex) {
        return new ErrorResponse(ex.getMessage(), List.of());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RoleNotFoundException.class)
    public ErrorResponse handleRoleNotFoundException(RoleNotFoundException ex) {
        return new ErrorResponse(ex.getMessage(), List.of());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public ErrorResponse handleBadCredentialsException(BadCredentialsException ex) {
        return new ErrorResponse(ex.getMessage(), List.of());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserAlreadyPresentException.class)
    public ErrorResponse handleUserAlreadyPresentException(UserAlreadyPresentException ex) {
        return new ErrorResponse(ex.getMessage(), List.of());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ErrorResponse handleValidationExceptions(HandlerMethodValidationException ex) {
        return new ErrorResponse("Validation failed", ex.getParameterValidationResults()
                .stream()
                .flatMap(vr -> vr.getResolvableErrors().stream())
                .map(MessageSourceResolvable::getDefaultMessage)
                .toList());
    }
    
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ErrorResponse handleMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException ex) {
        return new ErrorResponse("Invalid media type accepted one is " + Constants.VERSION_1_HEADER, List.of());
    }

    // NOT working I have to investigate
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException ex) {
        return new ErrorResponse(ex.getMessage(), List.of());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception ex) {
        log.error("Error happened", ex);
        return new ErrorResponse("Internal server errors, check server logs", List.of());
    }

}
