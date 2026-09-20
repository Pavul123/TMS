package com.transport.tms.finance.ledger.service;

import com.transport.tms.common.util.IdGenerator;
import com.transport.tms.finance.invoice.entity.Invoice;
import com.transport.tms.finance.ledger.entity.FinancialTransaction;
import com.transport.tms.finance.ledger.repository.FinancialTransactionRepository;
import com.transport.tms.finance.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LedgerService {

    private final FinancialTransactionRepository transactionRepository;
    private final IdGenerator idGenerator;

    @Transactional
    public void recordReceivable(Invoice invoice, String createdBy) {
        FinancialTransaction txn = FinancialTransaction.builder()
                .id(idGenerator.generateTransactionId())
                .date(invoice.getDate())
                .entityType("CUSTOMER")
                .entityId(invoice.getCustomerId())
                .entityName(invoice.getCustomerName())
                .transactionType("RECEIVABLE")
                .category("Revenue")
                .credit(invoice.getTotalAmount())
                .debit(BigDecimal.ZERO)
                .balance(invoice.getTotalAmount())
                .referenceId(invoice.getId())
                .description("Invoice " + invoice.getId() + " generated with " + invoice.getItems().size() + " trips")
                .createdBy(createdBy)
                .status("POSTED")
                .build();

        transactionRepository.save(txn);
    }

    @Transactional
    public void recordCustomerPayment(Payment payment, String createdBy) {
        FinancialTransaction txn = FinancialTransaction.builder()
                .id(idGenerator.generateTransactionId())
                .date(payment.getDate())
                .entityType("CUSTOMER")
                .entityId(payment.getCustomerId())
                .entityName(payment.getCustomerName())
                .transactionType("CUSTOMER_PAYMENT")
                .category("Receipt")
                .debit(payment.getAmount())
                .credit(BigDecimal.ZERO)
                .paymentMode(payment.getPaymentMode())
                .referenceId(payment.getId())
                .description("Payment received via " + payment.getPaymentMode() + " (Ref: " + payment.getReferenceNumber() + ")")
                .createdBy(createdBy)
                .status("POSTED")
                .build();

        transactionRepository.save(txn);
    }

    @Transactional
    public void recordExpense(String entityType, String entityId, String entityName, String type, BigDecimal amount, String mode, String refId, String desc, String createdBy) {
        FinancialTransaction txn = FinancialTransaction.builder()
                .id(idGenerator.generateTransactionId())
                .date(LocalDate.now())
                .entityType(entityType)
                .entityId(entityId)
                .entityName(entityName)
                .transactionType(type)
                .category("Operating Expense")
                .debit(amount)
                .credit(BigDecimal.ZERO)
                .paymentMode(mode)
                .referenceId(refId)
                .description(desc)
                .createdBy(createdBy)
                .status("POSTED")
                .build();

        transactionRepository.save(txn);
    }

    @Transactional(readOnly = true)
    public List<FinancialTransaction> getAllTransactions() {
        return transactionRepository.findTop200ByOrderByDateDesc();
    }

    @Transactional(readOnly = true)
    public List<FinancialTransaction> getCustomerTransactions(String customerId) {
        return transactionRepository.findByEntityTypeAndEntityIdOrderByDateDesc("CUSTOMER", customerId);
    }
}
