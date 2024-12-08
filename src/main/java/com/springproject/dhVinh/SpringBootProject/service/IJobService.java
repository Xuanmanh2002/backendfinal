package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Job;

import java.sql.Date;
import java.util.List;

public interface IJobService {

    void cancelJob(Long jobId);

    Job addJob(Admin admin , String jobName, String experience, String price, Date applicationDeadline, String recruitmentDetails, Long categoryId, String ranker, Long quantity, String workForm, String gender);

    List<Job> getAllJobs();

    List<Job> getAllJobByStatusTrue();

    List<Job> getAllJobsByEmployerId(Long adminId);

    Job updateJob(Long jobId, Admin admin, String jobName, String experience, String price, Date applicationDeadline, String recruitmentDetails, Long categoryId, String ranker, Long quantity, String workForm, String gender);

    Job updateActive(Long jobId, boolean status);

    List<Job> getAllJobsWithEmployerGold();

    List<Job> getAllJobStatusTrueByEmployer(Long adminId);

    Job getJobId(Long jobId);
}
