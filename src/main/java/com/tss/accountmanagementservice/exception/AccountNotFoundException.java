package com.tss.accountmanagementservice.exception;

import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends ApplicationException {
    public AccountNotFoundException(String identifier) {
        super("Account not found: " + identifier, "ACCOUNT_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
