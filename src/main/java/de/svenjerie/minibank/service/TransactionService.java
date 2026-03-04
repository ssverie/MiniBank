package de.svenjerie.minibank.service;

import de.svenjerie.minibank.exception.AccountNotFoundException;
import de.svenjerie.minibank.exception.InsufficientFundsException;
import de.svenjerie.minibank.exception.InvalidAmountException;
import de.svenjerie.minibank.model.Account;
import de.svenjerie.minibank.model.Transaction;
import de.svenjerie.minibank.model.TransactionType;
import de.svenjerie.minibank.repository.AccountRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class TransactionService {

    private final AccountRepository repository;

    public TransactionService(AccountRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public Transaction deposit(String accountId, BigDecimal amount, String description) {
        validateAmount(amount);
        var account = findAccountOrThrow(accountId);

        var transaction = new Transaction(
                UUID.randomUUID().toString(),
                accountId,
                LocalDateTime.now(),
                amount,
                TransactionType.DEPOSIT,
                description,
                null
        );

        account.addTransaction(transaction);
        return transaction;
    }

    public Transaction withdraw(String accountId, BigDecimal amount, String description) {
        validateAmount(amount);
        var account = findAccountOrThrow(accountId);
        checkSufficientFunds(account, amount);

        var transaction = new Transaction(
                UUID.randomUUID().toString(),
                accountId,
                LocalDateTime.now(),
                amount.negate(),
                TransactionType.WITHDRAWAL,
                description,
                null
        );

        account.addTransaction(transaction);
        return transaction;
    }

    public void transfer(String sourceAccountId, String targetAccountId,
                         BigDecimal amount, String description) {
        validateAmount(amount);

        if (sourceAccountId.equals(targetAccountId)) {
            throw new IllegalArgumentException("Cannot transfer to same account");
        }

        var source = findAccountOrThrow(sourceAccountId);
        var target = findAccountOrThrow(targetAccountId);
        checkSufficientFunds(source, amount);

        var referenceId = UUID.randomUUID().toString();
        var timestamp = LocalDateTime.now();

        var debit = new Transaction(
                UUID.randomUUID().toString(),
                sourceAccountId,
                timestamp,
                amount.negate(),
                TransactionType.TRANSFER_OUT,
                description,
                referenceId
        );

        var credit = new Transaction(
                UUID.randomUUID().toString(),
                targetAccountId,
                timestamp,
                amount,
                TransactionType.TRANSFER_IN,
                description,
                referenceId
        );

        source.addTransaction(debit);
        target.addTransaction(credit);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(amount);
        }
    }

    private void checkSufficientFunds(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                    account.getId(), amount, account.getBalance());
        }
    }

    private Account findAccountOrThrow(String accountId) {
        return repository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }
}