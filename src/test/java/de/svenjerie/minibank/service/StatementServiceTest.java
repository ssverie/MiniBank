package de.svenjerie.minibank.service;

import de.svenjerie.minibank.model.AccountType;
import de.svenjerie.minibank.model.Customer;
import de.svenjerie.minibank.model.TransactionType;
import de.svenjerie.minibank.repository.InMemoryAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class StatementServiceTest {

    private AccountService accountService;
    private TransactionService transactionService;
    private StatementService statementService;

    private static final Customer SVEN = new Customer("C1", "Sven", "Jerie");
    private static final Customer SOMCHAI = new Customer("C2", "Somchai", "Prasert");

    @BeforeEach
    void setUp() {
        var repository = new InMemoryAccountRepository();
        accountService = new AccountService(repository);
        transactionService = new TransactionService(repository);
        statementService = new StatementService(repository);
    }

    @Test
    void statement_showsAllTransactionsWithRunningBalance() {
        var account = accountService.openAccount(SVEN, AccountType.CHECKING);
        var id = account.getId();

        transactionService.deposit(id, new BigDecimal("1000.00"), "Salary");
        transactionService.withdraw(id, new BigDecimal("50.00"), "Coffee");
        transactionService.withdraw(id, new BigDecimal("200.00"), "Groceries");

        var statement = statementService.generateStatement(id);

        assertEquals(3, statement.entries().size());
        assertEquals("Sven Jerie", statement.customerName());
        assertEquals(new BigDecimal("750.00"), statement.balance());

        var entries = statement.entries();
        assertEquals(new BigDecimal("1000.00"), entries.get(0).runningBalance());
        assertEquals(new BigDecimal("950.00"), entries.get(1).runningBalance());
        assertEquals(new BigDecimal("750.00"), entries.get(2).runningBalance());
    }

    @Test
    void statement_showsTransferCorrectly() {
        var source = accountService.openAccount(SVEN, AccountType.CHECKING);
        var target = accountService.openAccount(SOMCHAI, AccountType.CHECKING);
        transactionService.deposit(source.getId(), new BigDecimal("500.00"), "Deposit");
        transactionService.transfer(source.getId(), target.getId(),
                new BigDecimal("200.00"), "Rent");

        var sourceStatement = statementService.generateStatement(source.getId());
        var targetStatement = statementService.generateStatement(target.getId());

        assertEquals(2, sourceStatement.entries().size());
        assertEquals(TransactionType.TRANSFER_OUT, sourceStatement.entries().get(1).type());
        assertEquals(new BigDecimal("300.00"), sourceStatement.balance());

        assertEquals(1, targetStatement.entries().size());
        assertEquals(TransactionType.TRANSFER_IN, targetStatement.entries().get(0).type());
        assertEquals(new BigDecimal("200.00"), targetStatement.balance());
    }

    @Test
    void statement_emptyAccount_hasNoEntries() {
        var account = accountService.openAccount(SVEN, AccountType.CHECKING);

        var statement = statementService.generateStatement(account.getId());

        assertTrue(statement.entries().isEmpty());
        assertEquals(BigDecimal.ZERO, statement.balance());
    }
}