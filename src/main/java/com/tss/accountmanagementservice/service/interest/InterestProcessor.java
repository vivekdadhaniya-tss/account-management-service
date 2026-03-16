package com.tss.accountmanagementservice.service.interest;

import com.tss.accountmanagementservice.exception.AccountNotFoundException;
import com.tss.accountmanagementservice.exception.InsufficientBalanceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Dynamic Dependency Injection - Strategy Pattern Implementation
 * 
 * This class demonstrates how to use a Map to store all implementations
 * of InterestCalculator interface and dynamically select the right one
 * based on account type at runtime.
 * 
 * All InterestCalculator implementations are automatically injected
 * into the Map by Spring's dependency injection framework.
 *
 * Bean names (keys in map):
 * - "savings" → SavingsAccountInterestCalculator (3.5%)
 * - "current" → CurrentAccountInterestCalculator (0.5%)
 * - "seniorcitizen" → SeniorCitizenAccountInterestCalculator (5.0%)
 */
@Component
public class InterestProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(InterestProcessor.class);
    
    // Map automatically populated by Spring with all InterestCalculator implementations
    // Key = Bean name (specified in @Service annotation)
    // Value = InterestCalculator implementation instance
    private final Map<String, InterestCalculator> interestCalculators;
    
    /**
     * Constructor that demonstrates dynamic dependency injection
     * 
     * Spring's IoC container automatically:
     * 1. Scans the classpath for all @Service classes implementing InterestCalculator
     * 2. Creates instances of: SavingsAccountInterestCalculator, 
     *    CurrentAccountInterestCalculator, SeniorCitizenAccountInterestCalculator
     * 3. Puts them in a Map with their bean names as keys
     * 4. Injects this Map into this constructor
     * 
     * @param interestCalculators Map of all InterestCalculator implementations
     *                           injected by Spring IoC container
     */
    public InterestProcessor(Map<String, InterestCalculator> interestCalculators) {
        this.interestCalculators = interestCalculators;
        logger.info("InterestProcessor initialized with dynamic dependency injection");
        logger.info("Available interest calculators: {}", interestCalculators.keySet());
    }

    public BigDecimal calculateInterest(String accountType, BigDecimal balance) {
        logger.debug("Processing interest calculation - Type: {}, Balance: {}", accountType, balance);
        
        if (accountType == null || accountType.trim().isEmpty()) {
            logger.error("Account type cannot be null or empty");
            throw new AccountNotFoundException("Account type is required");
        }
        
        if (balance == null) {
            logger.error("Balance cannot be null");
            throw new InsufficientBalanceException("Balance is required");
        }
        
        String normalizedType = accountType.toLowerCase().trim();
        InterestCalculator calculator = interestCalculators.get(normalizedType);
        
        // Check if calculator exists for this account type
        if (calculator == null) {
            logger.warn("No interest calculator found for account type: {}", accountType);
            logger.warn("Available types: {}", interestCalculators.keySet());
            throw new AccountNotFoundException(
                "Unsupported account type: " + accountType + 
                ". Supported types: " + interestCalculators.keySet()
            );
        }
        
        // EXECUTE: Call the appropriate calculator's method
        logger.info("Using {} for interest calculation", calculator.getClass().getSimpleName());
        BigDecimal interest = calculator.calculateInterest(balance);
        
        logger.info("Interest calculation completed - Type: {}, Calculator: {}, Interest: {}", accountType, calculator.getClass().getSimpleName(), interest);
        
        return interest;
    }

    public BigDecimal getInterestRate(String accountType) {
        logger.debug("Fetching interest rate for account type: {}", accountType);
        
        String normalizedType = accountType.toLowerCase().trim();
        InterestCalculator calculator = interestCalculators.get(normalizedType);
        
        if (calculator == null) {
            logger.warn("No interest calculator found for account type: {}", accountType);
            throw new AccountNotFoundException("Unsupported account type: " + accountType);
        }
        
        BigDecimal rate = calculator.getInterestRate();
        logger.info("Interest rate for {} account: {}", accountType, rate);
        
        return rate;
    }

    public String getAccountTypeDescription(String accountType) {
        logger.debug("Fetching description for account type: {}", accountType);
        
        String normalizedType = accountType.toLowerCase().trim();
        InterestCalculator calculator = interestCalculators.get(normalizedType);
        
        if (calculator == null) {
            logger.warn("No interest calculator found for account type: {}", accountType);
            throw new IllegalArgumentException("Unsupported account type: " + accountType);
        }
        
        String description = calculator.getDescription();
        logger.info("Description for {} account: {}", accountType, description);
        
        return description;
    }
}

