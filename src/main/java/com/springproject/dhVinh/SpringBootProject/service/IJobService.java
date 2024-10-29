package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Job;

import java.sql.Date;
import java.util.List;

public interface IJobService {

    void cancelJob(Long jobId);

    Job addJob(Admin admin , String jobName, String experience, Date applicationDeadline, String recruitmentDetails, Long categoryId);

    List<Job> getAllJobs();

    List<Job> getAllJobsByEmployerId(Long adminId);

}
