package de.svenjerie.minibank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Account {

    private final String id;
    private final Customer customer;
    private final AccountType type;
    private final LocalDateTime createdAt;
    private final List<Transaction> transactions;

    public Account(String id, Customer customer, AccountType type) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.customer = Objects.requireNonNull(customer, "customer must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.createdAt = LocalDateTime.now();
        this.transactions = new ArrayList<>();
    }

    public Account(Customer customer, AccountType type) {
        this(UUID.randomUUID().toString(), customer, type);
    }

    public BigDecimal getBalance() {
        return transactions.stream()
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addTransaction(Transaction transaction) {
        Objects.requireNonNull(transaction, "transaction must not be null");
        if (!transaction.accountId().equals(this.id)) {
            throw new IllegalArgumentException(
                    "Transaction accountId does not match this account");
        }
        transactions.add(transaction);
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public String getId() { return id; }
    public Customer getCustomer() { return customer; }
    public AccountType getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "Account{id='%s', customer=%s, type=%s, balance=%s}"
                .formatted(id, customer.fullName(), type, getBalance());
    }
}