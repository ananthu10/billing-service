package com.billing.billing_service.exceptions;


public class InvoiceNotApprovedException extends RuntimeException {
    public InvoiceNotApprovedException(String message) {
        super(String.valueOf(message));
    }
}