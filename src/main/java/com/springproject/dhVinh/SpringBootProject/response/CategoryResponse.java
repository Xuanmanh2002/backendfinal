package com.springproject.dhVinh.SpringBootProject.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;

import java.time.LocalDate;
@Data
@NoArgsConstructor
public class CategoryResponse {
    private Long id;
    private String categoryName;
    private String description;
    private LocalDate createAt;
    private String images;

    public CategoryResponse(Long id, String categoryName, String description) {
        this.id = id;
        this.categoryName = categoryName;
        this.description = description;
    }

    public CategoryResponse(Long id, String categoryName, String description, LocalDate createAt, byte[] photoBytes) {
        this.id = id;
        this.categoryName = categoryName;
        this.description = description;
        this.createAt = createAt;
        this.images = photoBytes != null ? Base64.encodeBase64String(photoBytes) : null;
    }
}
