package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.*;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StoreResourceTest {

    @Inject
    StoreResource storeResource;

    @InjectMock
    LegacyStoreManagerGateway legacyStoreManagerGateway;

    @Test
    void testGetStores() {
        assertNotNull(storeResource.get());
    }

    @Test
    void testGetSingleStoreNotFound() {
        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> storeResource.getSingle(999999L));

        assertEquals(404, exception.getResponse().getStatus());
    }

    @Test
    void testCreateStore() {
        Store store = new Store();
        store.name = "Direct Test Store";
        store.quantityProductsInStock = 10;

        Response response = storeResource.create(store);

        assertEquals(201, response.getStatus());
        assertNotNull(store.id);
    }

    @Test
    void testCreateStoreWithIdFails() {
        Store store = new Store();
        store.id = 999999L;
        store.name = "Invalid Store";

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> storeResource.create(store));

        assertEquals(422, exception.getResponse().getStatus());
    }

    @Test
    void testUpdateStore() {
        Store store = new Store();
        store.name = "Store Before Update";
        store.quantityProductsInStock = 10;

        Response createResponse = storeResource.create(store);
        assertEquals(201, createResponse.getStatus());

        Store updatedStore = new Store();
        updatedStore.name = "Store After Update";
        updatedStore.quantityProductsInStock = 20;

        Store result = storeResource.update(store.id, updatedStore);

        assertEquals("Store After Update", result.name);
        assertEquals(20, result.quantityProductsInStock);
    }

    @Test
    void testUpdateStoreWithoutNameFails() {
        Store updatedStore = new Store();
        updatedStore.quantityProductsInStock = 20;

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> storeResource.update(999999L, updatedStore));

        assertEquals(422, exception.getResponse().getStatus());
    }

    @Test
    void testUpdateStoreNotFound() {
        Store updatedStore = new Store();
        updatedStore.name = "Updated Store";
        updatedStore.quantityProductsInStock = 20;

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> storeResource.update(999999L, updatedStore));

        assertEquals(404, exception.getResponse().getStatus());
    }

    @Test
    void testPatchStore() {
        Store store = new Store();
        store.name = "Patch Before";
        store.quantityProductsInStock = 10;

        Response createResponse = storeResource.create(store);
        assertEquals(201, createResponse.getStatus());

        Store updatedStore = new Store();
        updatedStore.name = "Patch After";
        updatedStore.quantityProductsInStock = 25;

        Store result = storeResource.patch(store.id, updatedStore);

        assertEquals("Patch After", result.name);
        assertEquals(25, result.quantityProductsInStock);
    }

    @Test
    void testPatchStoreNotFound() {
        Store updatedStore = new Store();
        updatedStore.name = "Patch Store";
        updatedStore.quantityProductsInStock = 10;

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> storeResource.patch(999999L, updatedStore));

        assertEquals(404, exception.getResponse().getStatus());
    }

    @Test
    void testPatchStoreWithoutNameFails() {
        Store updatedStore = new Store();
        updatedStore.quantityProductsInStock = 10;

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> storeResource.patch(999999L, updatedStore));

        assertEquals(422, exception.getResponse().getStatus());
    }

    @Test
    void testDeleteStore() {
        Store store = new Store();
        store.name = "Store To Delete";
        store.quantityProductsInStock = 5;

        Response createResponse = storeResource.create(store);
        assertEquals(201, createResponse.getStatus());

        Response deleteResponse = storeResource.delete(store.id);

        assertEquals(204, deleteResponse.getStatus());
    }

    @Test
    void testDeleteStoreNotFound() {
        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> storeResource.delete(999999L));

        assertEquals(404, exception.getResponse().getStatus());
    }
}