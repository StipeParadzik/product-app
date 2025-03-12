package com.ingemark.productapp.app.customer.product;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "exchangeRateClient", url = "https://api.hnb.hr")
public interface ExchangeRateClient
{
    @GetMapping("/tecajn-eur/v3?valuta={currency}")
    List<ExchangeRateResponse> getExchangeRate(@PathVariable("currency") String currency);
}

