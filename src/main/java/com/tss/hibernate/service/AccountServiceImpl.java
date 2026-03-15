package com.tss.hibernate.service;

import com.tss.hibernate.dto.*;
import com.tss.hibernate.entity.Account;
import com.tss.hibernate.mapper.AccountMapper;
import com.tss.hibernate.repository.AccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepo;
    private final AccountMapper accountMapper;

    public AccountServiceImpl(AccountRepository accountRepo, AccountMapper accountMapper) {
        this.accountRepo = accountRepo;
        this.accountMapper = accountMapper;
    }

    private String generateUniqueAccountNumber() {
        String accNumber;
        do {
            long number = ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L);
            accNumber = String.valueOf(number);
        } while (accountRepo.findByAccNumber(accNumber).isPresent());
        return accNumber;
    }

    @Override
    public AccountPageResponseDto findAll(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Account> accounts = accountRepo.findAll(pageable);

        return AccountPageResponseDto.builder()
                .content(accounts.getContent().stream()
                        .map(accountMapper::toAccountResponseDto)
                        .collect(Collectors.toList()))
                .numberOfElements(accounts.getNumberOfElements())
                .totalElements(accounts.getTotalElements())
                .totalPages(accounts.getTotalPages())
                .first(accounts.isFirst())
                .last(accounts.isLast())
                .build();
    }

    @Override
    public AccountResponseDto findByAccNumber(String accNumber) {
        Account account = accountRepo.findByAccNumber(accNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Account not found with accNumber: " + accNumber));
        return accountMapper.toAccountResponseDto(account);
    }

    @Override
    @Transactional
    public AccountResponseDto createAccount(AccountRequestDto request) {
        Account account = accountMapper.toAccount(request);
        account.setAccNumber(generateUniqueAccountNumber());
        Account savedAccount = accountRepo.save(account);
        return accountMapper.toAccountResponseDto(savedAccount);
    }

    @Override
    @Transactional
    public void deleteByAccNumber(String accNumber) {
        Account account = accountRepo.findByAccNumber(accNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Account not found with accNumber: " + accNumber));
        accountRepo.delete(account);
    }

    @Override
    @Transactional
    public AccountResponseDto updateAccount(String accNumber, AccountUpdateDto request) {
        Account account = accountRepo.findByAccNumber(accNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Account not found with accNumber: " + accNumber));
        account.setEmail(request.getEmail());
        account.setMobileNumber(request.getMobileNumber());
        Account updatedAccount = accountRepo.save(account);
        return accountMapper.toAccountResponseDto(updatedAccount);
    }

    @Override
    @Transactional
    public AccountResponseDto debit(String accNumber, AccountTransactionDto transactionDto) {
        Account account = accountRepo.findByAccNumber(accNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Account not found with accNumber: " + accNumber));
        BigDecimal amount = transactionDto.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debit amount must be greater than zero");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance for debit operation");
        }
        account.setBalance(account.getBalance().subtract(amount));
        Account updatedAccount = accountRepo.save(account);
        return accountMapper.toAccountResponseDto(updatedAccount);
    }

    @Override
    @Transactional
    public AccountResponseDto credit(String accNumber, AccountTransactionDto transactionDto) {
        Account account = accountRepo.findByAccNumber(accNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Account not found with accNumber: " + accNumber));
        BigDecimal amount = transactionDto.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit amount must be greater than zero");
        }
        account.setBalance(account.getBalance().add(amount));
        Account updatedAccount = accountRepo.save(account);
        return accountMapper.toAccountResponseDto(updatedAccount);
    }
}
