package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.exception.AdminAlreadyExistsException;
import com.springproject.dhVinh.SpringBootProject.exception.JobAlreadyExistException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Category;
import com.springproject.dhVinh.SpringBootProject.model.Job;
import com.springproject.dhVinh.SpringBootProject.model.Order;
import com.springproject.dhVinh.SpringBootProject.repository.AdminRepository;
import com.springproject.dhVinh.SpringBootProject.repository.CategoryRepository;
import com.springproject.dhVinh.SpringBootProject.repository.JobRepository;
import com.springproject.dhVinh.SpringBootProject.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobService implements IJobService {

    private final JobRepository jobRepository;

    private final IAdminService adminService;

    private final CategoryRepository categoryRepository;

    private final AdminRepository adminRepository;

    private final OrderRepository orderRepository;

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
    public Job addJob(Admin admin, String jobName, String experience, String price, Date applicationDeadline, String recruitmentDetails, Long categoryId, String ranker, Long quantity, String workForm, String gender) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin cannot be null.");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category with ID " + categoryId + " not found"));

        Job job = new Job();
        job.setAdmins(admin);
        job.setJobName(jobName);
        job.setExperience(experience);
        job.setPrice(price);
        job.setApplicationDeadline(applicationDeadline);
        job.setRecruitmentDetails(recruitmentDetails);
        job.setCreateAt(LocalDate.now());
        job.setCategories(category);
        job.setRanker(ranker);
        job.setQuantity(quantity);
        job.setWorkingForm(workForm);
        job.setGender(gender);
        boolean hasOrder = orderRepository.existsByAdminId(admin.getId());
        if (hasOrder) {
            Order order = orderRepository.findByAdminId(admin.getId())
                    .orElseThrow(() -> new RuntimeException("Order not found for admin ID " + admin.getId()));
            job.setStatus(true);
            job.setActivationDate(LocalDate.now());
            job.setTotalValidityPeriod(order.getTotalValidityPeriod());
        } else {
            job.setStatus(false);
            job.setActivationDate(null);
            job.setTotalValidityPeriod(null);
        }
        LocalDate today = LocalDate.now();
        if (Boolean.TRUE.equals(job.getStatus()) && applicationDeadline.toLocalDate().isBefore(today)) {
            job.setStatus(false);
        }

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
    public Job updateJob(Long jobId, Admin admin, String jobName, String experience, String price, Date applicationDeadline, String recruitmentDetails, Long categoryId, String ranker, Long quantity, String workForm, String gender) {
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
        job.setCategories(optionalCategory.get());
        job.setRanker(ranker);
        job.setQuantity(quantity);
        job.setWorkingForm(workForm);
        job.setGender(gender);
        job.setCreateAt(LocalDate.now());
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
        List<String> rank = Arrays.asList("silver", "gold", "diamond");
        return jobRepository.findJobsByAdminRank(rank);
    }

    @Override
    public List<Job> getAllJobStatusTrueByEmployer(Long adminId) {
        return jobRepository.findByAdminIdAndStatusTrue(adminId);
    }

    @Override
    public Job getJobId(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new JobAlreadyExistException("Job with id " + jobId + " not found"));
    }
}
