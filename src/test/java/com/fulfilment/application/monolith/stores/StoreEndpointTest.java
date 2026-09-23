package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class StoreEndpointTest {

  @Test
  public void testGetAllStores() {
    given()
        .when()
        .get("/store")
        .then()
        .statusCode(200)
        .body(
            containsString("TONSTAD"),
            containsString("KALLAX"),
            containsString("BESTÅ"));
  }

  @Test
  public void testGetStoreById() {
    given()
        .when()
        .get("/store/1")
        .then()
        .statusCode(200)
        .body(
            containsString("TONSTAD"),
            containsString("10"));
  }

  @Test
  public void testGetNonExistingStore() {
    given()
        .when()
        .get("/store/99999")
        .then()
        .statusCode(404);
  }

  @Test
  public void testCreateStore() {
    String storeName = "TEST_STORE_CREATE";

    given()
        .contentType("application/json")
        .body(
            """
            {
              "name": "TEST_STORE_CREATE",
              "quantityProductsInStock": 15
            }
            """)
        .when()
        .post("/store")
        .then()
        .statusCode(201)
        .body(containsString(storeName));
  }

  @Test
  public void testCreateStoreWithInvalidId() {
    given()
        .contentType("application/json")
        .body(
            """
            {
              "id": 999,
              "name": "INVALID_ID_STORE",
              "quantityProductsInStock": 10
            }
            """)
        .when()
        .post("/store")
        .then()
        .statusCode(422);
  }

  @Test
  public void testUpdateStore() {
    given()
        .contentType("application/json")
        .body(
            """
            {
              "name": "TONSTAD_UPDATED",
              "quantityProductsInStock": 20
            }
            """)
        .when()
        .put("/store/1")
        .then()
        .statusCode(200)
        .body(
            containsString("TONSTAD_UPDATED"),
            containsString("20"));
  }

  @Test
  public void testUpdateNonExistingStore() {
    given()
        .contentType("application/json")
        .body(
            """
            {
              "name": "UNKNOWN_STORE",
              "quantityProductsInStock": 10
            }
            """)
        .when()
        .put("/store/99999")
        .then()
        .statusCode(404);
  }

  @Test
  public void testUpdateStoreWithoutName() {
    given()
        .contentType("application/json")
        .body(
            """
            {
              "quantityProductsInStock": 10
            }
            """)
        .when()
        .put("/store/1")
        .then()
        .statusCode(422);
  }

  @Test
  public void testPatchStore() {
    given()
        .contentType("application/json")
        .body(
            """
            {
              "name": "TONSTAD_PATCHED",
              "quantityProductsInStock": 25
            }
            """)
        .when()
        .patch("/store/1")
        .then()
        .statusCode(200)
        .body(
            containsString("TONSTAD_PATCHED"),
            containsString("25"));
  }

  @Test
  public void testPatchNonExistingStore() {
    given()
        .contentType("application/json")
        .body(
            """
            {
              "name": "UNKNOWN_PATCH_STORE",
              "quantityProductsInStock": 10
            }
            """)
        .when()
        .patch("/store/99999")
        .then()
        .statusCode(404);
  }

  @Test
  public void testDeleteStore() {
    // Create a store specifically for this test so we don't destroy seed data.
    given()
        .contentType("application/json")
        .body(
            """
            {
              "name": "TEST_STORE_DELETE",
              "quantityProductsInStock": 5
            }
            """)
        .when()
        .post("/store")
        .then()
        .statusCode(201);

    // Find the created store by listing stores.
    String storeId =
        given()
            .when()
            .get("/store")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getString("find { it.name == 'TEST_STORE_DELETE' }.id");

    given()
        .when()
        .delete("/store/" + storeId)
        .then()
        .statusCode(204);

    given()
        .when()
        .get("/store/" + storeId)
        .then()
        .statusCode(404);
  }

  @Test
  public void testDeleteNonExistingStore() {
    given()
        .when()
        .delete("/store/99999")
        .then()
        .statusCode(404);
  }
}