package com.billing.billing_service.exceptions;

public class NotPermitedException extends RuntimeException {
    public NotPermitedException(String message) {
        super((message));
    }
}