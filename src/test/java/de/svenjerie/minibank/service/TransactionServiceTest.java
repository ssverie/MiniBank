package de.svenjerie.minibank.service;

import de.svenjerie.minibank.exception.AccountNotFoundException;
import de.svenjerie.minibank.exception.InsufficientFundsException;
import de.svenjerie.minibank.exception.InvalidAmountException;
import de.svenjerie.minibank.model.AccountType;
import de.svenjerie.minibank.model.Customer;
import de.svenjerie.minibank.repository.InMemoryAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {

    private AccountService accountService;
    private TransactionService transactionService;

    private static final Customer SVEN = new Customer("C1", "Sven", "Jerie");
    private static final Customer SOMCHAI = new Customer("C2", "Somchai", "Prasert");

    @BeforeEach
    void setUp() {
        var repository = new InMemoryAccountRepository();
        accountService = new AccountService(repository);
        transactionService = new TransactionService(repository);
    }

    @Test
    void deposit_increasesBalance() {
        var account = accountService.openAccount(SVEN, AccountType.CHECKING);

        transactionService.deposit(account.getId(), new BigDecimal("1000.00"), "Initial deposit");

        assertEquals(new BigDecimal("1000.00"), account.getBalance());
    }

    @Test
    void withdraw_decreasesBalance() {
        var account = accountService.openAccount(SVEN, AccountType.CHECKING);
        transactionService.deposit(account.getId(), new BigDecimal("1000.00"), "Initial deposit");

        transactionService.withdraw(account.getId(), new BigDecimal("300.00"), "ATM withdrawal");

        assertEquals(new BigDecimal("700.00"), account.getBalance());
    }

    @Test
    void withdraw_withInsufficientFunds_throwsException() {
        var account = accountService.openAccount(SVEN, AccountType.CHECKING);
        transactionService.deposit(account.getId(), new BigDecimal("100.00"), "Small deposit");

        assertThrows(InsufficientFundsException.class,
                () -> transactionService.withdraw(account.getId(), new BigDecimal("200.00"), "Too much"));
    }

    @Test
    void transfer_movesMoney_K1_sumIsZero() {
        var source = accountService.openAccount(SVEN, AccountType.CHECKING);
        var target = accountService.openAccount(SOMCHAI, AccountType.CHECKING);
        transactionService.deposit(source.getId(), new BigDecimal("500.00"), "Initial deposit");

        transactionService.transfer(source.getId(), target.getId(),
                new BigDecimal("200.00"), "Rent payment");

        assertEquals(new BigDecimal("300.00"), source.getBalance());
        assertEquals(new BigDecimal("200.00"), target.getBalance());

        var totalBalance = source.getBalance().add(target.getBalance());
        assertEquals(new BigDecimal("500.00"), totalBalance);
    }

    @Test
    void transfer_withInsufficientFunds_throwsException() {
        var source = accountService.openAccount(SVEN, AccountType.CHECKING);
        var target = accountService.openAccount(SOMCHAI, AccountType.CHECKING);
        transactionService.deposit(source.getId(), new BigDecimal("100.00"), "Small deposit");

        assertThrows(InsufficientFundsException.class,
                () -> transactionService.transfer(source.getId(), target.getId(),
                        new BigDecimal("200.00"), "Too much"));

        assertEquals(new BigDecimal("100.00"), source.getBalance());
        assertEquals(BigDecimal.ZERO, target.getBalance());
    }

    @Test
    void transfer_toSameAccount_throwsException() {
        var account = accountService.openAccount(SVEN, AccountType.CHECKING);
        transactionService.deposit(account.getId(), new BigDecimal("500.00"), "Deposit");

        assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfer(account.getId(), account.getId(),
                        new BigDecimal("100.00"), "Self transfer"));
    }

    @Test
    void deposit_withNegativeAmount_throwsException() {
        var account = accountService.openAccount(SVEN, AccountType.CHECKING);

        assertThrows(InvalidAmountException.class,
                () -> transactionService.deposit(account.getId(), new BigDecimal("-50.00"), "Negative"));
    }

    @Test
    void deposit_withZeroAmount_throwsException() {
        var account = accountService.openAccount(SVEN, AccountType.CHECKING);

        assertThrows(InvalidAmountException.class,
                () -> transactionService.deposit(account.getId(), BigDecimal.ZERO, "Zero"));
    }

    @Test
    void deposit_toNonExistentAccount_throwsException() {
        assertThrows(AccountNotFoundException.class,
                () -> transactionService.deposit("GHOST", new BigDecimal("100.00"), "Nope"));
    }

    @Test
    void multipleTransactions_balanceIsCorrect() {
        var account = accountService.openAccount(SVEN, AccountType.CHECKING);

        transactionService.deposit(account.getId(), new BigDecimal("1000.00"), "Salary");
        transactionService.withdraw(account.getId(), new BigDecimal("50.00"), "Coffee");
        transactionService.withdraw(account.getId(), new BigDecimal("200.00"), "Groceries");
        transactionService.deposit(account.getId(), new BigDecimal("30.00"), "Refund");

        assertEquals(new BigDecimal("780.00"), account.getBalance());
        assertEquals(4, account.getTransactions().size());
    }
}