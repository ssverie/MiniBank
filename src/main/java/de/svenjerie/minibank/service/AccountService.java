package de.svenjerie.minibank.service;

import de.svenjerie.minibank.exception.AccountNotFoundException;
import de.svenjerie.minibank.model.Account;
import de.svenjerie.minibank.model.AccountType;
import de.svenjerie.minibank.model.Customer;
import de.svenjerie.minibank.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public Account openAccount(Customer customer, AccountType type) {
        var account = new Account(customer, type);
        return repository.save(account);
    }

    public Account findAccount(String accountId) {
        return repository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    public List<Account> findAllAccounts() {
        return repository.findAll();
    }

    public void closeAccount(String accountId) {
        var account = findAccount(accountId);

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException(
                    "Cannot close account with non-zero balance: " + account.getBalance());
        }

        repository.deleteById(accountId);
    }
}