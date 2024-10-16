package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Job;
import com.springproject.dhVinh.SpringBootProject.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService implements IJobService {

    private final JobRepository jobRepository;

    private final IAdminService adminService;

    @Override
    public void cancelJob(Long jobId) {

    }

    @Override
    public Job addJob(Admin admin , String jobName, String experience, Date applicationDeadline, String recruitmentDetails) {
        Job job = new Job();
        if (admin == null) {
            throw new RuntimeException("Admin not found");
        }
        job.setAdmins(admin);
        job.setJobName(jobName);
        job.setExperience(experience);
        job.setApplicationDeadline(applicationDeadline);
        job.setRecruitmentDetails(recruitmentDetails);
        return jobRepository.save(job);

    }

    @Override
    public List<Job> getAllJobsByEmployerEmail(String email) {
        return List.of();
    }

    @Override
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @Override
    public List<Job> getAllJobsByEmployerId(Long adminId) {
        return jobRepository.findByAdminId(adminId);
    }

}
