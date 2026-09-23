package com.fulfilment.application.monolith.warehouses.domain.usecases;

import jakarta.transaction.Transactional;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Override
  @Transactional
  public void replace(Warehouse newWarehouse) {

    if (newWarehouse == null) {
      throw new IllegalArgumentException("Replacement warehouse cannot be null.");
    }

    if (newWarehouse.businessUnitCode == null
        || newWarehouse.businessUnitCode.isBlank()) {
      throw new IllegalArgumentException(
          "Business unit code is required.");
    }

    Warehouse currentWarehouse =
        warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);

    if (currentWarehouse == null) {
      throw new IllegalArgumentException(
          "Active warehouse with business unit code "
              + newWarehouse.businessUnitCode
              + " does not exist.");
    }

    if (currentWarehouse.archivedAt != null) {
      throw new IllegalArgumentException(
          "Warehouse with business unit code "
              + newWarehouse.businessUnitCode
              + " is already archived.");
    }

    if (newWarehouse.stock == null) {
      throw new IllegalArgumentException(
          "Replacement warehouse stock is required.");
    }

    if (currentWarehouse.stock == null) {
      throw new IllegalArgumentException(
          "Current warehouse stock is not available.");
    }

    if (!newWarehouse.stock.equals(currentWarehouse.stock)) {
      throw new IllegalArgumentException(
          "Replacement warehouse stock must match the stock of the current warehouse.");
    }

    if (newWarehouse.capacity == null || newWarehouse.capacity < 0) {
      throw new IllegalArgumentException(
          "Replacement warehouse capacity must be a valid non-negative value.");
    }

    if (newWarehouse.capacity < currentWarehouse.stock) {
      throw new IllegalArgumentException(
          "Replacement warehouse capacity must be able to accommodate the current stock.");
    }

    newWarehouse.location = currentWarehouse.location;
    newWarehouse.createdAt = LocalDateTime.now();

    // Archive the current warehouse.
    currentWarehouse.archivedAt = LocalDateTime.now();
    warehouseStore.update(currentWarehouse);

    // Create the replacement warehouse.
    warehouseStore.create(newWarehouse);
  }
}