package com.tss.hibernate.repository;

import com.tss.hibernate.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccNumber(String accNumber);
}
