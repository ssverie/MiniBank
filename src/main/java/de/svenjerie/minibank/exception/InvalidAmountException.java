package de.svenjerie.minibank.exception;

import java.math.BigDecimal;

public class InvalidAmountException extends RuntimeException {

    public InvalidAmountException(BigDecimal amount) {
        super("Invalid amount: %s (must be positive)".formatted(amount));
    }
}