package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StoreTest {

    @Test
    void testDefaultConstructor() {
        Store store = new Store();

        assertNotNull(store);
        assertNull(store.name);
        assertEquals(0, store.quantityProductsInStock);
    }
}