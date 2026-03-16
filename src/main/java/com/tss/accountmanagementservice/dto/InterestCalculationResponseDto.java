package com.tss.accountmanagementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for interest calculation
 * Returns calculated interest, rate, and account details to client
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterestCalculationResponseDto {
    
    private String accountType;
    
    private BigDecimal balance;
    
    private BigDecimal interestRate;
    
    private BigDecimal calculatedInterest;
    
    private String description;
    
    private LocalDateTime calculatedAt;
}

