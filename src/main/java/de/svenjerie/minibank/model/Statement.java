package de.svenjerie.minibank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record Statement(
        String accountId,
        String customerName,
        AccountType accountType,
        LocalDateTime generatedAt,
        List<StatementEntry> entries,
        BigDecimal balance) {

    public record StatementEntry(
            LocalDateTime timestamp,
            TransactionType type,
            String description,
            BigDecimal amount,
            BigDecimal runningBalance) {
    }
}