package com.billing.billing_service.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", status.value());
        error.put("error", status.getReasonPhrase());
        error.put("message", message);
        return error;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Duplicate entry or constraint violation: " + ex.getMostSpecificCause().getMessage();
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.CONFLICT, message), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvoiceAlreadyPublishedException.class)
    public ResponseEntity<Map<String, Object>> handleInvoiceAlreadyPublished(InvoiceAlreadyPublishedException ex) {
        String message = "invoice already published: " + ex.getMessage();
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.FORBIDDEN, message), HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(InvoiceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleInvoiceAlreadyPublished(InvoiceNotFoundException ex) {
        String message = "Invoice not found: " + ex.getMessage();
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.NOT_FOUND, message), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(BuyerNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleBuyerNotFoundException(BuyerNotFoundException ex) {
        String message = "Buyer not found: " + ex.getMessage();
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.NOT_FOUND, message), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotPermitedException.class)
    public ResponseEntity<Map<String, Object>> handleNotPermitedException(NotPermitedException ex) {
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.FORBIDDEN,  ex.getMessage()), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(InvoiceNotApprovedException.class)
    public ResponseEntity<Map<String, Object>> handleInvoiceNotApprovedException(InvoiceNotApprovedException ex) {
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.FORBIDDEN,  ex.getMessage()), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(NotPermitedException ex) {
        return new ResponseEntity<>(buildErrorResponse(HttpStatus.FORBIDDEN, "You do not have the required permissions to perform this action."), HttpStatus.FORBIDDEN);
    }

}

