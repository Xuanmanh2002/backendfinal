package com.springproject.dhVinh.SpringBootProject.controller;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.ApplicationDocuments;
import com.springproject.dhVinh.SpringBootProject.response.ApplicationDocumentsResponse;
import com.springproject.dhVinh.SpringBootProject.service.IADService;
import com.springproject.dhVinh.SpringBootProject.service.ICustomerService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/application-documents")
public class ADController{

    private final IADService adService;

    private final ICustomerService customerService;

    @PostMapping(value = "/add")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<ApplicationDocumentsResponse> saveApplicationDocuments(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("telephone") String telephone,
            @RequestParam("letter") String letter,
            @RequestPart("cv") MultipartFile cv,
            @RequestParam("jobId") Long jobId,
            Principal principal, HttpSession httpSession) throws SQLException, IOException {

        String customerEmail = principal.getName();
        Admin admin = customerService.getCustomer(customerEmail);

        ApplicationDocuments applicationDocuments = adService.addAD(
                admin, fullName, email, telephone, cv, letter, jobId);
        ApplicationDocumentsResponse response = new ApplicationDocumentsResponse(
                applicationDocuments.getId(),
                applicationDocuments.getFullName(),
                applicationDocuments.getEmail(),
                applicationDocuments.getTelephone(),
                applicationDocuments.getLetter(),
                applicationDocuments.getJobs().getId()
        );

        return ResponseEntity.ok(response);
    }
}
