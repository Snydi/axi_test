package com.axi.loan.application;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationSubmissionExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new ValidationErrorResponse(
                "Проверьте заполнение полей", errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ValidationErrorResponse> handleUnreadableRequest() {
        return ResponseEntity.badRequest().body(new ValidationErrorResponse(
                "Некорректный формат данных", Map.of()));
    }

    @ExceptionHandler(ApplicationSubmissionConflictException.class)
    ResponseEntity<ValidationErrorResponse> handleConflict(ApplicationSubmissionConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidationErrorResponse(
                exception.getMessage(), Map.of(exception.getField(), exception.getMessage())));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ValidationErrorResponse> handleDataIntegrityViolation() {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidationErrorResponse(
                "Клиент с такими уникальными данными уже существует", Map.of()));
    }

    public record ValidationErrorResponse(String message, Map<String, String> errors) {
    }
}
