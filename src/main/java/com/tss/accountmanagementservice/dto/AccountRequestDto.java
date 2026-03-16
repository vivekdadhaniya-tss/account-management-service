package com.tss.accountmanagementservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequestDto {

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 2, max = 100)
    private String name;

    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal balance;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Mobile number cannot be empty")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be exactly 10 digits")
    private String mobileNumber;
}