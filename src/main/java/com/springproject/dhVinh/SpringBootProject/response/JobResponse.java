package com.springproject.dhVinh.SpringBootProject.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobResponse {
    private String jobName;
    private String experience;
    private Date applicationDeadline;
    private String recruitmentDetails;

    private AdminResponse adminResponse;

}
