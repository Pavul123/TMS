package com.transport.tms.finance.cashbank.repository;

import com.transport.tms.finance.cashbank.entity.CashBankEntities.ContraTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContraTransferRepository extends JpaRepository<ContraTransfer, String> {
    List<ContraTransfer> findAllByOrderByTransferDateDesc();
}
