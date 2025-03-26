package com.hsf302.ecommerce.service;

import com.hsf302.ecommerce.dto.request.CategoryCreationRequest;
import com.hsf302.ecommerce.dto.request.CategoryUpdateRequest;
import com.hsf302.ecommerce.dto.response.CategoryPageResponse;
import com.hsf302.ecommerce.dto.response.CategoryResponse;
import com.hsf302.ecommerce.entity.Category;
import com.hsf302.ecommerce.exception.AppException;
import com.hsf302.ecommerce.exception.ErrorCode;
import com.hsf302.ecommerce.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

public interface CategoryService {
    void createCategory(CategoryCreationRequest request);
    void deleteCategory(Long id);
    void updateCategory(CategoryUpdateRequest request, Long id);
    CategoryResponse getCategoryById(Long id);
    CategoryPageResponse getCategories(int page, int size);
}

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class CategoryServiceImpl implements CategoryService{

    CategoryRepository categoryRepository;

    @Override
    public void createCategory(CategoryCreationRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .thumbnail(request.getThumbnail())
                .build();
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public void updateCategory(CategoryUpdateRequest request, Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        if(request.getName() != null)
            category.setName(request.getName());
        if(request.getThumbnail() != null)
            category.setThumbnail(request.getThumbnail());
        categoryRepository.save(category);
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        return maptoCategoryResponse(categoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND)));
    }

    @Override
    public CategoryPageResponse getCategories(int page, int size) {
        if (page > 0) page -= 1;
        Pageable pageable;
        pageable = PageRequest.of(page, size);
        Page<Category> categories ;
        CategoryPageResponse response = new CategoryPageResponse();
        List<CategoryResponse> categoryResponses = new ArrayList<>();
        categories = categoryRepository.findAll(pageable);
        for (Category category : categories.getContent()) {
            CategoryResponse categoryResponse = maptoCategoryResponse(category);
            categoryResponses.add(categoryResponse);
        }
        response.setContents(categoryResponses);
        response.setTotalElements(categories.getTotalElements());
        response.setTotalPages(categories.getTotalPages());
        response.setPageNumber(categories.getNumber());
        response.setPageSize(categories.getSize());
        return response;
    }

    private CategoryResponse maptoCategoryResponse(Category category){
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .thumbnail(category.getThumbnail())
                .build();

    }
}
