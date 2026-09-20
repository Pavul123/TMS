package com.transport.tms.finance.invoice.repository;

import com.transport.tms.finance.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {

    List<Invoice> findByCustomerIdOrderByDateDesc(String customerId);

    List<Invoice> findByStatus(String status);

    @Query("SELECT SUM(i.balanceAmount) FROM Invoice i WHERE i.status != 'CANCELLED'")
    BigDecimal calculateTotalOutstandingReceivables();
}
