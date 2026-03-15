package com.tss.hibernate.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountTransactionDto {
    private BigDecimal amount;
}
