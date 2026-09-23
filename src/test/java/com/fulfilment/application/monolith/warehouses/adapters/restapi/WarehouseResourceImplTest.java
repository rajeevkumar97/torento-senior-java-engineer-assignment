package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ArchiveWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.CreateWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ReplaceWarehouseUseCase;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WarehouseResourceImplTest {

  private WarehouseResourceImpl warehouseResource;

  private WarehouseRepository warehouseRepository;
  private CreateWarehouseUseCase createWarehouseUseCase;
  private ArchiveWarehouseUseCase archiveWarehouseUseCase;
  private ReplaceWarehouseUseCase replaceWarehouseUseCase;

  @BeforeEach
  void setUp() throws Exception {

    warehouseResource = new WarehouseResourceImpl();

    warehouseRepository = mock(WarehouseRepository.class);
    createWarehouseUseCase = mock(CreateWarehouseUseCase.class);
    archiveWarehouseUseCase = mock(ArchiveWarehouseUseCase.class);
    replaceWarehouseUseCase = mock(ReplaceWarehouseUseCase.class);

    setField(
        warehouseResource,
        "warehouseRepository",
        warehouseRepository);

    setField(
        warehouseResource,
        "createWarehouseUseCase",
        createWarehouseUseCase);

    setField(
        warehouseResource,
        "archiveWarehouseUseCase",
        archiveWarehouseUseCase);

    setField(
        warehouseResource,
        "replaceWarehouseUseCase",
        replaceWarehouseUseCase);
  }

  private void setField(
      Object target,
      String fieldName,
      Object value) throws Exception {

    Field field =
        target.getClass().getDeclaredField(fieldName);

    field.setAccessible(true);
    field.set(target, value);
  }

  @Test
  void testListAllWarehouses() {

    Warehouse warehouse =
        createWarehouse(
            10L,
            "MWH.TEST.001",
            "ZWOLLE-001",
            50,
            20);

    when(warehouseRepository.getAll())
        .thenReturn(List.of(warehouse));

    var result =
        warehouseResource.listAllWarehousesUnits();

    assertNotNull(result);
    assertEquals(1, result.size());

    assertEquals(
        "10",
        result.get(0).getId());

    assertEquals(
        "MWH.TEST.001",
        result.get(0).getBusinessUnitCode());

    assertEquals(
        "ZWOLLE-001",
        result.get(0).getLocation());

    assertEquals(
        50,
        result.get(0).getCapacity());

    assertEquals(
        20,
        result.get(0).getStock());

    verify(warehouseRepository)
        .getAll();
  }

  @Test
  void testCreateWarehouse() {

    var request =
        new com.warehouse.api.beans.Warehouse();

    request.setBusinessUnitCode(
        "MWH.TEST.CREATE");

    request.setLocation(
        "ZWOLLE-002");

    request.setCapacity(40);

    request.setStock(15);

    doAnswer(
            invocation -> {

              Warehouse warehouse =
                  invocation.getArgument(0);

              warehouse.id = 100L;

              return null;
            })
        .when(createWarehouseUseCase)
        .create(any(Warehouse.class));

    var result =
        warehouseResource
            .createANewWarehouseUnit(request);

    assertNotNull(result);

    assertEquals(
        "100",
        result.getId());

    assertEquals(
        "MWH.TEST.CREATE",
        result.getBusinessUnitCode());

    assertEquals(
        "ZWOLLE-002",
        result.getLocation());

    assertEquals(
        40,
        result.getCapacity());

    assertEquals(
        15,
        result.getStock());

    verify(createWarehouseUseCase)
        .create(any(Warehouse.class));
  }

  @Test
  void testGetWarehouseById() {

    DbWarehouse dbWarehouse =
        new DbWarehouse();

    dbWarehouse.id = 1L;
    dbWarehouse.businessUnitCode = "MWH.001";
    dbWarehouse.location = "ZWOLLE-001";
    dbWarehouse.capacity = 100;
    dbWarehouse.stock = 10;

    when(warehouseRepository.findById(1L))
        .thenReturn(dbWarehouse);

    var result =
        warehouseResource
            .getAWarehouseUnitByID("1");

    assertNotNull(result);

    assertEquals(
        "1",
        result.getId());

    assertEquals(
        "MWH.001",
        result.getBusinessUnitCode());

    assertEquals(
        "ZWOLLE-001",
        result.getLocation());

    assertEquals(
        100,
        result.getCapacity());

    assertEquals(
        10,
        result.getStock());

    verify(warehouseRepository)
        .findById(1L);
  }

  @Test
  void testGetWarehouseWithInvalidId() {

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                warehouseResource
                    .getAWarehouseUnitByID("abc"));

    assertEquals(
        "Warehouse ID must be a valid number.",
        exception.getMessage());

    verifyNoInteractions(
        warehouseRepository);
  }

  @Test
  void testGetNonExistingWarehouse() {

    when(warehouseRepository.findById(999L))
        .thenReturn(null);

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                warehouseResource
                    .getAWarehouseUnitByID("999"));

    assertEquals(
        "Warehouse with ID 999 does not exist.",
        exception.getMessage());

    verify(warehouseRepository)
        .findById(999L);
  }

  @Test
  void testArchiveWarehouse() {

    DbWarehouse dbWarehouse =
        new DbWarehouse();

    dbWarehouse.id = 1L;
    dbWarehouse.businessUnitCode = "MWH.001";
    dbWarehouse.location = "ZWOLLE-001";
    dbWarehouse.capacity = 100;
    dbWarehouse.stock = 10;

    when(warehouseRepository.findById(1L))
        .thenReturn(dbWarehouse);

    warehouseResource
        .archiveAWarehouseUnitByID("1");

    verify(warehouseRepository)
        .findById(1L);

    verify(archiveWarehouseUseCase)
        .archive(any(Warehouse.class));
  }

  @Test
  void testArchiveWarehouseWithInvalidId() {

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                warehouseResource
                    .archiveAWarehouseUnitByID("abc"));

    assertEquals(
        "Warehouse ID must be a valid number.",
        exception.getMessage());

    verifyNoInteractions(
        warehouseRepository);

    verifyNoInteractions(
        archiveWarehouseUseCase);
  }

  @Test
  void testArchiveNonExistingWarehouse() {

    when(warehouseRepository.findById(999L))
        .thenReturn(null);

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                warehouseResource
                    .archiveAWarehouseUnitByID("999"));

    assertEquals(
        "Warehouse with ID 999 does not exist.",
        exception.getMessage());

    verify(warehouseRepository)
        .findById(999L);

    verifyNoInteractions(
        archiveWarehouseUseCase);
  }

  @Test
  void testReplaceWarehouse() {

    var request =
        new com.warehouse.api.beans.Warehouse();

    request.setLocation("ZWOLLE-001");
    request.setCapacity(120);
    request.setStock(10);

    doAnswer(
            invocation -> {

              Warehouse warehouse =
                  invocation.getArgument(0);

              warehouse.id = 200L;
              warehouse.location = "ZWOLLE-001";

              return null;
            })
        .when(replaceWarehouseUseCase)
        .replace(any(Warehouse.class));

    var result =
        warehouseResource
            .replaceTheCurrentActiveWarehouse(
                "MWH.001",
                request);

    assertNotNull(result);

    assertEquals(
        "200",
        result.getId());

    assertEquals(
        "MWH.001",
        result.getBusinessUnitCode());

    assertEquals(
        "ZWOLLE-001",
        result.getLocation());

    assertEquals(
        120,
        result.getCapacity());

    assertEquals(
        10,
        result.getStock());

    verify(replaceWarehouseUseCase)
        .replace(any(Warehouse.class));
  }

  @Test
  void testReplaceWarehouseWithMissingBusinessUnitCode() {

    var request =
        new com.warehouse.api.beans.Warehouse();

    request.setLocation("ZWOLLE-001");
    request.setCapacity(100);
    request.setStock(10);

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                warehouseResource
                    .replaceTheCurrentActiveWarehouse(
                        "",
                        request));

    assertEquals(
        "Business unit code is required.",
        exception.getMessage());

    verifyNoInteractions(
        replaceWarehouseUseCase);
  }

  @Test
  void testReplaceWarehouseWithNullData() {

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                warehouseResource
                    .replaceTheCurrentActiveWarehouse(
                        "MWH.001",
                        null));

    assertEquals(
        "Replacement warehouse data is required.",
        exception.getMessage());

    verifyNoInteractions(
        replaceWarehouseUseCase);
  }

  private Warehouse createWarehouse(
      Long id,
      String businessUnitCode,
      String location,
      Integer capacity,
      Integer stock) {

    Warehouse warehouse =
        new Warehouse();

    warehouse.id = id;
    warehouse.businessUnitCode =
        businessUnitCode;
    warehouse.location = location;
    warehouse.capacity = capacity;
    warehouse.stock = stock;

    return warehouse;
  }
}