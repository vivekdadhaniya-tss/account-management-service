package com.tss.accountmanagementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Request DTO for interest calculation
 * Used to receive account type and balance from client
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterestCalculationRequestDto {
    
    @NotBlank(message = "Account type is required")
    private String accountType;
    
    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Balance must be greater than 0")
    private BigDecimal balance;
}

