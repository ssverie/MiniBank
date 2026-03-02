package de.svenjerie.minibank.model;

import java.util.Objects;

public record Customer(String id, String firstName, String lastName) {

    public Customer {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(firstName, "firstName must not be null");
        Objects.requireNonNull(lastName, "lastName must not be null");

        if (firstName.isBlank()) {
            throw new IllegalArgumentException("firstName must not be blank");
        }
        if (lastName.isBlank()) {
            throw new IllegalArgumentException("lastName must not be blank");
        }
    }

    public String fullName() {
        return firstName + " " + lastName;
    }
}