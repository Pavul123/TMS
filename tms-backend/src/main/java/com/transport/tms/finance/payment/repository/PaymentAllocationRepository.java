package com.transport.tms.finance.payment.repository;

import com.transport.tms.finance.payment.entity.PaymentAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentAllocationRepository extends JpaRepository<PaymentAllocation, Long> {
    List<PaymentAllocation> findByPaymentId(String paymentId);
    List<PaymentAllocation> findByInvoiceId(String invoiceId);
}
