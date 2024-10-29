package com.springproject.dhVinh.SpringBootProject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springproject.dhVinh.SpringBootProject.exception.CategoryAlreadyExistsException;
import com.springproject.dhVinh.SpringBootProject.model.Category;
import com.springproject.dhVinh.SpringBootProject.response.CategoryResponse;
import com.springproject.dhVinh.SpringBootProject.service.ICategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@AllArgsConstructor
@RequestMapping("/admin/category")
public class CategoryController {

    private final ICategoryService categoryService;

    private final ObjectMapper objectMapper;


    @GetMapping("/all")
    public ResponseEntity<List<Category>> getAllCategory(){
        List<Category> categories = categoryService.getCategory();
        if(categories.isEmpty()){
            return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> createCategory(@RequestBody CategoryResponse response) {
        Map<String, Object> result = new HashMap<>();
        try {
            Category category = categoryService.createCategory(response.getCategoryName(), response.getDescription());
            result.put("status", "success");
            result.put("message", "Category created successfully");
            result.put("category", category);
            return ResponseEntity.ok(result);
        } catch (CategoryAlreadyExistsException cae) {
            result.put("status", "error");
            result.put("message", cae.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }
    }

    @DeleteMapping("/delete/{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteCategory(@PathVariable("categoryId") Long categoryId){
        categoryService.deleteCategory(categoryId);
    }

    @PutMapping("/update/{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> updateCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestBody CategoryResponse response) {

        Map<String, Object> result = new HashMap<>();
        try {
            Category updatedCategory = categoryService.updateCategory(categoryId, response.getCategoryName(), response.getDescription());
            result.put("status", "success");
            result.put("message", "Category updated successfully");
            result.put("category", updatedCategory);
            return ResponseEntity.ok(result);
        } catch (CategoryAlreadyExistsException ex) {
            result.put("status", "error");
            result.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }
}
