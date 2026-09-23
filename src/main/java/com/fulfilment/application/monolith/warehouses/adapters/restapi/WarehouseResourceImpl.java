package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.domain.usecases.ReplaceWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ArchiveWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.CreateWarehouseUseCase;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

  @Inject
  private WarehouseRepository warehouseRepository;

  @Inject
  private CreateWarehouseUseCase createWarehouseUseCase;
  
  @Inject
  private ArchiveWarehouseUseCase archiveWarehouseUseCase;
  
  @Inject
  private ReplaceWarehouseUseCase replaceWarehouseUseCase;

  @Override
  public List<Warehouse> listAllWarehousesUnits() {
    return warehouseRepository.getAll()
        .stream()
        .map(this::toWarehouseResponse)
        .toList();
  }

  @Override
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {

    var warehouse =
        new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();

    warehouse.businessUnitCode = data.getBusinessUnitCode();
    warehouse.location = data.getLocation();
    warehouse.capacity = data.getCapacity();
    warehouse.stock = data.getStock();
    warehouse.createdAt = LocalDateTime.now();

    createWarehouseUseCase.create(warehouse);

    return toWarehouseResponse(warehouse);
  }

  @Override
	public Warehouse getAWarehouseUnitByID(String id) {

		Long warehouseId;

		try {
			warehouseId = Long.valueOf(id);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Warehouse ID must be a valid number.");
		}

		var dbWarehouse = warehouseRepository.findById(warehouseId);

		if (dbWarehouse == null) {
			throw new IllegalArgumentException("Warehouse with ID " + id + " does not exist.");
		}

		var warehouse = dbWarehouse.toWarehouse();

		return toWarehouseResponse(warehouse);
	}
  

  @Override
	public void archiveAWarehouseUnitByID(String id) {

		Long warehouseId;

		try {
			warehouseId = Long.valueOf(id);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Warehouse ID must be a valid number.");
		}

		var dbWarehouse = warehouseRepository.findById(warehouseId);

		if (dbWarehouse == null) {
			throw new IllegalArgumentException("Warehouse with ID " + id + " does not exist.");
		}

		var warehouse = dbWarehouse.toWarehouse();

		archiveWarehouseUseCase.archive(warehouse);
	}
  

  @Override
  public Warehouse replaceTheCurrentActiveWarehouse(
      String businessUnitCode, @NotNull Warehouse data) {

      if (businessUnitCode == null || businessUnitCode.isBlank()) {
          throw new IllegalArgumentException(
              "Business unit code is required.");
      }

      if (data == null) {
          throw new IllegalArgumentException(
              "Replacement warehouse data is required.");
      }

      var warehouse =
          new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();

      warehouse.businessUnitCode = businessUnitCode;
      warehouse.location = data.getLocation();
      warehouse.capacity = data.getCapacity();
      warehouse.stock = data.getStock();

      replaceWarehouseUseCase.replace(warehouse);

      return toWarehouseResponse(warehouse);
  }

  private Warehouse toWarehouseResponse(
      com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {

    var response = new Warehouse();

    response.setId(
    	    warehouse.id != null ? warehouse.id.toString() : null);
    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);

    return response;
  }
}