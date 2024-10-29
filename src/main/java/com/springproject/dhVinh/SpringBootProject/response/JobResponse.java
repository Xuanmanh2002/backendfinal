package com.springproject.dhVinh.SpringBootProject.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springproject.dhVinh.SpringBootProject.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
public class JobResponse {
    private Long id;
    private String jobName;
    private String experience;
    private Date applicationDeadline;
    private String recruitmentDetails;
    private AdminResponse adminResponse;
    @JsonProperty("categoryId")
    private Long categoryId;

    public JobResponse(Long id, String jobName, String experience, Date applicationDeadline, String recruitmentDetails, Long categoryId) {
        this.id = id;
        this.jobName = jobName;
        this.experience = experience;
        this.applicationDeadline = applicationDeadline;
        this.recruitmentDetails = recruitmentDetails;
        this.categoryId = categoryId;
    }
    public JobResponse(Long id, String jobName, String experience, Date applicationDeadline, String recruitmentDetails, AdminResponse adminResponse, Long categoryId) {
        this.id = id;
        this.jobName = jobName;
        this.experience = experience;
        this.applicationDeadline = applicationDeadline;
        this.recruitmentDetails = recruitmentDetails;
        this.adminResponse = adminResponse;
        this.categoryId = categoryId;
    }
}
