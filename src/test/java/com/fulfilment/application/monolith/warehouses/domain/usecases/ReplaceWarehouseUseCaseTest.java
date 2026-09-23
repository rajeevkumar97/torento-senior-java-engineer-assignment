package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReplaceWarehouseUseCaseTest {

    @Test
    void shouldReplaceWarehouseSuccessfully() {

        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

        Warehouse currentWarehouse =
                warehouse("MWH.001", "ZWOLLE-001", 40, 20);

        warehouseStore.warehouses.add(currentWarehouse);

        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(warehouseStore);

        Warehouse newWarehouse =
                warehouse("MWH.001", "ZWOLLE-001", 50, 20);

        useCase.replace(newWarehouse);

        assertNotNull(currentWarehouse.archivedAt);
        assertSame(currentWarehouse, warehouseStore.updatedWarehouse);
        assertSame(newWarehouse, warehouseStore.createdWarehouse);
        assertEquals(20, newWarehouse.stock);
        assertEquals(50, newWarehouse.capacity);
    }

    @Test
    void shouldRejectNullWarehouse() {

        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(warehouseStore);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.replace(null));

        assertEquals(
                "Replacement warehouse cannot be null.",
                exception.getMessage());
    }

    @Test
    void shouldRejectMissingBusinessUnitCode() {

        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(warehouseStore);

        Warehouse warehouse =
                warehouse(null, "ZWOLLE-001", 40, 20);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.replace(warehouse));

        assertEquals(
                "Business unit code is required.",
                exception.getMessage());
    }

    @Test
    void shouldRejectBlankBusinessUnitCode() {

        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(warehouseStore);

        Warehouse warehouse =
                warehouse("   ", "ZWOLLE-001", 40, 20);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.replace(warehouse));

        assertEquals(
                "Business unit code is required.",
                exception.getMessage());
    }

    @Test
    void shouldRejectWhenCurrentWarehouseDoesNotExist() {

        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(warehouseStore);

        Warehouse warehouse =
                warehouse("MWH.001", "ZWOLLE-001", 40, 20);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.replace(warehouse));

        assertTrue(
                exception.getMessage().contains("does not exist"));
    }

    @Test
    void shouldRejectAlreadyArchivedWarehouse() {

        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

        Warehouse currentWarehouse =
                warehouse("MWH.001", "ZWOLLE-001", 40, 20);

        currentWarehouse.archivedAt =
                java.time.LocalDateTime.now();

        warehouseStore.warehouses.add(currentWarehouse);

        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(warehouseStore);

        Warehouse newWarehouse =
                warehouse("MWH.001", "ZWOLLE-001", 50, 20);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.replace(newWarehouse));

        assertTrue(
                exception.getMessage().contains("already archived"));
    }

    @Test
    void shouldRejectMissingStock() {

        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

        Warehouse currentWarehouse =
                warehouse("MWH.001", "ZWOLLE-001", 40, 20);

        warehouseStore.warehouses.add(currentWarehouse);

        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(warehouseStore);

        Warehouse newWarehouse =
                warehouse("MWH.001", "ZWOLLE-001", 50, null);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.replace(newWarehouse));

        assertEquals(
                "Replacement warehouse stock is required.",
                exception.getMessage());
    }

    @Test
    void shouldRejectStockMismatch() {

        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

        Warehouse currentWarehouse =
                warehouse("MWH.001", "ZWOLLE-001", 40, 20);

        warehouseStore.warehouses.add(currentWarehouse);

        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(warehouseStore);

        Warehouse newWarehouse =
                warehouse("MWH.001", "ZWOLLE-001", 50, 15);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.replace(newWarehouse));

        assertTrue(
                exception.getMessage().contains("must match"));
    }

    @Test
    void shouldRejectInvalidCapacity() {

        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

        Warehouse currentWarehouse =
                warehouse("MWH.001", "ZWOLLE-001", 40, 20);

        warehouseStore.warehouses.add(currentWarehouse);

        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(warehouseStore);

        Warehouse newWarehouse =
                warehouse("MWH.001", "ZWOLLE-001", -1, 20);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.replace(newWarehouse));

        assertTrue(
                exception.getMessage().contains("capacity"));
    }

    @Test
    void shouldRejectCapacitySmallerThanCurrentStock() {

        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

        Warehouse currentWarehouse =
                warehouse("MWH.001", "ZWOLLE-001", 40, 20);

        warehouseStore.warehouses.add(currentWarehouse);

        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(warehouseStore);

        Warehouse newWarehouse =
                warehouse("MWH.001", "ZWOLLE-001", 15, 20);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.replace(newWarehouse));

        assertTrue(
                exception.getMessage().contains("accommodate"));
    }

    private Warehouse warehouse(
            String businessUnitCode,
            String location,
            Integer capacity,
            Integer stock) {

        Warehouse warehouse = new Warehouse();

        warehouse.businessUnitCode = businessUnitCode;
        warehouse.location = location;
        warehouse.capacity = capacity;
        warehouse.stock = stock;

        return warehouse;
    }

    private static class FakeWarehouseStore implements WarehouseStore {

        private final List<Warehouse> warehouses = new ArrayList<>();

        private Warehouse updatedWarehouse;
        private Warehouse createdWarehouse;

        @Override
        public List<Warehouse> getAll() {
            return warehouses;
        }

        @Override
        public void create(Warehouse warehouse) {
            createdWarehouse = warehouse;
            warehouses.add(warehouse);
        }

        @Override
        public void update(Warehouse warehouse) {
            updatedWarehouse = warehouse;
        }

        @Override
        public void remove(Warehouse warehouse) {
        }

        @Override
        public Warehouse findByBusinessUnitCode(String buCode) {
            return warehouses.stream()
                .filter(w -> buCode.equals(w.businessUnitCode))
                .findFirst()
                .orElse(null);
        }
    }
}