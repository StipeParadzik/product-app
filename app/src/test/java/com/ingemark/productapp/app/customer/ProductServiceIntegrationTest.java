package com.ingemark.productapp.app.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingemark.productapp.app.AbstractIntegrationTest;
import com.ingemark.productapp.app.customer.product.Product;
import com.ingemark.productapp.app.customer.product.ProductDto;
import com.ingemark.productapp.app.customer.product.ProductRepository;

@TestMethodOrder(OrderAnnotation.class)
class ProductServiceIntegrationTest extends AbstractIntegrationTest
{
    private static final String URI = "/api/v1/products";

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Product> persistentProducts;

    @BeforeAll
    void setup()
    {
        persistentProducts = List.of(repository.save(createProduct("PROD000001")),
            repository.save(createProduct("PROD000002")),
            repository.save(createProduct("PROD000003")));
    }

    @Test
    @Order(1)
    void lists() throws Exception
    {
        mockMvc.perform(testHelper.createGetRequest(URI))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @Order(2)
    void reads() throws Exception
    {
        Product product = persistentProducts.get(0);
        Integer id = product.getId();

        mockMvc.perform(testHelper.createGetByIdRequest(URI, id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.code", is(product.getCode())))
            .andExpect(jsonPath("$.name", is(product.getName())))
            .andExpect(jsonPath("$.price_eur",
                is(product.getPriceEur()
                    .doubleValue())))
            .andExpect(jsonPath("$.is_available", is(product.getIsAvailable())));
    }

    @Test
    @Order(3)
    void creates() throws Exception
    {
        ProductDto productDto = new ProductDto();
        productDto.setCode("PROD000004");
        productDto.setName("New Product");
        productDto.setPriceEur(BigDecimal.valueOf(99.99));
        productDto.setIsAvailable(true);

        MvcResult mvcResult = mockMvc.perform(testHelper.createPostRequest(URI, productDto))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(productDto.getCode())))
            .andExpect(jsonPath("$.name", is(productDto.getName())))
            .andExpect(jsonPath("$.price_eur",
                is(productDto.getPriceEur()
                    .doubleValue())))
            .andExpect(jsonPath("$.is_available", is(productDto.getIsAvailable())))
            .andReturn();

        Integer id = objectMapper.readTree(mvcResult.getResponse()
                .getContentAsString())
            .get("id")
            .asInt();
        assertThat(repository.findById(id)).isPresent();
    }

    @Test
    @Order(4)
    void failsToCreateDuplicateCode() throws Exception
    {
        ProductDto productDto = new ProductDto();
        productDto.setCode("PROD000001");
        productDto.setName("Duplicate Product");
        productDto.setPriceEur(BigDecimal.valueOf(49.99));
        productDto.setIsAvailable(true);

        mockMvc.perform(testHelper.createPostRequest(URI, productDto))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class,
                result.getResolvedException()));
    }

    @Test
    @Order(5)
    void failsValidationOnCreate() throws Exception
    {
        ProductDto productDto = new ProductDto();
        productDto.setCode("");
        productDto.setName("Test Product");
        productDto.setPriceEur(BigDecimal.valueOf(99.99));
        productDto.setIsAvailable(true);

        mockMvc.perform(testHelper.createPostRequest(URI, productDto))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.fieldErrors.code").exists());

        productDto.setCode("SHORT");
        mockMvc.perform(testHelper.createPostRequest(URI, productDto))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.fieldErrors.code").exists());
    }

    @Test
    @Order(6)
    void updates() throws Exception
    {
        Integer id = persistentProducts.get(1)
            .getId();
        ProductDto updatedProduct = new ProductDto();
        updatedProduct.setId(id);
        updatedProduct.setCode("PROD000010");
        updatedProduct.setName("Updated Product");
        updatedProduct.setPriceEur(BigDecimal.valueOf(129.99));
        updatedProduct.setIsAvailable(false);

        mockMvc.perform(testHelper.createUpdateRequest(URI, id, updatedProduct))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(updatedProduct.getCode())))
            .andExpect(jsonPath("$.name", is(updatedProduct.getName())))
            .andExpect(jsonPath("$.price_eur",
                is(updatedProduct.getPriceEur()
                    .doubleValue())))
            .andExpect(jsonPath("$.is_available", is(updatedProduct.getIsAvailable())));

        assertThat(repository.findById(id)).isPresent()
            .get()
            .extracting(Product::getCode, Product::getName, Product::getPriceEur, Product::getIsAvailable)
            .containsExactly("PROD000010", "Updated Product", BigDecimal.valueOf(129.99), false);
    }

    @Test
    @Order(7)
    void deletes() throws Exception
    {
        Integer id = persistentProducts.get(2)
            .getId();
        mockMvc.perform(testHelper.createDeleteRequest(URI, id))
            .andExpect(status().isOk());

        assertThat(repository.findById(id)).isEmpty();
    }

    private Product createProduct(String code)
    {
        Product product = new Product();
        product.setCode(code);
        product.setName("Test Product");
        product.setPriceEur(BigDecimal.valueOf(99.99));
        product.setIsAvailable(true);
        return product;
    }
}

