package com.springproject.dhVinh.SpringBootProject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springproject.dhVinh.SpringBootProject.exception.CategoryAlreadyExistsException;
import com.springproject.dhVinh.SpringBootProject.exception.PhotoRetrievalException;
import com.springproject.dhVinh.SpringBootProject.model.Category;
import com.springproject.dhVinh.SpringBootProject.response.CategoryResponse;
import com.springproject.dhVinh.SpringBootProject.service.ICategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@AllArgsConstructor
@RequestMapping("/admin/category")
public class CategoryController {

    private final ICategoryService categoryService;

    private final ObjectMapper objectMapper;


    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponse>> getAllCategory() throws SQLException {
        List<Category> categories = categoryService.getCategory();
        List<CategoryResponse> responses = categories.stream()
                .map(category -> {
                    byte[] photoBytes = null;
                    try {
                        Blob photoBlob = category.getImages();
                        if (photoBlob != null) {
                            photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return new CategoryResponse(
                            category.getId(),
                            category.getCategoryName(),
                            category.getDescription(),
                            category.getCreateAt(),
                            photoBytes
                    );
                })
                .collect(Collectors.toList());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(@RequestParam("categoryName") String categoryName,
                                                           @RequestParam("description") String description,
                                                           @RequestPart("images") MultipartFile images) throws SQLException, IOException {
        Category category = categoryService.createCategory(categoryName, description, images);
        CategoryResponse response = new CategoryResponse(
                category.getId(),
                category.getCategoryName(),
                category.getDescription()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteCategory(@PathVariable("categoryId") Long categoryId) {
        categoryService.deleteCategory(categoryId);
    }

    @PutMapping("/update/{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam("categoryName") String categoryName,
            @RequestParam("description") String description,
            @RequestPart("images") MultipartFile images) throws SQLException, IOException {
        Category category = categoryService.updateCategory(categoryId, categoryName, description, images);
        byte[] photoBytes = null;
        try {
            Blob photoBlob = category.getImages();
            if (photoBlob != null) {
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        CategoryResponse response = new CategoryResponse(
                category.getId(),
                category.getCategoryName(),
                category.getDescription(),
                category.getCreateAt(),
                photoBytes
        );
        return ResponseEntity.ok(response);
    }
}