package com.hsf302.ecommerce.service;

import com.hsf302.ecommerce.dto.request.ProductCreationRequest;
import com.hsf302.ecommerce.dto.request.ProductUpdateRequest;
import com.hsf302.ecommerce.dto.response.CategoryResponse;
import com.hsf302.ecommerce.dto.response.ProductPageResponse;
import com.hsf302.ecommerce.dto.response.ProductResponse;
import com.hsf302.ecommerce.entity.Category;
import com.hsf302.ecommerce.entity.Product;
import com.hsf302.ecommerce.exception.AppException;
import com.hsf302.ecommerce.exception.ErrorCode;
import com.hsf302.ecommerce.repository.CategoryRepository;
import com.hsf302.ecommerce.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public interface ProductService {
    void createProduct(ProductCreationRequest request);
    void deleteProduct(Long id);
    void updateProduct(ProductUpdateRequest request, Long id);
    ProductResponse getProductById(Long id);
    ProductPageResponse getProducts(int page, int size);
}
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
class ProductServiceImpl implements ProductService{
    ProductRepository productRepository;
    CategoryRepository categoryRepository;

    @Override
    public void createProduct(ProductCreationRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .thumbnail(request.getThumbnail())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .category(category)
                .build();
        productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public void updateProduct(ProductUpdateRequest request, Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        if(request.getName() != null)
            product.setName(request.getName());
        if(request.getDescription() != null)
            product.setDescription(request.getDescription());
        if(request.getPrice() != null)
            product.setPrice(product.getPrice());
        if(request.getQuantity() != null)
            product.setQuantity(product.getQuantity());
        if(request.getThumbnail() != null)
            product.setThumbnail(product.getThumbnail());
        if(request.getCategoryId() != null)
            product.setCategory(categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND)));
        productRepository.save(product);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        return mapToProductResponse(productRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND)));
    }

    @Override
    public ProductPageResponse getProducts(int page, int size) {
        if (page > 0) page -= 1;
        Pageable pageable;
        pageable = PageRequest.of(page, size);
        Page<Product> products;
        ProductPageResponse response = new ProductPageResponse();
        List<ProductResponse> productResponses = new ArrayList<>();
        products = productRepository.findAll(pageable);
        for (Product product : products.getContent()) {
            ProductResponse productResponse = mapToProductResponse(product);
            productResponses.add(productResponse);
        }
        response.setContent(productResponses);
        response.setTotalElements(products.getTotalElements());
        response.setTotalPages(products.getTotalPages());
        response.setPageNumber(products.getNumber());
        response.setPageSize(products.getSize());
        return response;
    }
    private ProductResponse mapToProductResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .categoryName(product.getCategory().getName())
                .thumbnail(product.getThumbnail())
                .build();
    }
}
