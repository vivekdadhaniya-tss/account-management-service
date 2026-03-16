package com.tss.accountmanagementservice.service.interest;

import java.math.BigDecimal;

public interface InterestCalculator {

    BigDecimal calculateInterest(BigDecimal balance);

    BigDecimal getInterestRate();

    String getDescription();
}

