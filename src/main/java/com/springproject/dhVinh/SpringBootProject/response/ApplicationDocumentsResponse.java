package com.springproject.dhVinh.SpringBootProject.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApplicationDocumentsResponse {
    private Long id;
    private String fullName;
    private String email;
    private String telephone;
    private String cv;
    private String letter;
    private String status;
    private CustomerResponse customerResponse;
    private Long jobId;

    public ApplicationDocumentsResponse(Long id, String fullName, String email, String telephone, String letter, Long jobI) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.telephone = telephone;
        this.letter = letter;
        this.jobId = jobId;
    }
}
