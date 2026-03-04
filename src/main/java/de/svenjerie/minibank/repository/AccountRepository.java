package de.svenjerie.minibank.repository;

import de.svenjerie.minibank.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(String id);

    List<Account> findAll();

    void deleteById(String id);
}