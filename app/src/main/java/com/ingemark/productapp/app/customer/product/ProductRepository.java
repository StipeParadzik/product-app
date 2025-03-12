package com.ingemark.productapp.app.customer.product;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ingemark.productapp.app.entity.identifiable.IdentifiableEntityRepository;

@Repository
public interface ProductRepository extends IdentifiableEntityRepository<Product, Integer>
{
    Optional<Product> findByCode(String code);
}

