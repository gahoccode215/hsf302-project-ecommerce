package com.hsf302.ecommerce.controller;

import com.hsf302.ecommerce.dto.ApiResponse;
import com.hsf302.ecommerce.dto.request.ProductCreationRequest;
import com.hsf302.ecommerce.dto.request.ProductUpdateRequest;
import com.hsf302.ecommerce.dto.response.ProductPageResponse;
import com.hsf302.ecommerce.dto.response.ProductResponse;
import com.hsf302.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product Controller")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

    ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> createProduct(@RequestBody @Valid ProductCreationRequest request) {
        productService.createProduct(request);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .message("Product created successfully")
                .build();
    }

    @PutMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> updateProduct(
            @PathVariable Long productId,
            @RequestBody @Valid ProductUpdateRequest request) {
        productService.updateProduct(request, productId);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Product updated successfully")
                .build();
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Product deleted successfully")
                .build();
    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ProductResponse> getProduct(@PathVariable Long productId) {
        return ApiResponse.<ProductResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Get product detail successfully")
                .result(productService.getProductById(productId))
                .build();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ProductPageResponse> getProducts(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ApiResponse.<ProductPageResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Get product list successfully")
                .result(productService.getProducts(page, size))
                .build();
    }
}