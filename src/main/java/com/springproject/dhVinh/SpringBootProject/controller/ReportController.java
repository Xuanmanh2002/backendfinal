package com.springproject.dhVinh.SpringBootProject.controller;

import com.springproject.dhVinh.SpringBootProject.exception.PhotoRetrievalException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Report;
import com.springproject.dhVinh.SpringBootProject.response.CustomerResponse;
import com.springproject.dhVinh.SpringBootProject.response.OrderResponse;
import com.springproject.dhVinh.SpringBootProject.response.ReportResponse;
import com.springproject.dhVinh.SpringBootProject.service.ICustomerService;
import com.springproject.dhVinh.SpringBootProject.service.IReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final IReportService reportService;

    private final ICustomerService customerService;

    @PostMapping("/create-report")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<Map<String, Object>> createReport(
            @RequestBody ReportResponse reportRequest,
            Principal principal
    ) {
        Map<String, Object> responseBody = new HashMap<>();
        String customerEmail = principal.getName();

        try {
            Admin customer = customerService.getCustomerByEmail(customerEmail);
            if (customer == null) {
                responseBody.put("error", "Customer not found");
                return ResponseEntity.status(404).body(responseBody);
            }

            Report report = reportService.addReport(
                    customer,
                    reportRequest.getFullName(),
                    reportRequest.getEmail(),
                    reportRequest.getTelephone(),
                    reportRequest.getLetter(),
                    reportRequest.getAddress(),
                    reportRequest.getJobId()
            );

            CustomerResponse customerResponse = getCustomerResponse(customer);

            responseBody.put("id", report.getId());
            responseBody.put("fullName", report.getFullName());
            responseBody.put("email", report.getEmail());
            responseBody.put("telephone", report.getTelephone());
            responseBody.put("letter", report.getLetter());
            responseBody.put("address", report.getAddress());
            responseBody.put("createdAt", report.getCreateAt());
            responseBody.put("customer", customerResponse);
            responseBody.put("jobId", report.getJobs().getId());

            return ResponseEntity.ok(responseBody);

        } catch (RuntimeException e) {
            responseBody.put("error", "Customer not found: " + e.getMessage());
            return ResponseEntity.status(404).body(responseBody);
        } catch (Exception e) {
            responseBody.put("error", "Failed: " + e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }
    }
    private CustomerResponse getCustomerResponse(Admin admin) {
        CustomerResponse customerResponse = new CustomerResponse();

        customerResponse.setEmail(admin.getEmail());
        customerResponse.setFirstName(admin.getFirstName());
        customerResponse.setLastName(admin.getLastName());
        customerResponse.setBirthDate(admin.getBirthDate());
        customerResponse.setGender(admin.getGender());
        customerResponse.setTelephone(admin.getTelephone());
        customerResponse.setAddressId(admin.getAddress().getId());

        byte[] photoBytes = null;
        Blob photoBlob = admin.getAvatar();
        if (photoBlob != null) {
            try {
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
            } catch (SQLException e) {
                throw new PhotoRetrievalException("Error retrieving photo");
            }
        }

        return customerResponse;
    }

    @DeleteMapping("/delete-report/{reportId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<String> cancelReport(@PathVariable Long reportId) {
        reportService.cancleReport(reportId);
        return ResponseEntity.ok("Report cancelled successfully");
    }

    @GetMapping("/all-reports")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        try {
            List<Report> reports = reportService.getAllReport();

            List<ReportResponse> responses = reports.stream().map(report -> {
                CustomerResponse customerResponse = report.getAdmins() != null
                        ? getCustomerResponse(report.getAdmins())
                        : null;

                return new ReportResponse(
                        report.getId(),
                        report.getFullName(),
                        report.getEmail(),
                        report.getTelephone(),
                        report.getCreateAt(),
                        report.getLetter(),
                        report.getAddress(),
                        report.getStatus(),
                        customerResponse,
                        report.getJobs() != null ? report.getJobs().getId() : null
                );
            }).collect(Collectors.toList());

            return ResponseEntity.ok(responses);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @PutMapping("/update-status/{reportId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Report> updateStatus(
            @PathVariable Long reportId,
            @RequestParam("status") Boolean status
    ) {
        Report updatedReport = reportService.updateStatus(reportId, status);
        return ResponseEntity.ok(updatedReport);
    }
}
