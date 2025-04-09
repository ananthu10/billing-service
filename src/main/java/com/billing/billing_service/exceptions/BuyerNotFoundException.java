package com.billing.billing_service.exceptions;

public class BuyerNotFoundException extends RuntimeException {
    public BuyerNotFoundException(Long message) {
        super(String.valueOf(message));
    }
}