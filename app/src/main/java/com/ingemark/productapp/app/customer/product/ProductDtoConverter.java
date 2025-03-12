package com.ingemark.productapp.app.customer.product;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.ingemark.productapp.app.util.dto.DtoConverter;

@Component
public class ProductDtoConverter extends DtoConverter<Product, ProductDto>
{
    private final ExchangeRateService exchangeRateService;

    public ProductDtoConverter(ModelMapper modelMapper, ExchangeRateService exchangeRateService)
    {
        super(modelMapper);
        this.exchangeRateService = exchangeRateService;
    }

    @Override
    public ProductDto fromPojo(Product pojo, Class<ProductDto> dtoClass)
    {
        ProductDto dto = super.fromPojo(pojo, dtoClass);
        BigDecimal usdEurExchangeRate = exchangeRateService.getExchangeRate("USD");
        if (usdEurExchangeRate != null && dto.getPriceEur() != null)
        {
            dto.setPriceUsd(usdEurExchangeRate.multiply(dto.getPriceEur())
                .setScale(2, RoundingMode.HALF_UP));
        }
        return dto;
    }
}
