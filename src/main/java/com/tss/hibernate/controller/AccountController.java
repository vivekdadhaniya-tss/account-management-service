package com.tss.hibernate.controller;

import com.tss.hibernate.dto.*;
import com.tss.hibernate.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accountapp")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/accounts")
    public ResponseEntity<AccountPageResponseDto> getAllAccounts(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "5") Integer pageSize) {
        return ResponseEntity.ok(accountService.findAll(pageNumber, pageSize));
    }

    @GetMapping("/accounts/{accNumber}")
    public ResponseEntity<AccountResponseDto> getAccountByAccNumber(@PathVariable String accNumber) {
        return ResponseEntity.ok(accountService.findByAccNumber(accNumber));
    }

    @PostMapping("/accounts")
    public ResponseEntity<AccountResponseDto> createAccount(@Valid @RequestBody AccountRequestDto request) {
        return new ResponseEntity<>(accountService.createAccount(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/accounts")
    public ResponseEntity<Void> deleteAccount(@RequestParam String accNumber) {
        accountService.deleteByAccNumber(accNumber);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/accounts")
    public ResponseEntity<AccountResponseDto> updateAccount(
            @RequestParam String accNumber,
            @Valid @RequestBody AccountUpdateDto request) {
        return ResponseEntity.ok(accountService.updateAccount(accNumber, request));
    }

    @PostMapping("/accounts/debit")
    public ResponseEntity<AccountResponseDto> debit(
            @RequestParam String accNumber,
            @RequestBody AccountTransactionDto transactionDto) {
        return ResponseEntity.ok(accountService.debit(accNumber, transactionDto));
    }

    @PostMapping("/accounts/credit")
    public ResponseEntity<AccountResponseDto> credit(
            @RequestParam String accNumber,
            @RequestBody AccountTransactionDto transactionDto) {
        return ResponseEntity.ok(accountService.credit(accNumber, transactionDto));
    }
}
