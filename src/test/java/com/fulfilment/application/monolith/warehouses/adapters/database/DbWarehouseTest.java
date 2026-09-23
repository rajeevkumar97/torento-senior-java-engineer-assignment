package com.fulfilment.application.monolith.warehouses.adapters.database;

import static org.junit.jupiter.api.Assertions.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class DbWarehouseTest {

  @Test
  void testToWarehouseCopiesAllFields() {
    DbWarehouse dbWarehouse = new DbWarehouse();

    LocalDateTime createdAt = LocalDateTime.of(2026, 9, 23, 10, 30);
    LocalDateTime archivedAt = LocalDateTime.of(2026, 9, 24, 11, 45);

    dbWarehouse.id = 100L;
    dbWarehouse.businessUnitCode = "MWH.TEST";
    dbWarehouse.location = "ZWOLLE-001";
    dbWarehouse.capacity = 100;
    dbWarehouse.stock = 25;
    dbWarehouse.createdAt = createdAt;
    dbWarehouse.archivedAt = archivedAt;

    Warehouse result = dbWarehouse.toWarehouse();

    assertNotNull(result);
    assertEquals(100L, result.id);
    assertEquals("MWH.TEST", result.businessUnitCode);
    assertEquals("ZWOLLE-001", result.location);
    assertEquals(100, result.capacity);
    assertEquals(25, result.stock);
    assertEquals(createdAt, result.createdAt);
    assertEquals(archivedAt, result.archivedAt);
  }

  @Test
  void testToWarehouseWithNullValues() {
    DbWarehouse dbWarehouse = new DbWarehouse();

    Warehouse result = dbWarehouse.toWarehouse();

    assertNotNull(result);
    assertNull(result.id);
    assertNull(result.businessUnitCode);
    assertNull(result.location);
    assertNull(result.capacity);
    assertNull(result.stock);
    assertNull(result.createdAt);
    assertNull(result.archivedAt);
  }
}