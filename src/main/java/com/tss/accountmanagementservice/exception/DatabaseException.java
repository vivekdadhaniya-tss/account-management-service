package com.tss.accountmanagementservice.exception;

import org.springframework.http.HttpStatus;

public class DatabaseException extends ApplicationException {
    public DatabaseException(String message) {
        super(message, "DATABASE_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

