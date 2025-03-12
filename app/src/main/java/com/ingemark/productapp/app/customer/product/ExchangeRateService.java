package com.ingemark.productapp.app.customer.product;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;

@Service
@Slf4j
public class ExchangeRateService
{
    private final ExchangeRateClient exchangeRateClient;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String EXCHANGE_RATE_CACHE_KEY_PREFIX = "exchangeRate:EUR";
    private static final Duration CACHE_EXPIRATION = Duration.ofHours(12);

    public ExchangeRateService(ExchangeRateClient exchangeRateClient, RedisTemplate<String, String> redisTemplate)
    {
        this.exchangeRateClient = exchangeRateClient;
        this.redisTemplate = redisTemplate;
    }

    public BigDecimal getExchangeRate(@NotNull String isoCode)
    {
        if (isoCode == null || isoCode.isEmpty())
        {
            return BigDecimal.ZERO;
        }
        String cacheKey = EXCHANGE_RATE_CACHE_KEY_PREFIX + isoCode.toUpperCase();
        String cachedRate = redisTemplate.opsForValue()
            .get(cacheKey);
        if (cachedRate != null)
        {
            return new BigDecimal(cachedRate);
        }

        List<ExchangeRateResponse> response = exchangeRateClient.getExchangeRate(isoCode.toUpperCase());

        if (response == null)
        {
            response = List.of();
        }

        BigDecimal exchangeRate = response.stream()
            .filter(exchangeRateResponse -> isoCode.equalsIgnoreCase(exchangeRateResponse.getCurrency()))
            .map(ExchangeRateResponse::getExchangeRate)
            .filter(Objects::nonNull)
            .map(value -> value.replace(",", "."))
            .map(BigDecimal::new)
            .findFirst()
            .orElse(BigDecimal.ZERO);

        if (exchangeRate.compareTo(BigDecimal.ZERO) > 0)
        {
            redisTemplate.opsForValue()
                .set(cacheKey, exchangeRate.toString(), CACHE_EXPIRATION);
        }
        else
        {
            redisTemplate.opsForValue()
                .set(cacheKey, exchangeRate.toString(), Duration.ofMinutes(10));
            log.warn("Cached temporary '0.00' exchange rate for {} (valid for 10min)", isoCode);
        }

        return exchangeRate;
    }
}




