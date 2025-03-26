package com.hsf302.ecommerce.controller;

import com.hsf302.ecommerce.dto.ApiResponse;
import com.hsf302.ecommerce.dto.request.CategoryCreationRequest;
import com.hsf302.ecommerce.dto.request.CategoryUpdateRequest;
import com.hsf302.ecommerce.dto.response.CategoryPageResponse;
import com.hsf302.ecommerce.dto.response.CategoryResponse;
import com.hsf302.ecommerce.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Category Controller")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    CategoryService categoryService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> createCategory(@RequestBody @Valid CategoryCreationRequest request) {
        categoryService.createCategory(request);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .message("Category created successfully")
                .build();
    }

    @PutMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> updateCategory(@RequestBody @Valid CategoryUpdateRequest request, @PathVariable Long categoryId) {
        categoryService.updateCategory(request, categoryId);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Category updated successfully")
                .build();
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Category deleted successfully")
                .build();
    }

    @GetMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<CategoryResponse> getCategory(@PathVariable Long categoryId) {
        return ApiResponse.<CategoryResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Get category detail successfully")
                .result(categoryService.getCategoryById(categoryId))
                .build();
    }

    @GetMapping
    public ApiResponse<CategoryPageResponse> getAllCategories(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ApiResponse.<CategoryPageResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Get category list successfully")
                .result(categoryService.getCategories(page, size))
                .build();
    }
}