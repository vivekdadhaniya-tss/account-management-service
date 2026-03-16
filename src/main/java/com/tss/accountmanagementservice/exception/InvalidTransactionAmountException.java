package com.tss.accountmanagementservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidTransactionAmountException extends ApplicationException {
    public InvalidTransactionAmountException(String message) {
        super(message, "INVALID_TRANSACTION_AMOUNT", HttpStatus.BAD_REQUEST);
    }
}
