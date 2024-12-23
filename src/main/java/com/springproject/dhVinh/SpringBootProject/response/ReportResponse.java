package com.springproject.dhVinh.SpringBootProject.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ReportResponse {
    private Long id;
    private String fullName;
    private String email;
    private String telephone;
    private LocalDate createAt;
    private String letter;
    private String address;
    private Boolean status;
    private CustomerResponse customerResponse;
    private Long jobId;

    public ReportResponse(String fullName, String email, String telephone, String letter, String address, CustomerResponse customerResponse, Long jobId) {
        this.fullName = fullName;
        this.email = email;
        this.telephone = telephone;
        this.letter = letter;
        this.address = address;
        this.customerResponse = customerResponse;
        this.jobId = jobId;
    }

    public ReportResponse(Long id, String fullName, String email, String telephone, LocalDate createAt, String letter, String address, Boolean status, CustomerResponse customerResponse, Long jobId) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.telephone = telephone;
        this.createAt = createAt;
        this.letter = letter;
        this.address = address;
        this.status = status;
        this.customerResponse = customerResponse;
        this.jobId = jobId;
    }
}
