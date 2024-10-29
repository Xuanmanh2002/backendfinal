package com.springproject.dhVinh.SpringBootProject.controller;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Category;
import com.springproject.dhVinh.SpringBootProject.model.Job;
import com.springproject.dhVinh.SpringBootProject.repository.JobRepository;
import com.springproject.dhVinh.SpringBootProject.response.AdminResponse;
import com.springproject.dhVinh.SpringBootProject.response.JobResponse;
import com.springproject.dhVinh.SpringBootProject.service.IAdminService;
import com.springproject.dhVinh.SpringBootProject.service.IJobService;
import com.springproject.dhVinh.SpringBootProject.service.JobService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Date;
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
            Principal principal,
            HttpSession httpSession) {
        Map<String, Object> jsonResponse = new HashMap<>();
        try {
            String email = principal.getName();
            Admin admin = adminService.getAdmin(email);

            if (admin == null) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Admin not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(jsonResponse);
            }

            httpSession.setAttribute("loggedInAdmin", admin);

            Job job = jobService.addJob(admin, jobResponse.getJobName(), jobResponse.getExperience(),
                    jobResponse.getApplicationDeadline(), jobResponse.getRecruitmentDetails(),
                    jobResponse.getCategoryId());
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Job created successfully");
            jsonResponse.put("job", job);

            return ResponseEntity.status(HttpStatus.CREATED).body(jsonResponse);

        } catch (RuntimeException e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(jsonResponse);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Job>> getAllJob(){
        List<Job> jobs = jobService.getAllJobs();
        if(jobs.isEmpty()){
            return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(jobs, HttpStatus.OK);
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

        // Kiểm tra nếu không có việc làm nào
        if (jobs.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<JobResponse> jobResponses = jobs.stream().map(job -> {
            return new JobResponse(
                    job.getId(),
                    job.getJobName(),
                    job.getExperience(),
                    job.getApplicationDeadline(),
                    job.getRecruitmentDetails(),
                    job.getCategories().getId()
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(jobResponses);
    }

}
