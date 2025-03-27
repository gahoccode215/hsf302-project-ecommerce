package com.hsf302.ecommerce.service;

import com.hsf302.ecommerce.dto.request.ProductCreationRequest;
import com.hsf302.ecommerce.dto.request.ProductUpdateRequest;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
class ProductServiceImpl implements ProductService{
    ProductRepository productRepository;
    CategoryRepository categoryRepository;

    @Override
    @CacheEvict(value = "productPages", allEntries = true)
    public void createProduct(ProductCreationRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .thumbnail(request.getThumbnail())
                .price(request.getPrice())
                .isDeleted(false)
                .quantity(request.getQuantity())
                .build();
        if(request.getCategoryId() != null){
            Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            product.setCategory(category);
        }
        productRepository.save(product);
    }

    @Override
    @CacheEvict(value = {"products", "productPages"}, allEntries = true)
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        product.setIsDeleted(true);
        productRepository.save(product);
    }

    @Override
    @CacheEvict(value = {"products", "productPages"}, allEntries = true)
    @Transactional
    public void updateProduct(ProductUpdateRequest request, Long id) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
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
    @Cacheable(value = "products")
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product from DB with id: {}", id);
        return mapToProductResponse(productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND)));
    }

    @Override
    @Cacheable(value = "productPages", key = "#page + '-' + #size")
    public ProductPageResponse getProducts(int page, int size) {
        log.info("Fetching getProducts in DB");
        if (page > 0) page -= 1;
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAllByIsDeletedFalse(pageable);

        ProductPageResponse response = new ProductPageResponse();
        List<ProductResponse> productResponses = products.getContent().stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
        response.setContent(productResponses);
        response.setTotalElements(products.getTotalElements());
        response.setTotalPages(products.getTotalPages());
        response.setPageNumber(products.getNumber());
        response.setPageSize(products.getSize());
        return response;
    }
    private ProductResponse mapToProductResponse(Product product){
        ProductResponse productResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .thumbnail(product.getThumbnail())
                .build();
        if(product.getCategory() != null){
            productResponse.setCategoryName(product.getCategory().getName());
        }
        return productResponse;
    }
}
