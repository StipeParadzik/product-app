package com.ingemark.productapp.app.customer.product;

import java.math.BigDecimal;

import lombok.Data;

import com.ingemark.codegeneration.GenerateController;
import com.ingemark.codegeneration.GenerateService;
import com.ingemark.productapp.app.entity.identifiable.IdentifiableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
@Data
@GenerateController(resourceBaseName = "products")
@GenerateService
public class Product implements IdentifiableEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false, unique = true, length = 10)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price_eur", nullable = false)
    private BigDecimal priceEur;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;
}
