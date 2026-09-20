package com.transport.tms.finance.payment.repository;

import com.transport.tms.finance.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByCustomerIdOrderByDateDesc(String customerId);
    List<Payment> findAllByOrderByDateDesc();
}
