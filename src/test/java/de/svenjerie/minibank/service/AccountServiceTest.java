package de.svenjerie.minibank.service;

import de.svenjerie.minibank.exception.AccountNotFoundException;
import de.svenjerie.minibank.model.AccountType;
import de.svenjerie.minibank.model.Customer;
import de.svenjerie.minibank.repository.InMemoryAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {

    private AccountService accountService;
    private TransactionService transactionService;

    private static final Customer SVEN = new Customer("C1", "Sven", "Jerie");

    @BeforeEach
    void setUp() {
        var repository = new InMemoryAccountRepository();
        accountService = new AccountService(repository);
        transactionService = new TransactionService(repository);
    }

    @Test
    void openAccount_createsAccountWithZeroBalance() {
        var account = accountService.openAccount(SVEN, AccountType.CHECKING);

        assertNotNull(account.getId());
        assertEquals(SVEN, account.getCustomer());
        assertEquals(AccountType.CHECKING, account.getType());
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }

    @Test
    void findAccount_returnsExistingAccount() {
        var created = accountService.openAccount(SVEN, AccountType.SAVINGS);

        var found = accountService.findAccount(created.getId());

        assertEquals(created.getId(), found.getId());
    }

    @Test
    void findAccount_withUnknownId_throwsException() {
        assertThrows(AccountNotFoundException.class,
                () -> accountService.findAccount("DOES_NOT_EXIST"));
    }

    @Test
    void closeAccount_withZeroBalance_succeeds() {
        var account = accountService.openAccount(SVEN, AccountType.CHECKING);

        accountService.closeAccount(account.getId());

        assertThrows(AccountNotFoundException.class,
                () -> accountService.findAccount(account.getId()));
    }

    @Test
    void closeAccount_withNonZeroBalance_throwsException() {
        var account = accountService.openAccount(SVEN, AccountType.CHECKING);
        transactionService.deposit(account.getId(), new BigDecimal("100.00"), "Deposit");

        assertThrows(IllegalStateException.class,
                () -> accountService.closeAccount(account.getId()));
    }

    @Test
    void findAllAccounts_returnsAllAccounts() {
        accountService.openAccount(SVEN, AccountType.CHECKING);
        accountService.openAccount(SVEN, AccountType.SAVINGS);

        assertEquals(2, accountService.findAllAccounts().size());
    }
}