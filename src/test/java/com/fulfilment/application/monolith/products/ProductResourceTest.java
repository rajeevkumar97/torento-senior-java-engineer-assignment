package com.fulfilment.application.monolith.products;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductResourceTest {

  private ProductResource productResource;
  private ProductRepository productRepository;

  @BeforeEach
  void setUp() {
    productResource = new ProductResource();
    productRepository = mock(ProductRepository.class);
    productResource.productRepository = productRepository;
  }

  @Test
  void testGetAllProducts() {
    Product product1 = new Product("BESTÅ");
    Product product2 = new Product("KALLAX");

    when(productRepository.listAll(any())).thenReturn(List.of(product1, product2));

    List<Product> result = productResource.get();

    assertEquals(2, result.size());
    assertEquals("BESTÅ", result.get(0).name);
    assertEquals("KALLAX", result.get(1).name);

    verify(productRepository).listAll(any());
  }

  @Test
  void testGetSingleProduct() {
    Product product = new Product("TONSTAD");
    product.id = 1L;

    when(productRepository.findById(1L)).thenReturn(product);

    Product result = productResource.getSingle(1L);

    assertNotNull(result);
    assertEquals(1L, result.id);
    assertEquals("TONSTAD", result.name);

    verify(productRepository).findById(1L);
  }

  @Test
  void testGetSingleProductNotFound() {
    when(productRepository.findById(999L)).thenReturn(null);

    WebApplicationException exception =
        assertThrows(
            WebApplicationException.class,
            () -> productResource.getSingle(999L));

    assertEquals(404, exception.getResponse().getStatus());

    verify(productRepository).findById(999L);
  }

  @Test
  void testCreateProduct() {
    Product product = new Product("MALM");
    product.stock = 20;

    Response response = productResource.create(product);

    assertEquals(201, response.getStatus());
    assertSame(product, response.getEntity());

    verify(productRepository).persist(product);
  }

  @Test
  void testCreateProductWithIdFails() {
    Product product = new Product("MALM");
    product.id = 10L;

    WebApplicationException exception =
        assertThrows(
            WebApplicationException.class,
            () -> productResource.create(product));

    assertEquals(422, exception.getResponse().getStatus());

    verify(productRepository, never()).persist(any(Product.class));
  }

  @Test
  void testUpdateProduct() {
    Product existing = new Product("OLD-NAME");
    existing.id = 1L;
    existing.stock = 5;

    Product updated = new Product("NEW-NAME");
    updated.description = "Updated product";
    updated.price = new BigDecimal("99.99");
    updated.stock = 20;

    when(productRepository.findById(1L)).thenReturn(existing);

    Product result = productResource.update(1L, updated);

    assertSame(existing, result);
    assertEquals("NEW-NAME", result.name);
    assertEquals("Updated product", result.description);
    assertEquals(new BigDecimal("99.99"), result.price);
    assertEquals(20, result.stock);

    verify(productRepository).findById(1L);
    verify(productRepository).persist(existing);
  }

  @Test
  void testUpdateProductWithoutNameFails() {
    Product product = new Product();
    product.description = "Description";

    WebApplicationException exception =
        assertThrows(
            WebApplicationException.class,
            () -> productResource.update(1L, product));

    assertEquals(422, exception.getResponse().getStatus());

    verify(productRepository, never()).findById(anyLong());
    verify(productRepository, never()).persist(any(Product.class));
  }

  @Test
  void testUpdateProductNotFound() {
    Product product = new Product("NEW-NAME");

    when(productRepository.findById(999L)).thenReturn(null);

    WebApplicationException exception =
        assertThrows(
            WebApplicationException.class,
            () -> productResource.update(999L, product));

    assertEquals(404, exception.getResponse().getStatus());

    verify(productRepository).findById(999L);
    verify(productRepository, never()).persist(any(Product.class));
  }

  @Test
  void testDeleteProduct() {
    Product product = new Product("DELETE-ME");
    product.id = 1L;

    when(productRepository.findById(1L)).thenReturn(product);

    Response response = productResource.delete(1L);

    assertEquals(204, response.getStatus());

    verify(productRepository).findById(1L);
    verify(productRepository).delete(product);
  }

  @Test
  void testDeleteProductNotFound() {
    when(productRepository.findById(999L)).thenReturn(null);

    WebApplicationException exception =
        assertThrows(
            WebApplicationException.class,
            () -> productResource.delete(999L));

    assertEquals(404, exception.getResponse().getStatus());

    verify(productRepository).findById(999L);
    verify(productRepository, never()).delete(any());
  }
}