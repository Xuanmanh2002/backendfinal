package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Category;

import java.util.List;

public interface ICategoryService {
    List<Category> getCategory();

    Category createCategory(String categoryName, String description);

    void deleteCategory(Long categoryId);

    Category findByCategoryName(String categoryName);

    Category updateCategory(Long categoryId, String categoryName, String description );

}
