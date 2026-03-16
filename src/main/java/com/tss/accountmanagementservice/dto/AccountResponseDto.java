package com.tss.accountmanagementservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AccountResponseDto {
    private Long accId;
    private String accNumber;
    private String name;
    private BigDecimal balance;
    private String email;
    private String mobileNumber;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime accountCreationDate;
}
