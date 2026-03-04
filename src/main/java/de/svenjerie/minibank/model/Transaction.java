package de.svenjerie.minibank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public record Transaction(
        String id,
        String accountId,
        LocalDateTime timestamp,
        BigDecimal amount,
        TransactionType type,
        String description,
        String referenceId) {

    public Transaction {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(accountId, "accountId must not be null");
        Objects.requireNonNull(timestamp, "timestamp must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(type, "type must not be null");

        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("amount must not be zero");
        }
    }
}