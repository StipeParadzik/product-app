package com.ingemark.productapp.app.customer.product;

import java.math.BigDecimal;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ingemark.productapp.app.customer.product.validation.UniqueProductCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@UniqueProductCode
public class ProductDto
{
    private Integer id;

    @NotBlank
    @Size(min = 10, max = 10, message = "Code must be exactly 10 characters long")
    private String code;

    @NotBlank
    private String name;

    @NotNull
    @JsonProperty("price_eur")
    private BigDecimal priceEur;

    @JsonProperty("price_usd")
    private BigDecimal priceUsd;

    @NotNull
    @JsonProperty("is_available")
    private Boolean isAvailable;
}
