package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArchiveWarehouseUseCaseTest {

    @Test
    void shouldArchiveWarehouse() {

        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();
        ArchiveWarehouseUseCase useCase =
                new ArchiveWarehouseUseCase(warehouseStore);

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "MWH.001";
        warehouse.stock = 20;
        warehouse.archivedAt = null;

        useCase.archive(warehouse);

        assertNotNull(warehouse.archivedAt);
        assertSame(warehouse, warehouseStore.updatedWarehouse);
    }

    @Test
    void shouldRejectNullWarehouse() {

        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();
        ArchiveWarehouseUseCase useCase =
                new ArchiveWarehouseUseCase(warehouseStore);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.archive(null));

        assertEquals(
                "Warehouse cannot be null.",
                exception.getMessage());
    }

    @Test
    void shouldRejectAlreadyArchivedWarehouse() {

        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();
        ArchiveWarehouseUseCase useCase =
                new ArchiveWarehouseUseCase(warehouseStore);

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "MWH.001";

        warehouse.archivedAt =
                java.time.LocalDateTime.now();

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.archive(warehouse));

        assertTrue(
                exception.getMessage().contains("already archived"));

        assertNull(warehouseStore.updatedWarehouse);
    }

    private static class FakeWarehouseStore implements WarehouseStore {

        private Warehouse updatedWarehouse;

        @Override
        public List<Warehouse> getAll() {
            return new ArrayList<>();
        }

        @Override
        public void create(Warehouse warehouse) {
        }

        @Override
        public void update(Warehouse warehouse) {
            this.updatedWarehouse = warehouse;
        }

        @Override
        public void remove(Warehouse warehouse) {
        }

        @Override
        public Warehouse findByBusinessUnitCode(String buCode) {
            return null;
        }
    }
}