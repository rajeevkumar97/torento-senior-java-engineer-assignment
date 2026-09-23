package com.fulfilment.application.monolith.warehouses.domain.usecases;

import jakarta.transaction.Transactional;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  public CreateWarehouseUseCase(
      WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  @Transactional
  public void create(Warehouse warehouse) {

    // 1. Business Unit Code must be unique
    if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
      throw new IllegalArgumentException(
          "Warehouse with business unit code "
              + warehouse.businessUnitCode
              + " already exists.");
    }

    // 2. Location must exist
    Location location = locationResolver.resolveByIdentifier(warehouse.location);

    if (location == null) {
      throw new IllegalArgumentException(
          "Location " + warehouse.location + " does not exist.");
    }

    // 3. Check maximum number of active warehouses at the location
    long warehousesAtLocation =
        warehouseStore.getAll().stream()
            .filter(existing -> warehouse.location.equals(existing.location))
            .filter(existing -> existing.archivedAt == null)
            .count();

    if (warehousesAtLocation >= location.maxNumberOfWarehouses) {
      throw new IllegalArgumentException(
          "Maximum number of warehouses for location "
              + warehouse.location
              + " has been reached.");
    }

    // 4. Validate capacity and stock
    if (warehouse.capacity == null || warehouse.capacity < 0) {
      throw new IllegalArgumentException(
          "Warehouse capacity must be a valid non-negative value.");
    }

    if (warehouse.stock == null
        || warehouse.stock < 0
        || warehouse.capacity < warehouse.stock) {
      throw new IllegalArgumentException(
          "Warehouse capacity must be greater than or equal to stock.");
    }

    // 5. Validate total capacity of the location
    int currentTotalCapacity =
        warehouseStore.getAll().stream()
            .filter(existing -> warehouse.location.equals(existing.location))
            .filter(existing -> existing.archivedAt == null)
            .filter(existing -> existing.capacity != null)
            .mapToInt(existing -> existing.capacity)
            .sum();

    if (currentTotalCapacity + warehouse.capacity > location.maxCapacity) {
      throw new IllegalArgumentException(
          "Total warehouse capacity exceeds the maximum capacity of location "
              + warehouse.location
              + ".");
    }

    // 6. If all validations pass, create the warehouse
    warehouseStore.create(warehouse);
  }
}