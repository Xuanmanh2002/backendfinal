package com.springproject.dhVinh.SpringBootProject.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleResponse {
    private String name;
    private String description;

    public RoleResponse(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
