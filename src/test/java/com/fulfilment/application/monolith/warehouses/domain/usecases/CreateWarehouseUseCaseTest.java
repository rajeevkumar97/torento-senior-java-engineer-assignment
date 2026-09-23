package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.location.LocationGateway;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateWarehouseUseCaseTest {

    @Test
    void shouldCreateValidWarehouse() {

        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();
        LocationGateway locationGateway = new LocationGateway();

        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(warehouseStore, locationGateway);

        Warehouse warehouse = warehouse(
                "TEST-001",
                "ZWOLLE-001",
                30,
                20
        );

        useCase.create(warehouse);

        assertSame(warehouse, warehouseStore.createdWarehouse);
    }

    @Test
    void shouldRejectDuplicateBusinessUnitCode() {

        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();
        LocationGateway locationGateway = new LocationGateway();

        Warehouse existing = warehouse(
                "TEST-001",
                "ZWOLLE-001",
                20,
                10
        );

        warehouseStore.warehouses.add(existing);

        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(warehouseStore, locationGateway);

        Warehouse warehouse = warehouse(
                "TEST-001",
                "ZWOLLE-002",
                20,
                10
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.create(warehouse)
                );

        assertTrue(
                exception.getMessage().contains("already exists")
        );
    }

    @Test
    void shouldRejectInvalidLocation() {

        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();
        LocationGateway locationGateway = new LocationGateway();

        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(warehouseStore, locationGateway);

        Warehouse warehouse = warehouse(
                "TEST-001",
                "INVALID-LOCATION",
                20,
                10
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.create(warehouse)
                );

        assertTrue(
                exception.getMessage().contains("does not exist")
        );
    }

    @Test
    void shouldRejectNegativeCapacity() {

        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();
        LocationGateway locationGateway = new LocationGateway();

        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(warehouseStore, locationGateway);

        Warehouse warehouse = warehouse(
                "TEST-001",
                "ZWOLLE-001",
                -1,
                0
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.create(warehouse)
                );

        assertTrue(
                exception.getMessage().contains("capacity")
        );
    }

    @Test
    void shouldRejectNegativeStock() {

        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();
        LocationGateway locationGateway = new LocationGateway();

        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(warehouseStore, locationGateway);

        Warehouse warehouse = warehouse(
                "TEST-001",
                "ZWOLLE-001",
                20,
                -1
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.create(warehouse)
                );

        assertTrue(
                exception.getMessage().contains("capacity")
        );
    }

    @Test
    void shouldRejectStockGreaterThanCapacity() {

        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();
        LocationGateway locationGateway = new LocationGateway();

        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(warehouseStore, locationGateway);

        Warehouse warehouse = warehouse(
                "TEST-001",
                "ZWOLLE-001",
                10,
                20
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.create(warehouse)
                );

        assertTrue(
                exception.getMessage().contains("greater than or equal")
        );
    }

    @Test
    void shouldRejectTooManyWarehousesAtLocation() {

        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();
        LocationGateway locationGateway = new LocationGateway();

        // ZWOLLE-001 allows only 1 warehouse.
        warehouseStore.warehouses.add(
                warehouse("EXISTING-001", "ZWOLLE-001", 20, 10)
        );

        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(warehouseStore, locationGateway);

        Warehouse warehouse =
                warehouse("TEST-001", "ZWOLLE-001", 10, 5);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.create(warehouse)
                );

        assertTrue(
                exception.getMessage().contains("Maximum number of warehouses")
        );
    }

    @Test
    void shouldRejectCapacityExceedingLocationMaximum() {

        FakeWarehouseStore warehouseStore = new FakeWarehouseStore();
        LocationGateway locationGateway = new LocationGateway();

        // AMSTERDAM-002 has maximum capacity 75.
        warehouseStore.warehouses.add(
                warehouse("EXISTING-001", "AMSTERDAM-002", 50, 20)
        );

        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(warehouseStore, locationGateway);

        Warehouse warehouse =
                warehouse("TEST-001", "AMSTERDAM-002", 30, 20);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.create(warehouse)
                );

        assertTrue(
                exception.getMessage().contains("Total warehouse capacity")
        );
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