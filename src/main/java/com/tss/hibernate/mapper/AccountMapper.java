package com.tss.hibernate.mapper;

import com.tss.hibernate.dto.AccountRequestDto;
import com.tss.hibernate.dto.AccountResponseDto;
import com.tss.hibernate.entity.Account;
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
