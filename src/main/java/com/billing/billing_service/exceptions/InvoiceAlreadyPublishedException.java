package com.billing.billing_service.exceptions;

public class InvoiceAlreadyPublishedException extends RuntimeException {
    public InvoiceAlreadyPublishedException(String message) {
        super(message);
    }
}