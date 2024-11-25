package com.springproject.dhVinh.SpringBootProject.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class JobResponse {
    private Long id;
    private String jobName;
    private String experience;
    private String price;

    public JobResponse(Long id, String jobName, String experience, String price, Date applicationDeadline, String recruitmentDetails, EmployerResponse employerResponse, Long categoryId, LocalDate createAt, Boolean status) {
        this.id = id;
        this.jobName = jobName;
        this.experience = experience;
        this.price = price;
        this.applicationDeadline = applicationDeadline;
        this.recruitmentDetails = recruitmentDetails;
        this.employerResponse = employerResponse;
        this.categoryId = categoryId;
        this.createAt = createAt;
        this.status = status;
    }

    private Date applicationDeadline;
    private String recruitmentDetails;
    private EmployerResponse employerResponse;
    @JsonProperty("categoryId")
    private Long categoryId;
    private LocalDate createAt;
    private Boolean status;



    public JobResponse(Long id, String jobName, String experience, String price, Date applicationDeadline,
                       String recruitmentDetails, Long categoryId, LocalDate createAt) {
        this.id = id;
        this.jobName = jobName;
        this.experience = experience;
        this.price = price;
        this.applicationDeadline = applicationDeadline;
        this.recruitmentDetails = recruitmentDetails;
        this.categoryId = categoryId;
        this.createAt = createAt;
    }

    public JobResponse(Long id, String jobName, String experience, String price, Date applicationDeadline, String recruitmentDetails, EmployerResponse employerResponse, Long categoryId) {
        this.id = id;
        this.jobName = jobName;
        this.experience = experience;
        this.price = price;
        this.applicationDeadline = applicationDeadline;
        this.recruitmentDetails = recruitmentDetails;
        this.employerResponse = employerResponse;
        this.categoryId = categoryId;
    }

    public JobResponse(Long id, String jobName, String experience, String price, Date applicationDeadline, String recruitmentDetails, EmployerResponse employerResponse, Long categoryId, LocalDate createAt) {
        this.id = id;
        this.jobName = jobName;
        this.experience = experience;
        this.price = price;
        this.applicationDeadline = applicationDeadline;
        this.recruitmentDetails = recruitmentDetails;
        this.employerResponse = employerResponse;
        this.categoryId = categoryId;
        this.createAt = createAt;
    }
}
