package com.transport.tms.finance.wage.service;

import com.transport.tms.common.util.IdGenerator;
import com.transport.tms.finance.ledger.service.LedgerService;
import com.transport.tms.finance.wage.entity.WageEntities.WageAdvance;
import com.transport.tms.finance.wage.entity.WageEntities.WorkerWage;
import com.transport.tms.finance.wage.repository.WageAdvanceRepository;
import com.transport.tms.finance.wage.repository.WorkerWageRepository;
import com.transport.tms.master.driver.entity.Driver;
import com.transport.tms.master.driver.repository.DriverRepository;
import com.transport.tms.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WageService {

    private final WorkerWageRepository wageRepository;
    private final WageAdvanceRepository advanceRepository;
    private final DriverRepository driverRepository;
    private final LedgerService ledgerService;
    private final IdGenerator idGenerator;

    @Transactional
    public WageAdvance recordAdvance(WageAdvance request, UserPrincipal currentUser) {
        String advId = "ADV-" + String.format("%04d", (int)(Math.random() * 9000) + 1000);
        
        WageAdvance advance = WageAdvance.builder()
                .id(advId)
                .date(request.getDate())
                .recipientType(request.getRecipientType())
                .recipientId(request.getRecipientId())
                .recipientName(request.getRecipientName())
                .amount(request.getAmount())
                .reason(request.getReason())
                .paymentMode(request.getPaymentMode() != null ? request.getPaymentMode() : "CASH")
                .status("PAID")
                .givenBy(currentUser.getFullName())
                .build();

        WageAdvance saved = advanceRepository.save(advance);

        // If recipient is driver, update driver advance balance
        if ("DRIVER".equalsIgnoreCase(request.getRecipientType())) {
            Driver driver = driverRepository.findById(request.getRecipientId()).orElse(null);
            if (driver != null) {
                driver.setAdvanceBalance(driver.getAdvanceBalance().add(request.getAmount()));
                driverRepository.save(driver);
            }
        }

        // Record in ledger
        ledgerService.recordExpense(
                request.getRecipientType(),
                request.getRecipientId(),
                request.getRecipientName(),
                "DRIVER_ADVANCE",
                request.getAmount(),
                request.getPaymentMode(),
                advId,
                "Advance Cash Paid: " + request.getReason(),
                currentUser.getFullName()
        );

        return saved;
    }

    @Transactional(readOnly = true)
    public List<WageAdvance> getAllAdvances() {
        return advanceRepository.findAllByOrderByDateDesc();
    }

    @Transactional(readOnly = true)
    public List<WorkerWage> getAllWages() {
        return wageRepository.findAll();
    }

    @Transactional
    public WorkerWage calculateAndDisburseWage(WorkerWage request, UserPrincipal currentUser) {
        String wageId = "WAG-" + String.format("%04d", (int)(Math.random() * 9000) + 1000);

        BigDecimal gross = request.getBaseSalary()
                .add(request.getOvertimeAmount() != null ? request.getOvertimeAmount() : BigDecimal.ZERO)
                .add(request.getTripAllowance() != null ? request.getTripAllowance() : BigDecimal.ZERO);

        BigDecimal deductions = (request.getAdvancesDeducted() != null ? request.getAdvancesDeducted() : BigDecimal.ZERO)
                .add(request.getOtherDeductions() != null ? request.getOtherDeductions() : BigDecimal.ZERO);

        BigDecimal netPayable = gross.subtract(deductions).max(BigDecimal.ZERO);

        WorkerWage wage = WorkerWage.builder()
                .id(wageId)
                .workerId(request.getWorkerId())
                .workerName(request.getWorkerName())
                .monthYear(request.getMonthYear())
                .baseSalary(request.getBaseSalary())
                .overtimeAmount(request.getOvertimeAmount())
                .tripAllowance(request.getTripAllowance())
                .advancesDeducted(request.getAdvancesDeducted())
                .otherDeductions(request.getOtherDeductions())
                .netPayable(netPayable)
                .status("DISBURSED")
                .paymentDate(LocalDate.now())
                .paymentMode(request.getPaymentMode() != null ? request.getPaymentMode() : "BANK")
                .referenceNo(request.getReferenceNo())
                .disbursedBy(currentUser.getFullName())
                .build();

        WorkerWage saved = wageRepository.save(wage);

        // Record in ledger
        ledgerService.recordExpense(
                "WORKER",
                request.getWorkerId(),
                request.getWorkerName(),
                "SALARY",
                netPayable,
                wage.getPaymentMode(),
                wageId,
                "Monthly Salary Disbursed for " + request.getMonthYear(),
                currentUser.getFullName()
        );

        return saved;
    }
}
