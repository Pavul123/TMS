package com.transport.tms.finance.cashbank.service;

import com.transport.tms.common.exception.Exceptions;
import com.transport.tms.common.util.IdGenerator;
import com.transport.tms.finance.cashbank.entity.CashBankEntities.CashBankAccount;
import com.transport.tms.finance.cashbank.entity.CashBankEntities.ContraTransfer;
import com.transport.tms.finance.cashbank.repository.CashBankAccountRepository;
import com.transport.tms.finance.cashbank.repository.ContraTransferRepository;
import com.transport.tms.finance.ledger.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CashBankService {

    private final CashBankAccountRepository accountRepository;
    private final ContraTransferRepository transferRepository;
    private final LedgerService ledgerService;
    private final IdGenerator idGenerator;

    @Transactional(readOnly = true)
    public List<CashBankAccount> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Transactional(readOnly = true)
    public CashBankAccount getAccountById(String id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new Exceptions.ResourceNotFoundException("Account", "id", id));
    }

    @Transactional
    public CashBankAccount createAccount(CashBankAccount account) {
        if (account.getId() == null || account.getId().isBlank()) {
            account.setId("ACC-" + String.format("%03d", (int)(Math.random() * 900) + 100));
        }
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }
        if (account.getStatus() == null) {
            account.setStatus("ACTIVE");
        }
        return accountRepository.save(account);
    }

    @Transactional
    public CashBankAccount updateAccount(String id, CashBankAccount request) {
        CashBankAccount account = getAccountById(id);
        if (request.getAccountName() != null) account.setAccountName(request.getAccountName());
        if (request.getAccountType() != null) account.setAccountType(request.getAccountType());
        if (request.getBankName() != null) account.setBankName(request.getBankName());
        if (request.getAccountNumber() != null) account.setAccountNumber(request.getAccountNumber());
        if (request.getIfsc() != null) account.setIfsc(request.getIfsc());
        if (request.getBranch() != null) account.setBranch(request.getBranch());
        if (request.getStatus() != null) account.setStatus(request.getStatus());
        return accountRepository.save(account);
    }

    @Transactional
    public void deleteAccount(String id) {
        CashBankAccount account = getAccountById(id);
        account.setStatus("INACTIVE");
        accountRepository.save(account);
    }

    @Transactional
    public void creditAccount(String accountId, BigDecimal amount) {
        CashBankAccount account = getAccountById(accountId);
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }

    @Transactional
    public void debitAccount(String accountId, BigDecimal amount) {
        CashBankAccount account = getAccountById(accountId);
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }

    @Transactional
    public ContraTransfer executeContraTransfer(
            String fromAccountId,
            String toAccountId,
            BigDecimal amount,
            String refNo,
            String reason,
            String performedBy) {

        if (fromAccountId.equals(toAccountId)) {
            throw new Exceptions.BadRequestException("Source and Destination accounts cannot be the same!");
        }

        CashBankAccount fromAccount = getAccountById(fromAccountId);
        CashBankAccount toAccount = getAccountById(toAccountId);

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new Exceptions.BadRequestException("Insufficient balance in source account " + fromAccount.getAccountName());
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        String transferId = idGenerator.generateTransferId();
        ContraTransfer transfer = ContraTransfer.builder()
                .id(transferId)
                .transferDate(LocalDate.now())
                .fromAccountId(fromAccountId)
                .toAccountId(toAccountId)
                .amount(amount)
                .referenceNumber(refNo)
                .reason(reason)
                .performedBy(performedBy)
                .build();

        ContraTransfer saved = transferRepository.save(transfer);

        // Record in central financial ledger
        ledgerService.recordExpense(
                "TRANSFER",
                transferId,
                fromAccount.getAccountName() + " -> " + toAccount.getAccountName(),
                "TRANSFER",
                amount,
                "CONTRA",
                transferId,
                "Contra Transfer: " + reason,
                performedBy
        );

        return saved;
    }

    @Transactional(readOnly = true)
    public List<ContraTransfer> getAllTransfers() {
        return transferRepository.findAllByOrderByTransferDateDesc();
    }
}
