package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.exception.AdminAlreadyExistsException;
import com.springproject.dhVinh.SpringBootProject.exception.JobAlreadyExistException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Category;
import com.springproject.dhVinh.SpringBootProject.model.Job;
import com.springproject.dhVinh.SpringBootProject.repository.AdminRepository;
import com.springproject.dhVinh.SpringBootProject.repository.CategoryRepository;
import com.springproject.dhVinh.SpringBootProject.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobService implements IJobService {

    private final JobRepository jobRepository;

    private final IAdminService adminService;

    private final CategoryRepository categoryRepository;
    private final AdminRepository adminRepository;

    @Override
    public void cancelJob(Long jobId) {
        Optional<Job> job = jobRepository.findById(jobId);
        if (job.isPresent()) {
            jobRepository.deleteById(jobId);
        } else {
            throw new JobAlreadyExistException("Job with ID " + jobId + " not found.");
        }
    }

    @Override
    public Job addJob(Admin admin, String jobName, String experience, String price, Date applicationDeadline, String recruitmentDetails, Long categoryId) {
        Job job = new Job();
        if (admin == null) {
            throw new RuntimeException("Admin not found");
        }
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (!optionalCategory.isPresent()) {
            throw new RuntimeException("Category not found");
        }
        Category category = optionalCategory.get();
        job.setAdmins(admin);
        job.setJobName(jobName);
        job.setExperience(experience);
        job.setPrice(price);
        job.setApplicationDeadline(applicationDeadline);
        job.setRecruitmentDetails(recruitmentDetails);
        job.setStatus(false);
        LocalDate localDate = LocalDate.now();
        job.setCreateAt(localDate);

        job.setCategories(category);

        return jobRepository.save(job);
    }

    @Override
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @Override
    public List<Job> getAllJobByStatusTrue() {
            return jobRepository.findByStatusTrue();
    }

    @Override
    public List<Job> getAllJobsByEmployerId(Long adminId) {
        return jobRepository.findByAdminId(adminId);
    }

    @Override
    public Job updateJob(Long jobId, Admin admin, String jobName, String experience, String price, Date applicationDeadline, String recruitmentDetails, Long categoryId) {
        if (admin == null) {
            throw new AdminAlreadyExistsException("Admin not found");
        }

        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);

        if (!optionalCategory.isPresent()) {
            throw new RuntimeException("Category not found");
        }
        Optional<Job> optionalJob = jobRepository.findById(jobId);
        Job job = optionalJob.get();
        job.setAdmins(admin);
        job.setJobName(jobName);
        job.setExperience(experience);
        job.setPrice(price);
        job.setApplicationDeadline(applicationDeadline);
        job.setRecruitmentDetails(recruitmentDetails);
        job.setStatus(false);
        job.setCreateAt(LocalDate.now());
        job.setCategories(optionalCategory.get());
        return jobRepository.save(job);
    }

    @Override
    public Job updateActive(Long jobId, boolean status) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));
        job.setStatus(status);
        return jobRepository.save(job);
    }

    @Override
    public List<Job> getAllJobsWithEmployerGold() {
        return jobRepository.findJobsByAdminSalaryRange("gold");
    }
}
