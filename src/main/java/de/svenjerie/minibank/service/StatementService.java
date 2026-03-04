package de.svenjerie.minibank.service;

import de.svenjerie.minibank.exception.AccountNotFoundException;
import de.svenjerie.minibank.model.Account;
import de.svenjerie.minibank.model.Statement;
import de.svenjerie.minibank.model.Statement.StatementEntry;
import de.svenjerie.minibank.model.Transaction;
import de.svenjerie.minibank.repository.AccountRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class StatementService {

    private final AccountRepository repository;

    public StatementService(AccountRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public Statement generateStatement(String accountId) {
        var account = repository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        var entries = buildEntries(account);

        return new Statement(
                account.getId(),
                account.getCustomer().fullName(),
                account.getType(),
                LocalDateTime.now(),
                entries,
                account.getBalance()
        );
    }

    private List<StatementEntry> buildEntries(Account account) {
        var sorted = account.getTransactions().stream()
                .sorted(Comparator.comparing(Transaction::timestamp))
                .toList();

        var entries = new ArrayList<StatementEntry>();
        var runningBalance = BigDecimal.ZERO;

        for (var transaction : sorted) {
            runningBalance = runningBalance.add(transaction.amount());

            entries.add(new StatementEntry(
                    transaction.timestamp(),
                    transaction.type(),
                    transaction.description(),
                    transaction.amount(),
                    runningBalance
            ));
        }

        return List.copyOf(entries);
    }
}