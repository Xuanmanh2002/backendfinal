package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Category;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface ICategoryService {
    List<Category> getCategory();

    Category createCategory(String categoryName, String description, MultipartFile images) throws SQLException, IOException;

    void deleteCategory(Long categoryId);

    Category findByCategoryName(String categoryName);

    Category updateCategory(Long categoryId, String categoryName, String description, MultipartFile images) throws SQLException, IOException;

}
