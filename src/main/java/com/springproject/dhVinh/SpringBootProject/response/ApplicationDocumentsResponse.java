package com.springproject.dhVinh.SpringBootProject.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;

import java.time.LocalDate;

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
    private LocalDate createAt;
    private CustomerResponse customerResponse;
    private Long jobId;

    public ApplicationDocumentsResponse(Long id, String fullName, String email, String telephone, byte[] photoBytes, String letter, String status, LocalDate createAt, CustomerResponse customerResponse, Long jobId) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.telephone = telephone;
        this.cv = photoBytes != null ? Base64.encodeBase64String(photoBytes) : null;
        this.letter = letter;
        this.status = status;
        this.createAt = createAt;
        this.customerResponse = customerResponse;
        this.jobId = jobId;
    }

    public ApplicationDocumentsResponse(Long id, String fullName, String email, String telephone, String letter, Long jobI) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.telephone = telephone;
        this.letter = letter;
        this.jobId = jobId;
    }
}
