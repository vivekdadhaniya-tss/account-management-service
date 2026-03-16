package com.tss.accountmanagementservice.service;

import com.tss.accountmanagementservice.dto.*;
import com.tss.accountmanagementservice.entity.Account;
import com.tss.accountmanagementservice.mapper.AccountMapper;
import com.tss.accountmanagementservice.repository.AccountRepository;
import com.tss.accountmanagementservice.exception.*;
import com.tss.accountmanagementservice.service.interest.InterestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
    private final AccountRepository accountRepo;
    private final AccountMapper accountMapper;
    private final InterestProcessor interestProcessor;

    public AccountServiceImpl(AccountRepository accountRepo, AccountMapper accountMapper, 
                            InterestProcessor interestProcessor) {
        this.accountRepo = accountRepo;
        this.accountMapper = accountMapper;
        this.interestProcessor = interestProcessor;
        logger.info("AccountServiceImpl initialized with dynamic dependency injection");
        logger.info("InterestProcessor injected for dynamic interest calculation");
    }

    private String generateUniqueAccountNumber() {
        logger.debug("Generating unique account number");
        String accNumber;
        do {
            long number = ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L);
            accNumber = String.valueOf(number);
        } while (accountRepo.findByAccNumber(accNumber).isPresent());
        logger.debug("Generated unique account number: {}", accNumber);
        return accNumber;
    }

    @Override
    public AccountPageResponseDto findAll(Integer pageNumber, Integer pageSize) {
        logger.debug("Fetching all accounts - pageNumber: {}, pageSize: {}", pageNumber, pageSize);
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Account> accounts = accountRepo.findAll(pageable);
            
            AccountPageResponseDto response = AccountPageResponseDto.builder()
                    .content(accounts.getContent().stream()
                            .map(accountMapper::toAccountResponseDto)
                            .collect(Collectors.toList()))
                    .numberOfElements(accounts.getNumberOfElements())
                    .totalElements(accounts.getTotalElements())
                    .totalPages(accounts.getTotalPages())
                    .first(accounts.isFirst())
                    .last(accounts.isLast())
                    .build();
            
            logger.info("Successfully fetched {} accounts from page {}", 
                       accounts.getNumberOfElements(), pageNumber);
            return response;
        } catch (Exception ex) {
            logger.error("Error fetching accounts for page {}", pageNumber, ex);
            throw new DatabaseException("Failed to fetch accounts: " + ex.getMessage());
        }
    }

    @Override
    public AccountResponseDto findByAccNumber(String accNumber) {
        logger.debug("Finding account by account number: {}", accNumber);
        try {
            Account account = accountRepo.findByAccNumber(accNumber)
                    .orElseThrow(() -> {
                        logger.warn("Account not found with number: {}", accNumber);
                        return new AccountNotFoundException(accNumber);
                    });
            logger.info("Successfully found account: {}", accNumber);
            return accountMapper.toAccountResponseDto(account);
        } catch (ApplicationException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Error finding account: {}", accNumber, ex);
            throw new DatabaseException("Failed to fetch account: " + ex.getMessage());
        }
    }

    @Override
    @Transactional
    public AccountResponseDto createAccount(AccountRequestDto request) {
        logger.debug("Creating new account with request: {}", request);
        try {
            Account account = accountMapper.toAccount(request);
            account.setAccNumber(generateUniqueAccountNumber());
            Account savedAccount = accountRepo.save(account);
            logger.info("Successfully created account: {} for customer: {}", 
                       savedAccount.getAccNumber(), savedAccount.getName());
            return accountMapper.toAccountResponseDto(savedAccount);
        } catch (Exception ex) {
            logger.error("Error creating account", ex);
            throw new DatabaseException("Failed to create account: " + ex.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteByAccNumber(String accNumber) {
        logger.debug("Deleting account: {}", accNumber);
        try {
            Account account = accountRepo.findByAccNumber(accNumber)
                    .orElseThrow(() -> {
                        logger.warn("Account not found for deletion: {}", accNumber);
                        return new AccountNotFoundException(accNumber);
                    });
            accountRepo.delete(account);
            logger.info("Successfully deleted account: {}", accNumber);
        } catch (ApplicationException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Error deleting account: {}", accNumber, ex);
            throw new DatabaseException("Failed to delete account: " + ex.getMessage());
        }
    }

    @Override
    @Transactional
    public AccountResponseDto updateAccount(String accNumber, AccountUpdateDto request) {
        logger.debug("Updating account: {} with request: {}", accNumber, request);
        try {
            Account account = accountRepo.findByAccNumber(accNumber)
                    .orElseThrow(() -> {
                        logger.warn("Account not found for update: {}", accNumber);
                        return new AccountNotFoundException(accNumber);
                    });
            account.setEmail(request.getEmail());
            account.setMobileNumber(request.getMobileNumber());
            Account updatedAccount = accountRepo.save(account);
            logger.info("Successfully updated account: {}", accNumber);
            return accountMapper.toAccountResponseDto(updatedAccount);
        } catch (ApplicationException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Error updating account: {}", accNumber, ex);
            throw new DatabaseException("Failed to update account: " + ex.getMessage());
        }
    }

    @Override
    @Transactional
    public AccountResponseDto debit(String accNumber, AccountTransactionDto transactionDto) {
        logger.debug("Processing debit for account: {} with amount: {}", 
                   accNumber, transactionDto.getAmount());
        try {
            Account account = accountRepo.findByAccNumber(accNumber)
                    .orElseThrow(() -> {
                        logger.warn("Account not found for debit: {}", accNumber);
                        return new AccountNotFoundException(accNumber);
                    });
            
            BigDecimal amount = transactionDto.getAmount();
            
            // Validate amount
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("Invalid debit amount: {} for account: {}", amount, accNumber);
                throw new InvalidTransactionAmountException("Debit amount must be greater than zero");
            }
            
            // Check balance
            if (account.getBalance().compareTo(amount) < 0) {
                logger.warn("Insufficient balance for debit. Account: {}, Balance: {}, Amount: {}", 
                           accNumber, account.getBalance(), amount);
                throw new InsufficientBalanceException("Insufficient balance for debit operation. " +
                        "Current balance: " + account.getBalance() + ", Required amount: " + amount);
            }
            
            account.setBalance(account.getBalance().subtract(amount));
            Account updatedAccount = accountRepo.save(account);
            logger.info("Successfully debited {} from account: {}. New balance: {}", 
                       amount, accNumber, updatedAccount.getBalance());
            return accountMapper.toAccountResponseDto(updatedAccount);
        } catch (ApplicationException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Error processing debit for account: {}", accNumber, ex);
            throw new TransactionFailedException("Failed to process debit: " + ex.getMessage());
        }
    }

    @Override
    @Transactional
    public AccountResponseDto credit(String accNumber, AccountTransactionDto transactionDto) {
        logger.debug("Processing credit for account: {} with amount: {}", 
                   accNumber, transactionDto.getAmount());
        try {
            Account account = accountRepo.findByAccNumber(accNumber)
                    .orElseThrow(() -> {
                        logger.warn("Account not found for credit: {}", accNumber);
                        return new AccountNotFoundException(accNumber);
                    });
            
            BigDecimal amount = transactionDto.getAmount();
            
            // Validate amount
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("Invalid credit amount: {} for account: {}", amount, accNumber);
                throw new InvalidTransactionAmountException("Credit amount must be greater than zero");
            }
            
            account.setBalance(account.getBalance().add(amount));
            Account updatedAccount = accountRepo.save(account);
            logger.info("Successfully credited {} to account: {}. New balance: {}", 
                       amount, accNumber, updatedAccount.getBalance());
            return accountMapper.toAccountResponseDto(updatedAccount);
        } catch (ApplicationException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Error processing credit for account: {}", accNumber, ex);
            throw new TransactionFailedException("Failed to process credit: " + ex.getMessage());
        }
    }

    @Override
    public InterestCalculationResponseDto calculateInterest(InterestCalculationRequestDto request) {
        logger.debug("Calculating interest - Account Type: {}, Balance: {}", 
                   request.getAccountType(), request.getBalance());
        
        try {
            // DYNAMIC DEPENDENCY INJECTION IN ACTION:
            // The interestProcessor has a Map<String, InterestCalculator> of all implementations
            // It selects the right calculator based on account type at runtime
            BigDecimal calculatedInterest = interestProcessor.calculateInterest(
                request.getAccountType(),
                request.getBalance()
            );
            
            BigDecimal interestRate = interestProcessor.getInterestRate(request.getAccountType());
            String description = interestProcessor.getAccountTypeDescription(request.getAccountType());
            
            // Build response
            InterestCalculationResponseDto response = InterestCalculationResponseDto.builder()
                    .accountType(request.getAccountType())
                    .balance(request.getBalance())
                    .interestRate(interestRate)
                    .calculatedInterest(calculatedInterest)
                    .description(description)
                    .calculatedAt(LocalDateTime.now())
                    .build();
            
            logger.info("Interest calculated successfully - Type: {}, Balance: {}, Interest: {}", 
                       request.getAccountType(), request.getBalance(), calculatedInterest);
            
            return response;
            
        } catch (IllegalArgumentException ex) {
            logger.warn("Invalid account type for interest calculation: {}", request.getAccountType());
            throw ex;
        } catch (Exception ex) {
            logger.error("Error calculating interest for account type: {}", request.getAccountType(), ex);
            throw new DatabaseException("Failed to calculate interest: " + ex.getMessage());
        }
    }
}

