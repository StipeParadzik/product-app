package com.ingemark.productapp.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ProductAppApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(ProductAppApplication.class, args);
    }
}
