package com.transport.tms.finance.ledger.repository;

import com.transport.tms.finance.ledger.entity.FinancialTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, String> {

    List<FinancialTransaction> findByEntityTypeAndEntityIdOrderByDateDesc(String entityType, String entityId);

    List<FinancialTransaction> findTop200ByOrderByDateDesc();

    @Query("SELECT SUM(f.credit) FROM FinancialTransaction f WHERE f.transactionType = 'RECEIVABLE' AND f.status = 'POSTED'")
    BigDecimal calculateTotalGrossRevenue();

    @Query("SELECT SUM(f.debit) FROM FinancialTransaction f WHERE f.transactionType IN ('DIESEL', 'MAINTENANCE', 'SALARY', 'EXPENSE') AND f.status = 'POSTED'")
    BigDecimal calculateTotalOperatingExpenses();
}
