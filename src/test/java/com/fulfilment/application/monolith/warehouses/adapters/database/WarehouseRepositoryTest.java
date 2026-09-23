package com.fulfilment.application.monolith.warehouses.adapters.database;

import static org.junit.jupiter.api.Assertions.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class WarehouseRepositoryTest {

  @Inject WarehouseRepository warehouseRepository;

  @Test
  public void testGetAllReturnsOnlyActiveWarehouses() {
    List<Warehouse> warehouses = warehouseRepository.getAll();

    assertNotNull(warehouses);
    assertFalse(warehouses.isEmpty());

    assertTrue(
        warehouses.stream()
            .allMatch(warehouse -> warehouse.archivedAt == null));

    assertTrue(
        warehouses.stream()
            .anyMatch(warehouse -> "MWH.001".equals(warehouse.businessUnitCode)));
  }

  @Test
  @Transactional
  public void testCreateWarehouse() {
    Warehouse warehouse = new Warehouse();

    warehouse.businessUnitCode = "MWH.TEST.CREATE";
    warehouse.location = "ZWOLLE-002";
    warehouse.capacity = 30;
    warehouse.stock = 10;
    warehouse.createdAt = LocalDateTime.now();
    warehouse.archivedAt = null;

    warehouseRepository.create(warehouse);

    assertNotNull(warehouse.id);

    Warehouse saved =
        warehouseRepository.findByBusinessUnitCode("MWH.TEST.CREATE");

    assertNotNull(saved);
    assertEquals("MWH.TEST.CREATE", saved.businessUnitCode);
    assertEquals("ZWOLLE-002", saved.location);
    assertEquals(30, saved.capacity);
    assertEquals(10, saved.stock);
    assertNotNull(saved.createdAt);
    assertNull(saved.archivedAt);
  }

  @Test
  @Transactional
  public void testCreateArchivedWarehouse() {
    Warehouse warehouse = new Warehouse();

    warehouse.businessUnitCode = "MWH.TEST.ARCHIVED";
    warehouse.location = "ZWOLLE-002";
    warehouse.capacity = 30;
    warehouse.stock = 10;
    warehouse.createdAt = LocalDateTime.now();
    warehouse.archivedAt = LocalDateTime.now();

    warehouseRepository.create(warehouse);

    assertNotNull(warehouse.id);

    Warehouse saved =
        warehouseRepository.findByBusinessUnitCode("MWH.TEST.ARCHIVED");

    assertNotNull(saved);
    assertNotNull(saved.archivedAt);
  }

  @Test
  @Transactional
  public void testCreateWarehouseWithNullOptionalFields() {
    Warehouse warehouse = new Warehouse();

    warehouse.businessUnitCode = "MWH.TEST.NULLS";
    warehouse.location = "ZWOLLE-002";
    warehouse.capacity = null;
    warehouse.stock = null;
    warehouse.createdAt = null;
    warehouse.archivedAt = null;

    warehouseRepository.create(warehouse);

    assertNotNull(warehouse.id);

    Warehouse saved =
        warehouseRepository.findByBusinessUnitCode("MWH.TEST.NULLS");

    assertNotNull(saved);
    assertEquals("MWH.TEST.NULLS", saved.businessUnitCode);
    assertNull(saved.capacity);
    assertNull(saved.stock);
    assertNull(saved.createdAt);
    assertNull(saved.archivedAt);
  }

  @Test
  @Transactional
  public void testUpdateWarehouse() {
    Warehouse warehouse = new Warehouse();

    warehouse.businessUnitCode = "MWH.TEST.UPDATE";
    warehouse.location = "ZWOLLE-002";
    warehouse.capacity = 30;
    warehouse.stock = 10;
    warehouse.createdAt = LocalDateTime.now();

    warehouseRepository.create(warehouse);

    warehouse.location = "AMSTERDAM-002";
    warehouse.capacity = 40;
    warehouse.stock = 15;
    warehouse.archivedAt = LocalDateTime.now();

    warehouseRepository.update(warehouse);

    Warehouse updated =
        warehouseRepository.findByBusinessUnitCode("MWH.TEST.UPDATE");

    assertNotNull(updated);
    assertEquals("AMSTERDAM-002", updated.location);
    assertEquals(40, updated.capacity);
    assertEquals(15, updated.stock);
    assertNotNull(updated.archivedAt);
  }

  @Test
  public void testUpdateNonExistingWarehouse() {
    Warehouse warehouse = new Warehouse();

    warehouse.businessUnitCode = "MWH.DOES.NOT.EXIST";
    warehouse.location = "ZWOLLE-002";
    warehouse.capacity = 20;
    warehouse.stock = 5;

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> warehouseRepository.update(warehouse));

    assertTrue(exception.getMessage().contains("does not exist"));
  }

  @Test
  public void testFindByBusinessUnitCodeReturnsWarehouse() {
    Warehouse warehouse =
        warehouseRepository.findByBusinessUnitCode("MWH.001");

    assertNotNull(warehouse);
    assertEquals("MWH.001", warehouse.businessUnitCode);
    assertEquals("ZWOLLE-001", warehouse.location);
    assertEquals(100, warehouse.capacity);
    assertEquals(10, warehouse.stock);
    assertNotNull(warehouse.createdAt);
    assertNull(warehouse.archivedAt);
  }

  @Test
  public void testFindByBusinessUnitCodeReturnsArchivedWarehouse() {
    Warehouse warehouse =
        warehouseRepository.findByBusinessUnitCode("MWH.023");

    assertNotNull(warehouse);
    assertEquals("MWH.023", warehouse.businessUnitCode);
  }

  @Test
  public void testFindByBusinessUnitCodeReturnsNullWhenMissing() {
    Warehouse warehouse =
        warehouseRepository.findByBusinessUnitCode("MWH.NOT.FOUND");

    assertNull(warehouse);
  }

  @Test
  @Transactional
  public void testRemoveWarehouse() {
    Warehouse warehouse = new Warehouse();

    warehouse.businessUnitCode = "MWH.TEST.REMOVE";
    warehouse.location = "ZWOLLE-002";
    warehouse.capacity = 20;
    warehouse.stock = 5;
    warehouse.createdAt = LocalDateTime.now();

    warehouseRepository.create(warehouse);

    assertNotNull(
        warehouseRepository.findByBusinessUnitCode("MWH.TEST.REMOVE"));

    warehouseRepository.remove(warehouse);

    assertNull(
        warehouseRepository.findByBusinessUnitCode("MWH.TEST.REMOVE"));
  }

  @Test
  public void testRemoveNonExistingWarehouseDoesNothing() {
    Warehouse warehouse = new Warehouse();

    warehouse.businessUnitCode = "MWH.NEVER.EXISTED";

    assertDoesNotThrow(
        () -> warehouseRepository.remove(warehouse));
  }
}