package com.tss.accountmanagementservice.service;

import com.tss.accountmanagementservice.dto.*;

public interface AccountService {

    AccountPageResponseDto findAll(Integer pageNumber, Integer pageSize);

    AccountResponseDto findByAccNumber(String accNumber);

    AccountResponseDto createAccount(AccountRequestDto request);

    void deleteByAccNumber(String accNumber);

    AccountResponseDto updateAccount(String accNumber, AccountUpdateDto request);

    AccountResponseDto debit(String accNumber, AccountTransactionDto transactionDto);

    AccountResponseDto credit(String accNumber, AccountTransactionDto transactionDto);
}
