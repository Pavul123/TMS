package com.transport.tms.finance.cashbank.repository;

import com.transport.tms.finance.cashbank.entity.CashBankEntities.CashBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CashBankAccountRepository extends JpaRepository<CashBankAccount, String> {
    List<CashBankAccount> findByStatus(String status);
}
