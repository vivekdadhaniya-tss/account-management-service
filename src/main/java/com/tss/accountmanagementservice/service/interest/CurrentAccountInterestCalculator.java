package com.tss.accountmanagementservice.service.interest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service("current")
public class CurrentAccountInterestCalculator implements InterestCalculator {
    
    private static final Logger logger = LoggerFactory.getLogger(CurrentAccountInterestCalculator.class);
    
    private static final BigDecimal INTEREST_RATE = new BigDecimal("0.5");
    private static final BigDecimal ANNUAL_DIVISOR = new BigDecimal("100");
    
    public CurrentAccountInterestCalculator() {
        logger.info("CurrentAccountInterestCalculator bean initialized - Rate: 0.5% per annum");
    }
    
    @Override
    public BigDecimal calculateInterest(BigDecimal balance) {
        logger.debug("Calculating current account interest for balance: {}", balance);
        
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            logger.warn("Invalid balance provided for interest calculation: {}", balance);
            return BigDecimal.ZERO;
        }

        BigDecimal interest = balance
                .multiply(INTEREST_RATE)
                .divide(ANNUAL_DIVISOR, 2, RoundingMode.HALF_UP);
        
        logger.info("Current account interest calculated - Balance: {}, Interest: {}, Rate: 0.5%", balance, interest);
        
        return interest;
    }
    
    @Override
    public BigDecimal getInterestRate() {
        return INTEREST_RATE;
    }
    
    @Override
    public String getDescription() {
        return "Current Account - 0.5% per annum";
    }
}

