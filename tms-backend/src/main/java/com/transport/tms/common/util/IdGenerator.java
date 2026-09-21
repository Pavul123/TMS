package com.transport.tms.common.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class IdGenerator {

    private final AtomicLong tripSequence = new AtomicLong(10000 + (System.currentTimeMillis() % 10000));
    private final AtomicLong invoiceSequence = new AtomicLong(100 + (System.currentTimeMillis() % 900));
    private final AtomicLong paymentSequence = new AtomicLong(100 + (System.currentTimeMillis() % 900));
    private final AtomicLong transactionSequence = new AtomicLong(50000 + (System.currentTimeMillis() % 10000));
    private final AtomicLong correctionSequence = new AtomicLong(100 + (System.currentTimeMillis() % 900));
    private final AtomicLong customerSequence = new AtomicLong(1000 + (System.currentTimeMillis() % 1000));
    private final AtomicLong vehicleExpenseSequence = new AtomicLong(100 + (System.currentTimeMillis() % 900));
    private final AtomicLong dieselSequence = new AtomicLong(100 + (System.currentTimeMillis() % 900));
    private final AtomicLong transferSequence = new AtomicLong(100 + (System.currentTimeMillis() % 900));

    public String generateTripId() {
        return String.format("TRP-%05d", tripSequence.incrementAndGet());
    }

    public String generateInvoiceId() {
        return String.format("INV-%04d", invoiceSequence.incrementAndGet());
    }

    public String generatePaymentId() {
        return String.format("PAY-%04d", paymentSequence.incrementAndGet());
    }

    public String generateTransactionId() {
        return String.format("TXN-%05d", transactionSequence.incrementAndGet());
    }

    public String generateCorrectionId() {
        return String.format("CRQ-%04d", correctionSequence.incrementAndGet());
    }

    public String generateCustomerId() {
        return String.format("CUS-%05d", customerSequence.incrementAndGet());
    }

    public String generateExpenseId() {
        return String.format("EXP-%04d", vehicleExpenseSequence.incrementAndGet());
    }

    public String generateDieselId() {
        return String.format("DSL-%04d", dieselSequence.incrementAndGet());
    }

    public String generateTransferId() {
        return String.format("TRF-%04d", transferSequence.incrementAndGet());
    }
}
