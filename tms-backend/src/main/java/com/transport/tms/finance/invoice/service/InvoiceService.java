package com.transport.tms.finance.invoice.service;

import com.transport.tms.common.exception.Exceptions;
import com.transport.tms.common.util.IdGenerator;
import com.transport.tms.finance.invoice.dto.InvoiceDto;
import com.transport.tms.finance.invoice.entity.Invoice;
import com.transport.tms.finance.invoice.entity.InvoiceItem;
import com.transport.tms.finance.invoice.repository.InvoiceRepository;
import com.transport.tms.finance.ledger.service.LedgerService;
import com.transport.tms.master.customer.entity.Customer;
import com.transport.tms.master.customer.service.CustomerService;
import com.transport.tms.security.UserPrincipal;
import com.transport.tms.trip.entity.Trip;
import com.transport.tms.trip.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final TripRepository tripRepository;
    private final CustomerService customerService;
    private final LedgerService ledgerService;
    private final IdGenerator idGenerator;

    @Transactional
    public Invoice generateInvoice(InvoiceDto.GenerateInvoiceRequest request, UserPrincipal currentUser) {
        Customer customer = customerService.getCustomerById(request.getCustomerId());

        String invoiceId = idGenerator.generateInvoiceId();
        BigDecimal subtotal = BigDecimal.ZERO;
        List<InvoiceItem> items = new ArrayList<>();

        Invoice invoice = Invoice.builder()
                .id(invoiceId)
                .date(request.getDate())
                .dueDate(request.getDueDate() != null ? request.getDueDate() : request.getDate().plusDays(30))
                .customerId(customer.getId())
                .customerName(customer.getName())
                .customerGstin(customer.getGstin())
                .taxRate(request.getTaxRate() != null ? request.getTaxRate() : BigDecimal.ZERO)
                .status("GENERATED")
                .notes(request.getNotes())
                .generatedBy(currentUser.getFullName())
                .items(new ArrayList<>())
                .build();

        for (String tripId : request.getTripIds()) {
            Trip trip = tripRepository.findById(tripId)
                    .orElseThrow(() -> new Exceptions.ResourceNotFoundException("Trip", "id", tripId));

            if (trip.getInvoiceId() != null && !trip.getInvoiceId().isBlank()) {
                throw new Exceptions.BadRequestException("Trip " + tripId + " is already billed under Invoice " + trip.getInvoiceId());
            }

            BigDecimal amount = trip.getTotalAmount();
            subtotal = subtotal.add(amount);

            InvoiceItem item = InvoiceItem.builder()
                    .invoice(invoice)
                    .tripId(trip.getId())
                    .tripDate(trip.getDate())
                    .vehicle(trip.getVehicleRegistration())
                    .material(trip.getMaterial())
                    .quantity(trip.getQuantity())
                    .unit(trip.getUnit())
                    .rate(trip.getAppliedRate())
                    .amount(amount)
                    .build();

            items.add(item);

            // Link trip to this invoice
            trip.setInvoiceId(invoiceId);
            tripRepository.save(trip);
        }

        BigDecimal taxRate = invoice.getTaxRate();
        BigDecimal taxAmount = subtotal.multiply(taxRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.add(taxAmount);

        invoice.setSubtotal(subtotal);
        invoice.setTaxAmount(taxAmount);
        invoice.setTotalAmount(totalAmount);
        invoice.setBalanceAmount(totalAmount);
        invoice.setPaidAmount(BigDecimal.ZERO);
        invoice.setItems(items);

        Invoice saved = invoiceRepository.save(invoice);

        // Record Receivable entry in Central Ledger
        ledgerService.recordReceivable(saved, currentUser.getFullName());

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByCustomer(String customerId) {
        return invoiceRepository.findByCustomerIdOrderByDateDesc(customerId);
    }

    @Transactional(readOnly = true)
    public Invoice getInvoiceById(String id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new Exceptions.ResourceNotFoundException("Invoice", "id", id));
    }
}
