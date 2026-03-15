package com.tss.hibernate.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequestDto {
    private String name;
    private BigDecimal balance;
    private String email;
    private String mobileNumber;
}
