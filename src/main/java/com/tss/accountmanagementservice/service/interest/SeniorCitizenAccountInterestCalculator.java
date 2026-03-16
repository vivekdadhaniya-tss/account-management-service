package com.tss.accountmanagementservice.service.interest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service("seniorcitizen")
public class SeniorCitizenAccountInterestCalculator implements InterestCalculator {
    
    private static final Logger logger = LoggerFactory.getLogger(SeniorCitizenAccountInterestCalculator.class);
    
    private static final BigDecimal INTEREST_RATE = new BigDecimal("5.0");
    private static final BigDecimal ANNUAL_DIVISOR = new BigDecimal("100");
    
    public SeniorCitizenAccountInterestCalculator() {
        logger.info("SeniorCitizenAccountInterestCalculator bean initialized - Rate: 5.0% per annum");
    }
    
    @Override
    public BigDecimal calculateInterest(BigDecimal balance) {
        logger.debug("Calculating senior citizen account interest for balance: {}", balance);
        
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            logger.warn("Invalid balance provided for interest calculation: {}", balance);
            return BigDecimal.ZERO;
        }

        BigDecimal interest = balance
                .multiply(INTEREST_RATE)
                .divide(ANNUAL_DIVISOR, 2, RoundingMode.HALF_UP);
        
        logger.info("Senior citizen account interest calculated - Balance: {}, Interest: {}, Rate: 5.0%", 
                   balance, interest);
        
        return interest;
    }
    
    @Override
    public BigDecimal getInterestRate() {
        return INTEREST_RATE;
    }
    
    @Override
    public String getDescription() {
        return "Senior Citizen Account - 5.0% per annum";
    }
}

