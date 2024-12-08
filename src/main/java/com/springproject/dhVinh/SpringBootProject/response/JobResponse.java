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
    private Date applicationDeadline;
    private String recruitmentDetails;
    private Long totalValidityPeriod;
    private LocalDate activationDate;
    private EmployerResponse employerResponse;
    private LocalDate createAt;
    private Boolean status;
    private String ranker;
    private Long quantity;
    private String workingForm;
    private String gender;
    @JsonProperty("categoryId")
    private Long categoryId;

    public JobResponse(Long id, String jobName, String experience, String price, Date applicationDeadline, String recruitmentDetails, Long totalValidityPeriod, LocalDate activationDate, EmployerResponse employerResponse, LocalDate createAt, Boolean status, String ranker, Long quantity, String workingForm, String gender, Long categoryId) {
        this.id = id;
        this.jobName = jobName;
        this.experience = experience;
        this.price = price;
        this.applicationDeadline = applicationDeadline;
        this.recruitmentDetails = recruitmentDetails;
        this.totalValidityPeriod = totalValidityPeriod;
        this.activationDate = activationDate;
        this.employerResponse = employerResponse;
        this.createAt = createAt;
        this.status = status;
        this.ranker = ranker;
        this.quantity = quantity;
        this.workingForm = workingForm;
        this.gender = gender;
        this.categoryId = categoryId;
    }

    public JobResponse(Long id, String jobName, String experience, String price, Date applicationDeadline, String recruitmentDetails, Long totalValidityPeriod, LocalDate activationDate, LocalDate createAt, Boolean status, String ranker, Long quantity, String workingForm, String gender, Long categoryId) {
        this.id = id;
        this.jobName = jobName;
        this.experience = experience;
        this.price = price;
        this.applicationDeadline = applicationDeadline;
        this.recruitmentDetails = recruitmentDetails;
        this.totalValidityPeriod = totalValidityPeriod;
        this.activationDate = activationDate;
        this.createAt = createAt;
        this.status = status;
        this.ranker = ranker;
        this.quantity = quantity;
        this.workingForm = workingForm;
        this.gender = gender;
        this.categoryId = categoryId;
    }


}
