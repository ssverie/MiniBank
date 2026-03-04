package de.svenjerie.minibank.repository;

import de.svenjerie.minibank.model.Account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryAccountRepository implements AccountRepository {

    private final Map<String, Account> accounts = new HashMap<>();

    @Override
    public Account save(Account account) {
        accounts.put(account.getId(), account);
        return account;
    }

    @Override
    public Optional<Account> findById(String id) {
        return Optional.ofNullable(accounts.get(id));
    }

    @Override
    public List<Account> findAll() {
        return List.copyOf(accounts.values());
    }

    @Override
    public void deleteById(String id) {
        accounts.remove(id);
    }
}