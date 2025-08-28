package com.library.management.mapper;

import com.library.management.dto.request.CategoryCreateRequest;
import com.library.management.dto.request.CategoryUpdateRequest;
import com.library.management.dto.response.CategoryResponse;
import com.library.management.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {
    
    public Category toEntity(CategoryCreateRequest request) {
        if (request == null) {
            return null;
        }
        
        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }
    
    public CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }
        
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .bookCount(category.getBooks() != null ? category.getBooks().size() : 0)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
    
    public CategoryResponse toResponseWithoutBooks(Category category) {
        if (category == null) {
            return null;
        }
        
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .bookCount(category.getBooks() != null ? category.getBooks().size() : 0)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
    
    public void updateEntity(Category category, CategoryUpdateRequest request) {
        if (category == null || request == null) {
            return;
        }
        
        if (request.getName() != null) {
            category.setName(request.getName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
    }
    
    public List<CategoryResponse> toResponseList(List<Category> categories) {
        if (categories == null) {
            return null;
        }
        
        return categories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}

