package com.springproject.dhVinh.SpringBootProject.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApplicationDocumentsResponse {
    private String fullName;
    private String email;
    private String telephone;
    private String letter;
}
