package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static org.hamcrest.Matchers.equalTo;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
public class WarehouseEndpointIT {

  @Test
  public void testSimpleListWarehouses() {

    final String path = "warehouse";

    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(
            containsString("MWH.001"),
            containsString("MWH.012"),
            containsString("MWH.023"));
  }

  @Test
  public void testSimpleCheckingArchivingWarehouses() {

    final String path = "warehouse";

    // Verify warehouses exist before archiving
    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(
            containsString("MWH.001"),
            containsString("MWH.012"),
            containsString("MWH.023"),
            containsString("ZWOLLE-001"),
            containsString("AMSTERDAM-001"),
            containsString("TILBURG-001"));

    // Archive ZWOLLE-001
    given()
        .when()
        .delete(path + "/1")
        .then()
        .statusCode(204);

    // Verify ZWOLLE-001 is no longer returned
    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(
            not(containsString("ZWOLLE-001")),
            containsString("AMSTERDAM-001"),
            containsString("TILBURG-001"));
  }
  
  @Test
  public void testCreateWarehouse() {

    final String path = "warehouse";

    given()
        .contentType("application/json")
        .body(
            """
            {
              "businessUnitCode": "MWH.100",
              "location": "HELMOND-001",
              "capacity": 40,
              "stock": 20
            }
            """)
        .when()
        .post(path)
        .then()
        .statusCode(200)
        .body("businessUnitCode", equalTo("MWH.100"))
        .body("location", equalTo("HELMOND-001"))
        .body("capacity", equalTo(40))
        .body("stock", equalTo(20));
  }
  @Test
  public void testGetWarehouseById() {

      final String path = "warehouse/1";

      given()
          .when()
          .get(path)
          .then()
          .statusCode(200)
          .body("id", equalTo("1"))
          .body("businessUnitCode", equalTo("MWH.001"))
          .body("location", equalTo("ZWOLLE-001"))
          .body("capacity", equalTo(100))
          .body("stock", equalTo(10));
  }
  
  @Test
  public void testReplaceWarehouse() {

      final String path = "warehouse/MWH.012/replacement";

      given()
          .contentType("application/json")
          .body(
              """
              {
                "location": "AMSTERDAM-001",
                "capacity": 60,
                "stock": 5
              }
              """)
          .when()
          .post(path)
          .then()
          .statusCode(200)
          .body("businessUnitCode", equalTo("MWH.012"))
          .body("location", equalTo("AMSTERDAM-001"))
          .body("capacity", equalTo(60))
          .body("stock", equalTo(5));
  }
  @Test
  public void testGetWarehouseWithInvalidId() {

      given()
          .when()
          .get("warehouse/abc")
          .then()
          .statusCode(500);
  }

  @Test
  public void testGetNonExistingWarehouse() {

      given()
          .when()
          .get("warehouse/99999")
          .then()
          .statusCode(500);
  }

  @Test
  public void testCreateWarehouseWithInvalidLocation() {

      given()
          .contentType("application/json")
          .body(
              """
              {
                "businessUnitCode": "MWH.INVALID",
                "location": "INVALID-LOCATION",
                "capacity": 40,
                "stock": 20
              }
              """)
          .when()
          .post("warehouse")
          .then()
          .statusCode(500);
  }

  @Test
  public void testCreateDuplicateWarehouse() {

      given()
          .contentType("application/json")
          .body(
              """
              {
                "businessUnitCode": "MWH.001",
                "location": "ZWOLLE-001",
                "capacity": 20,
                "stock": 10
              }
              """)
          .when()
          .post("warehouse")
          .then()
          .statusCode(500);
  }
}