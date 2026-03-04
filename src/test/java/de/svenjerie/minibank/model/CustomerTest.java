package de.svenjerie.minibank.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void create_validCustomer() {
        var customer = new Customer("C1", "Sven", "Jerie");
        assertEquals("Sven Jerie", customer.fullName());
    }

    @Test
    void create_withNullName_throwsException() {
        assertThrows(NullPointerException.class,
                () -> new Customer("C1", null, "Jerie"));
    }

    @Test
    void create_withBlankName_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Customer("C1", "", "Jerie"));
    }
}