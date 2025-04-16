package com.junghyun.product.repository;

import com.junghyun.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // category로 상품 조회
    List<Product> findByCategory(String category);
}
