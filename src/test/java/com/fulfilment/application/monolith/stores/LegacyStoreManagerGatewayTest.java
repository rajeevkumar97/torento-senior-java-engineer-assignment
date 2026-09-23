package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class LegacyStoreManagerGatewayTest {

  @Test
  void testCreateStoreOnLegacySystem() {
    LegacyStoreManagerGateway gateway = new LegacyStoreManagerGateway();

    Store store = new Store("TEST-STORE");
    store.quantityProductsInStock = 25;

    assertDoesNotThrow(() -> gateway.createStoreOnLegacySystem(store));
  }

  @Test
  void testUpdateStoreOnLegacySystem() {
    LegacyStoreManagerGateway gateway = new LegacyStoreManagerGateway();

    Store store = new Store("TEST-STORE");
    store.quantityProductsInStock = 50;

    assertDoesNotThrow(() -> gateway.updateStoreOnLegacySystem(store));
  }
}