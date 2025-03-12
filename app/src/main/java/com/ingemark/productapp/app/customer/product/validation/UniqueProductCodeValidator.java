package com.ingemark.productapp.app.customer.product.validation;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.ingemark.productapp.app.customer.product.Product;
import com.ingemark.productapp.app.customer.product.ProductDto;
import com.ingemark.productapp.app.customer.product.ProductRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class UniqueProductCodeValidator implements ConstraintValidator<UniqueProductCode, Object>
{
    private final ProductRepository productRepository;

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context)
    {
        if (!(value instanceof ProductDto productDto))
        {
            return false;
        }

        if (productDto.getCode() == null ||
            productDto.getCode()
                .isBlank())
        {
            return false;
        }

        Optional<Product> existingProduct = productRepository.findByCode(productDto.getCode());

        return existingProduct.map(product -> productDto.getId() != null &&
                productDto.getId()
                    .equals(product.getId()))
            .orElse(true);
    }
}

