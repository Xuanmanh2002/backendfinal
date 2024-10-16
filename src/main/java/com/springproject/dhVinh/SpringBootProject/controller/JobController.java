package com.springproject.dhVinh.SpringBootProject.controller;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Job;
import com.springproject.dhVinh.SpringBootProject.repository.JobRepository;
import com.springproject.dhVinh.SpringBootProject.response.JobResponse;
import com.springproject.dhVinh.SpringBootProject.service.IAdminService;
import com.springproject.dhVinh.SpringBootProject.service.IJobService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;

@RestController
@RequestMapping("/employer")
@RequiredArgsConstructor
public class JobController {

    private final IAdminService adminService;

    private final IJobService jobService;

    private final JobRepository jobRepository;

    @PostMapping("/create-job")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> saveJob(@ModelAttribute(name = "JobResponse") JobResponse response, Principal principal, HttpSession httpSession) {
        try {
            String email = principal.getName();
            Admin admin = adminService.getAdmin(email);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");
            }
            httpSession.setAttribute("loggedInAdmin", admin);
            Job job = new Job();
            job.setAdmins(admin);
            jobService.addJob(admin, response.getJobName(), response.getExperience(), response.getApplicationDeadline(), response.getRecruitmentDetails());
            return ResponseEntity.ok("Job created successfully");

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

}
