package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StoreErrorMapperTest {

  private StoreResource.ErrorMapper errorMapper;

  @BeforeEach
  void setUp() throws Exception {
    errorMapper = new StoreResource.ErrorMapper();

    var field = StoreResource.ErrorMapper.class.getDeclaredField("objectMapper");
    field.setAccessible(true);
    field.set(errorMapper, new ObjectMapper());
  }

  @Test
  void testWebApplicationExceptionMapping() {
    WebApplicationException exception =
        new WebApplicationException("Store not found", 404);

    Response response = errorMapper.toResponse(exception);

    assertEquals(404, response.getStatus());
    assertNotNull(response.getEntity());
  }

  @Test
  void testGenericExceptionMapping() {
    Exception exception = new RuntimeException("Something went wrong");

    Response response = errorMapper.toResponse(exception);

    assertEquals(500, response.getStatus());
    assertNotNull(response.getEntity());
  }

  @Test
  void testExceptionWithoutMessage() {
    Exception exception = new RuntimeException();

    Response response = errorMapper.toResponse(exception);

    assertEquals(500, response.getStatus());
    assertNotNull(response.getEntity());
  }
}