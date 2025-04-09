package com.billing.billing_service.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleRuntimeException_ShouldReturnNotFound() {
        RuntimeException ex = new RuntimeException("Runtime issue");
        ResponseEntity<Map<String, Object>> response = handler.handleRuntimeException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Runtime issue", response.getBody().get("message"));
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        Exception ex = new Exception("Generic error");
        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Generic error", response.getBody().get("message"));
    }

    @Test
    void handleDataIntegrityViolation_ShouldReturnConflict() {
        SQLException sqlEx = new SQLException("Duplicate entry for key");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Constraint violation", sqlEx);

        ResponseEntity<Map<String, Object>> response = handler.handleDataIntegrityViolation(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().get("message").toString().contains("Duplicate entry"));
    }

    @Test
    void handleInvoiceAlreadyPublished_ShouldReturnForbidden() {
        InvoiceAlreadyPublishedException ex = new InvoiceAlreadyPublishedException("Invoice already published");
        ResponseEntity<Map<String, Object>> response = handler.handleInvoiceAlreadyPublished(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().get("message").toString().contains("invoice already published"));
    }

    @Test
    void handleInvoiceNotFound_ShouldReturnNotFound() {
        InvoiceNotFoundException ex = new InvoiceNotFoundException(1L);
        ResponseEntity<Map<String, Object>> response = handler.handleInvoiceAlreadyPublished(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().get("message").toString().contains("Invoice not found"));
    }

    @Test
    void handleBuyerNotFound_ShouldReturnNotFound() {
        BuyerNotFoundException ex = new BuyerNotFoundException(1L);
        ResponseEntity<Map<String, Object>> response = handler.handleBuyerNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().get("message").toString().contains("Buyer not found"));
    }

    @Test
    void handleNotPermittedException_ShouldReturnForbidden() {
        NotPermitedException ex = new NotPermitedException("Not permitted");
        ResponseEntity<Map<String, Object>> response = handler.handleNotPermitedException(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().get("message").toString().contains("Not permitted"));
    }

    @Test
    void handleInvoiceNotApprovedException_ShouldReturnForbidden() {
        InvoiceNotApprovedException ex = new InvoiceNotApprovedException("Invoice not approved");
        ResponseEntity<Map<String, Object>> response = handler.handleInvoiceNotApprovedException(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().get("message").toString().contains("Invoice not approved"));
    }

    @Test
    void handleAccessDeniedException_ShouldReturnForbidden() {
        NotPermitedException ex = new NotPermitedException("Access denied");
        ResponseEntity<Map<String, Object>> response = handler.handleAccessDeniedException(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().get("message").toString().contains("required permissions"));
    }
}
