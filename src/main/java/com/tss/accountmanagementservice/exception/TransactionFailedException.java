package com.tss.accountmanagementservice.exception;

import org.springframework.http.HttpStatus;

public class TransactionFailedException extends ApplicationException {
    public TransactionFailedException(String message) {
        super(message, "TRANSACTION_FAILED", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

