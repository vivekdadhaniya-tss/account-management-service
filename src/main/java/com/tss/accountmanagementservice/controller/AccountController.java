package com.tss.accountmanagementservice.controller;

import com.tss.accountmanagementservice.dto.*;
import com.tss.accountmanagementservice.service.AccountService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accountapp")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
        logger.info("AccountController initialized with dynamic dependency injection");
    }

    @GetMapping("/accounts")
    public ResponseEntity<AccountPageResponseDto> getAllAccounts(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "5") Integer pageSize) {

        logger.debug("Fetching all accounts with pageNumber: {} and pageSize: {}", pageNumber, pageSize);
        AccountPageResponseDto response = accountService.findAll(pageNumber, pageSize);
        logger.info("Successfully retrieved {} accounts from page {}", response.getContent().size(), pageNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/accounts/{accNumber}")
    public ResponseEntity<AccountResponseDto> getAccountByAccNumber(@PathVariable String accNumber) {
        logger.debug("Fetching account with number: {}", accNumber);
        AccountResponseDto response = accountService.findByAccNumber(accNumber);
        logger.info("Successfully retrieved account: {}", accNumber);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/accounts")
    public ResponseEntity<AccountResponseDto> createAccount(@Valid @RequestBody AccountRequestDto request) {
        logger.debug("Creating new account with request: {}", request);
        AccountResponseDto response = accountService.createAccount(request);
        logger.info("Successfully created account: {}", response.getAccNumber());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/accounts")
    public ResponseEntity<Void> deleteAccount(@RequestParam String accNumber) {
        logger.debug("Deleting account: {}", accNumber);
        accountService.deleteByAccNumber(accNumber);
        logger.info("Successfully deleted account: {}", accNumber);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/accounts")
    public ResponseEntity<AccountResponseDto> updateAccount(
            @RequestParam String accNumber,
            @Valid @RequestBody AccountUpdateDto request) {
        logger.debug("Updating account: {} with request: {}", accNumber, request);
        AccountResponseDto response = accountService.updateAccount(accNumber, request);
        logger.info("Successfully updated account: {}", accNumber);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/accounts/debit")
    public ResponseEntity<AccountResponseDto> debit(
            @RequestParam String accNumber,
            @RequestBody AccountTransactionDto transactionDto) {
        logger.debug("Processing debit for account: {} with amount: {}", accNumber, transactionDto.getAmount());
        AccountResponseDto response = accountService.debit(accNumber, transactionDto);
        logger.info("Successfully debited {} from account: {}", transactionDto.getAmount(), accNumber);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/accounts/credit")
    public ResponseEntity<AccountResponseDto> credit(
            @RequestParam String accNumber,
            @RequestBody AccountTransactionDto transactionDto) {
        logger.debug("Processing credit for account: {} with amount: {}", accNumber, transactionDto.getAmount());
        AccountResponseDto response = accountService.credit(accNumber, transactionDto);
        logger.info("Successfully credited {} to account: {}", transactionDto.getAmount(), accNumber);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/accounts/calculate-interest")
    public ResponseEntity<InterestCalculationResponseDto> calculateInterest(
            @Valid @RequestBody InterestCalculationRequestDto request) {
        logger.debug("Interest calculation request - Type: {}, Balance: {}", 
                   request.getAccountType(), request.getBalance());
        InterestCalculationResponseDto response = accountService.calculateInterest(request);
        logger.info("Interest calculated for {} account with balance: {}", 
                   request.getAccountType(), request.getBalance());
        return ResponseEntity.ok(response);
    }
}

