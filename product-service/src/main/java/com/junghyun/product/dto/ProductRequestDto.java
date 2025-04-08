package com.junghyun.product.dto;

import com.junghyun.product.entity.Product;

public record ProductRequestDto(
        String name,
        String description,
        Double price,
        String category,
        Product.Status status
) {}
