package com.billing.billing_service.exceptions;

public class InvoiceNotFoundException extends RuntimeException {
    public InvoiceNotFoundException(Long message) {
        super(String.valueOf(message));
    }
}