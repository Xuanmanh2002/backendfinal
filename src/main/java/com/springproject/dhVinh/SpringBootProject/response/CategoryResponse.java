package com.springproject.dhVinh.SpringBootProject.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
public class CategoryResponse {
    private String categoryName;
    private String description;

    public CategoryResponse(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
    }
}
