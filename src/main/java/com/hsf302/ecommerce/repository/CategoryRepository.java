package com.hsf302.ecommerce.repository;

import com.hsf302.ecommerce.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByIdAndIsDeletedFalse(Long id);
    Page<Category> findAllByIsDeletedFalse(Pageable pageable);
}
