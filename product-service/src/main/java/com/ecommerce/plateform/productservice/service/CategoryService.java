package com.ecommerce.plateform.productservice.service;

import com.ecommerce.plateform.productservice.dto.CategoryRequest;
import com.ecommerce.plateform.productservice.entity.Category;
import com.ecommerce.plateform.productservice.exception.ResourceNotFoundException;
import com.ecommerce.plateform.productservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category create(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return categoryRepository.save(category);
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    public Category update(Long id, CategoryRequest request) {
        Category category = getById(id);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return categoryRepository.save(category);
    }

    public void delete(Long id) {
        Category category = getById(id);
        categoryRepository.delete(category);
    }
}
