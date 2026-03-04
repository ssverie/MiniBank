package de.svenjerie.minibank.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String accountId, BigDecimal requested, BigDecimal available) {
        super("Insufficient funds on account %s: requested=%s, available=%s"
                .formatted(accountId, requested, available));
    }
}