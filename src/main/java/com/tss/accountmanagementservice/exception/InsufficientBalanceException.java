package com.tss.accountmanagementservice.exception;

import org.springframework.http.HttpStatus;

public class InsufficientBalanceException extends ApplicationException {
    public InsufficientBalanceException(String message) {
        super(message, "INSUFFICIENT_BALANCE", HttpStatus.PAYMENT_REQUIRED);
    }
}
