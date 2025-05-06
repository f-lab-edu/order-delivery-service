package com.junghyun.product.service;

import com.junghyun.product.dto.ProductRequestDto;
import com.junghyun.product.entity.Product;
import com.junghyun.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import com.junghyun.product.document.ProductDocument;
import com.junghyun.product.repository.ProductSearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;

    @CachePut(value = "product", key = "#result.id")
    public Product createProduct(ProductRequestDto dto) {
        Product product = Product.builder()
                .name(dto.name())
                .description(dto.description())
                .price(dto.price())
                .category(dto.category())
                .status(dto.status())
                .build();

        // 상품을 DB에 저장
        Product savedProduct = productRepository.save(product);

        // 상품을 Elasticsearch에 저장 (DB 저장 성공한 후)
        if(savedProduct != null) {
            ProductDocument productDocument = ProductDocument.builder()
                    .id(savedProduct.getId())
                    .name(savedProduct.getName())
                    .description(savedProduct.getDescription())
                    .price(savedProduct.getPrice())
                    .category(savedProduct.getCategory())
                    .status(savedProduct.getStatus().name())
                    .build();

            productSearchRepository.save(productDocument);
        }

        return savedProduct;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Cacheable(value = "product", key = "#id")
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 상품이 존재하지 않습니다: " + id));
    }

    @Cacheable(value = "product", key = "#category")
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public Page<ProductDocument> searchProducts(String keyword, Pageable pageable) {
        return productSearchRepository.findByNameContainingOrDescriptionContaining(keyword, keyword, pageable);
    }
}
