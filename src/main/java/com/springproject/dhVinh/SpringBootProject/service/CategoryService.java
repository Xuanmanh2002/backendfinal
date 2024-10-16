package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.exception.CategoryAlreadyExistsException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Category;
import com.springproject.dhVinh.SpringBootProject.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService{

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public Category createCategory(String categoryName, String description) {
        if (categoryRepository.existsByCategoryName(categoryName)) {
            throw new CategoryAlreadyExistsException(categoryName + " category already exists");
        }
        Category category = new Category();
        category.setCategoryName(categoryName);
        category.setDescription(description);
        LocalDate createdDate = LocalDate.now();
        category.setCreateAt(createdDate);
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isPresent()) {
            categoryRepository.deleteById(categoryId);
        } else {
            throw new CategoryAlreadyExistsException("Category with ID " + categoryId + " not found.");
        }
    }

    @Override
    public Category findByCategoryName(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName).get();
    }

    @Override
    public Category updateCategory(Long categoryId, String categoryName, String description) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            category.setCategoryName(categoryName);
            category.setDescription(description);
            return categoryRepository.save(category);
        } else {
            throw new CategoryAlreadyExistsException("Category with ID " + categoryId + " not found.");
        }
    }
}
