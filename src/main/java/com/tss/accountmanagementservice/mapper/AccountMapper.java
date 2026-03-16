package com.tss.accountmanagementservice.mapper;

import com.tss.accountmanagementservice.dto.AccountRequestDto;
import com.tss.accountmanagementservice.dto.AccountResponseDto;
import com.tss.accountmanagementservice.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "accId", ignore = true)
    @Mapping(target = "accNumber", ignore = true)
    @Mapping(target = "accountCreationDate", ignore = true)
    Account toAccount(AccountRequestDto accountRequestDto);

    AccountResponseDto toAccountResponseDto(Account account);
}
