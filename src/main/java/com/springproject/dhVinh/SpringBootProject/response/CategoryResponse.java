package com.springproject.dhVinh.SpringBootProject.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
public class CategoryResponse {
    private Long id;
    private String categoryName;
    private String description;

    public CategoryResponse(Long id, String categoryName, String description) {
        this.id = id;
        this.categoryName = categoryName;
        this.description = description;
    }
}
