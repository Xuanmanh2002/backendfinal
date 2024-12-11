package com.springproject.dhVinh.SpringBootProject.controller;

import com.springproject.dhVinh.SpringBootProject.exception.AdminAlreadyExistsException;
import com.springproject.dhVinh.SpringBootProject.exception.JobAlreadyExistException;
import com.springproject.dhVinh.SpringBootProject.exception.PhotoRetrievalException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Job;
import com.springproject.dhVinh.SpringBootProject.repository.JobRepository;
import com.springproject.dhVinh.SpringBootProject.response.AdminResponse;
import com.springproject.dhVinh.SpringBootProject.response.EmployerResponse;
import com.springproject.dhVinh.SpringBootProject.response.JobResponse;
import com.springproject.dhVinh.SpringBootProject.service.IAdminService;
import com.springproject.dhVinh.SpringBootProject.service.IJobService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/employer/job")
@RequiredArgsConstructor
public class JobController {

    private final IAdminService adminService;

    private final IJobService jobService;

    private final JobRepository jobRepository;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> saveJob(
            @RequestBody JobResponse jobResponse,
            Principal principal) {
        Map<String, Object> jsonResponse = new HashMap<>();
        try {
            String email = principal.getName();
            Admin admin = adminService.getAdmin(email);

            if (admin == null) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Admin not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(jsonResponse);
            }
            Job job = jobService.addJob(
                    admin,
                    jobResponse.getJobName(),
                    jobResponse.getExperience(),
                    jobResponse.getPrice(),
                    jobResponse.getApplicationDeadline(),
                    jobResponse.getRecruitmentDetails(),
                    jobResponse.getCategoryId(),
                    jobResponse.getRanker(),
                    jobResponse.getQuantity(),
                    jobResponse.getWorkingForm(),
                    jobResponse.getGender()
            );
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Job created successfully");
            jsonResponse.put("job", job);

            return ResponseEntity.ok(jsonResponse);

        } catch (IllegalArgumentException e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Invalid input: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonResponse);

        } catch (Exception e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Internal error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonResponse);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<JobResponse>> getAllJob() {
        List<Job> jobs = jobService.getAllJobs();
        if (jobs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        List<JobResponse> jobResponses = jobs.stream().map(job -> {
            EmployerResponse employerResponse = getEmployerResponse(job.getAdmins());
            return new JobResponse(
                    job.getId(),
                    job.getJobName(),
                    job.getExperience(),
                    job.getPrice(),
                    job.getApplicationDeadline(),
                    job.getRecruitmentDetails(),
                    job.getTotalValidityPeriod(),
                    job.getActivationDate(),
                    employerResponse,
                    job.getCreateAt(),
                    job.getStatus(),
                    job.getRanker(),
                    job.getQuantity(),
                    job.getWorkingForm(),
                    job.getGender(),
                    job.getCategories().getId()
            );
        }).collect(Collectors.toList());
        return ResponseEntity.ok(jobResponses);
    }


    @GetMapping("/all-job/{adminId}/active")
    public ResponseEntity<List<JobResponse>> getJobsByAdminAndStatusTrue(@PathVariable Long adminId) {
        List<Job> jobs = jobService.getAllJobStatusTrueByEmployer(adminId);
        List<JobResponse> jobResponses = jobs.stream().map(job -> {
            EmployerResponse employerResponse = getEmployerResponse(job.getAdmins());
            return new JobResponse(
                    job.getId(),
                    job.getJobName(),
                    job.getExperience(),
                    job.getPrice(),
                    job.getApplicationDeadline(),
                    job.getRecruitmentDetails(),
                    job.getTotalValidityPeriod(),
                    job.getActivationDate(),
                    employerResponse,
                    job.getCreateAt(),
                    job.getStatus(),
                    job.getRanker(),
                    job.getQuantity(),
                    job.getWorkingForm(),
                    job.getGender(),
                    job.getCategories().getId()
            );
        }).collect(Collectors.toList());
        return ResponseEntity.ok(jobResponses);
    }

    @GetMapping("/active")
    public ResponseEntity<List<JobResponse>> getAllActiveJobs() {
        List<Job> jobs = jobService.getAllJobByStatusTrue();
        List<JobResponse> jobResponses = jobs.stream().map(job -> {
            EmployerResponse employerResponse = getEmployerResponse(job.getAdmins());
            return new JobResponse(
                    job.getId(),
                    job.getJobName(),
                    job.getExperience(),
                    job.getPrice(),
                    job.getApplicationDeadline(),
                    job.getRecruitmentDetails(),
                    job.getTotalValidityPeriod(),
                    job.getActivationDate(),
                    employerResponse,
                    job.getCreateAt(),
                    job.getStatus(),
                    job.getRanker(),
                    job.getQuantity(),
                    job.getWorkingForm(),
                    job.getGender(),
                    job.getCategories().getId()
            );
        }).collect(Collectors.toList());
        return ResponseEntity.ok(jobResponses);
    }

    private EmployerResponse getEmployerResponse(Admin admin) {
        EmployerResponse employerResponse = new EmployerResponse();
        employerResponse.setId(admin.getId());
        employerResponse.setEmail(admin.getEmail());
        employerResponse.setFirstName(admin.getFirstName());
        employerResponse.setLastName(admin.getLastName());
        employerResponse.setBirthDate(admin.getBirthDate());
        employerResponse.setGender(admin.getGender());
        employerResponse.setTelephone(admin.getTelephone());
        employerResponse.setAddressId(admin.getAddress().getId());
        employerResponse.setCompanyName(admin.getCompanyName());
        employerResponse.setScale(admin.getScale());
        employerResponse.setFieldActivity(admin.getFieldActivity());

        byte[] photoBytes = null;
        Blob photoBlob = admin.getAvatar();
        if (photoBlob != null) {
            try {
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
                String avatarBase64 = Base64.getEncoder().encodeToString(photoBytes);
                employerResponse.setAvatar(avatarBase64);
            } catch (SQLException e) {
                throw new PhotoRetrievalException("Error retrieving photo");
            }
        }

        return employerResponse;
    }


    @GetMapping("/all-job-by-employer")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<JobResponse>> getAllJobsByEmployerEmail(Principal principal, HttpSession httpSession) {
        String email = principal.getName();
        Admin admin = adminService.getAdmin(email);

        if (admin == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Admin not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        httpSession.setAttribute("loggedInAdmin", admin);
        List<Job> jobs = jobService.getAllJobsByEmployerId(admin.getId());
        if (jobs.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        List<JobResponse> jobResponses = jobs.stream().map(job -> {
            return new JobResponse(
                    job.getId(),
                    job.getJobName(),
                    job.getExperience(),
                    job.getPrice(),
                    job.getApplicationDeadline(),
                    job.getRecruitmentDetails(),
                    job.getTotalValidityPeriod(),
                    job.getActivationDate(),
                    job.getCreateAt(),
                    job.getStatus(),
                    job.getRanker(),
                    job.getQuantity(),
                    job.getWorkingForm(),
                    job.getGender(),
                    job.getCategories().getId()
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(jobResponses);
    }

    @DeleteMapping("/delete/{jobId}")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public void deleteCategory(@PathVariable("jobId") Long jobId){
        jobService.cancelJob(jobId);
    }

    @PutMapping("/update/{jobId}")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> updateJob(
            @PathVariable("jobId") Long jobId,
            @RequestBody JobResponse response,
            Principal principal,
            HttpSession httpSession) {
        Map<String, Object> result = new HashMap<>();
        try {
            String email = principal.getName();
            Admin admin = adminService.getAdmin(email);

            if (admin == null) {
                throw new AdminAlreadyExistsException("Admin not found");
            }
            httpSession.setAttribute("loggedInAdmin", admin);
            Job updatedJob = jobService.updateJob(jobId, admin, response.getJobName(), response.getExperience(),
                    response.getPrice(),response.getApplicationDeadline(), response.getRecruitmentDetails(), response.getCategoryId(), response.getRanker(), response.getQuantity(), response.getWorkingForm(), response.getGender());;
            result.put("status", "success");
            result.put("message", "Job updated successfully");
            result.put("job", updatedJob);
            return ResponseEntity.ok(result);
        } catch (JobAlreadyExistException j) {
            result.put("status", "error");
            result.put("message", j.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        } catch (RuntimeException e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }
    }

    @PutMapping("/update-status/{jobId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Job updateJobStatus(@PathVariable Long jobId, @RequestParam boolean status) {
        return jobService.updateActive(jobId, status);
    }

    @GetMapping("/all-job-with-range")
    public ResponseEntity<List<JobResponse>> getAllJobWithGold() {
        List<Job> jobs = jobService.getAllJobsWithEmployerGold();
        if (jobs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        List<JobResponse> jobResponses = jobs.stream().map(job -> {
            EmployerResponse employerResponse = getEmployerResponse(job.getAdmins());
            return new JobResponse(
                    job.getId(),
                    job.getJobName(),
                    job.getExperience(),
                    job.getPrice(),
                    job.getApplicationDeadline(),
                    job.getRecruitmentDetails(),
                    job.getTotalValidityPeriod(),
                    job.getActivationDate(),
                    employerResponse,
                    job.getCreateAt(),
                    job.getStatus(),
                    job.getRanker(),
                    job.getQuantity(),
                    job.getWorkingForm(),
                    job.getGender(),
                    job.getCategories().getId()
            );
        }).collect(Collectors.toList());
        return ResponseEntity.ok(jobResponses);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long jobId) {
        try {
            Job job = jobService.getJobId(jobId);
            EmployerResponse employerResponse = getEmployerResponse(job.getAdmins());
            JobResponse jobResponse = new JobResponse(
                    job.getId(),
                    job.getJobName(),
                    job.getExperience(),
                    job.getPrice(),
                    job.getApplicationDeadline(),
                    job.getRecruitmentDetails(),
                    job.getTotalValidityPeriod(),
                    job.getActivationDate(),
                    employerResponse,
                    job.getCreateAt(),
                    job.getStatus(),
                    job.getRanker(),
                    job.getQuantity(),
                    job.getWorkingForm(),
                    job.getGender(),
                    job.getCategories().getId()
            );
            return new ResponseEntity<>(jobResponse, HttpStatus.OK);
        } catch (JobAlreadyExistException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/jobs-find-by-employer")
    public int getActiveJobs(@RequestParam Long adminId) {
        return jobService.countJobsByAdmin(adminId);
    }

}
