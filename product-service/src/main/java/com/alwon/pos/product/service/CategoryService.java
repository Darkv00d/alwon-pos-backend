package com.alwon.pos.product.service;

import com.alwon.pos.product.dto.CategoryDto;
import com.alwon.pos.product.dto.CreateCategoryRequest;
import com.alwon.pos.product.model.Category;
import com.alwon.pos.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        log.debug("Fetching all categories");
        return categoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getActiveCategories() {
        log.debug("Fetching active categories");
        return categoryRepository.findByActiveTrueOrderByDisplayOrderAsc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        log.debug("Fetching category by ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        return convertToDto(category);
    }

    @Transactional
    public CategoryDto createCategory(CreateCategoryRequest request) {
        log.info("Creating new category: {}", request.getName());

        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateResourceException("Category with name " + request.getName() + " already exists");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setActive(request.getActive());

        Category saved = categoryRepository.save(category);
        log.info("Category created with ID: {}", saved.getId());
        return convertToDto(saved);
    }

    @Transactional
    public CategoryDto updateCategory(Long id, CreateCategoryRequest request) {
        log.info("Updating category ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        if (request.getName() != null)
            category.setName(request.getName());
        if (request.getDescription() != null)
            category.setDescription(request.getDescription());
        if (request.getActive() != null)
            category.setActive(request.getActive());

        Category updated = categoryRepository.save(category);
        log.info("Category updated successfully");
        return convertToDto(updated);
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.info("Deleting category ID: {}", id);
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with ID: " + id);
        }
        categoryRepository.deleteById(id);
        log.info("Category deleted successfully");
    }

    private CategoryDto convertToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setActive(category.getActive());
        dto.setProductCount(category.getProducts() != null ? category.getProducts().size() : 0);
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }
}
