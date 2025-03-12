package com.ingemark.productapp.app.customer.product;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class ExchangeRateResponse
{
    @JsonProperty("srednji_tecaj")
    private String exchangeRate;

    @JsonProperty("valuta")
    private String currency;
}

