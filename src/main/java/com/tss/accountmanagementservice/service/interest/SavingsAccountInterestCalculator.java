package com.tss.accountmanagementservice.service.interest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Interest calculator for Savings Accounts
 * 
 * Interest Rate: 3.5% per annum
 * Frequency: Annual calculation
 * Eligible Account Type: "savings"
 */
@Service("savings")
@Primary
public class SavingsAccountInterestCalculator implements InterestCalculator {
    
    private static final Logger logger = LoggerFactory.getLogger(SavingsAccountInterestCalculator.class);
    
    private static final BigDecimal INTEREST_RATE = new BigDecimal("3.5");
    private static final BigDecimal ANNUAL_DIVISOR = new BigDecimal("100");
    
    public SavingsAccountInterestCalculator() {
        logger.info("SavingsAccountInterestCalculator bean initialized - Rate: 3.5% per annum");
    }
    
    @Override
    public BigDecimal calculateInterest(BigDecimal balance) {
        logger.debug("Calculating savings account interest for balance: {}", balance);
        
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            logger.warn("Invalid balance provided for interest calculation: {}", balance);
            return BigDecimal.ZERO;
        }

        BigDecimal interest = balance
                .multiply(INTEREST_RATE)
                .divide(ANNUAL_DIVISOR, 2, RoundingMode.HALF_UP);
        
        logger.info("Savings account interest calculated - Balance: {}, Interest: {}, Rate: 3.5%", 
                   balance, interest);
        
        return interest;
    }
    
    @Override
    public BigDecimal getInterestRate() {
        return INTEREST_RATE;
    }
    
    @Override
    public String getDescription() {
        return "Savings Account - 3.5% per annum";
    }
}

