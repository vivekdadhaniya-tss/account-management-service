package com.tss.accountmanagementservice.repository;

import com.tss.accountmanagementservice.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccNumber(String accNumber);
}
