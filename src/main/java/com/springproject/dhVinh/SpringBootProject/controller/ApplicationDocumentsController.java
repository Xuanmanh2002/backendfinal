package com.springproject.dhVinh.SpringBootProject.controller;

import com.springproject.dhVinh.SpringBootProject.exception.PhotoRetrievalException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.ApplicationDocuments;
import com.springproject.dhVinh.SpringBootProject.model.Job;
import com.springproject.dhVinh.SpringBootProject.response.ApplicationDocumentsResponse;
import com.springproject.dhVinh.SpringBootProject.response.CustomerResponse;
import com.springproject.dhVinh.SpringBootProject.response.EmployerResponse;
import com.springproject.dhVinh.SpringBootProject.service.IApplicationDocumentsService;
import com.springproject.dhVinh.SpringBootProject.service.ICustomerService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/application-documents")
public class ApplicationDocumentsController {

    private final IApplicationDocumentsService adService;

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
        Admin admin = customerService.getCustomerByEmail(customerEmail);

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

    @GetMapping("/list-applications")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<List<ApplicationDocumentsResponse>> getAllByEmployer(Principal principal) throws SQLException {
        String employerEmail = principal.getName();
        Admin employer = customerService.getCustomerByEmail(employerEmail);
        List<ApplicationDocuments> documents = adService.getAllJobByAdmin(employer.getId());
        List<ApplicationDocumentsResponse> responses = documents.stream().map(applicationDocuments -> {
            CustomerResponse customerResponse = getCustomerResponse(applicationDocuments.getAdmins());
            byte[] photoBytes = null;
            Blob photoBlob = applicationDocuments.getCv();
            if (photoBlob != null) {
                try {
                    photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
                } catch (SQLException e) {
                    throw new PhotoRetrievalException("Error retrieving CV data");
                }
            }
            return new ApplicationDocumentsResponse(
                    applicationDocuments.getId(),
                    applicationDocuments.getFullName(),
                    applicationDocuments.getEmail(),
                    applicationDocuments.getTelephone(),
                    photoBytes,
                    applicationDocuments.getLetter(),
                    applicationDocuments.getStatus(),
                    applicationDocuments.getCreateAt(),
                    customerResponse,
                    applicationDocuments.getJobs().getId()
            );
        }).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
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

    @DeleteMapping("/delete/{applicationDocumentsId}")
    public void deleteApplicationDocuments(@PathVariable("applicationDocumentsId") Long applicationDocumentsId) {
        adService.cancleAD(applicationDocumentsId);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<List<ApplicationDocumentsResponse>> getAllByCustomer(Principal principal) throws SQLException {
        String customerEmail = principal.getName();
        Admin customer = customerService.getCustomerByEmail(customerEmail);
        List<ApplicationDocuments> documents = adService.getAllJobByCustomer(customer.getId());
        List<ApplicationDocumentsResponse> responses = documents.stream().map(applicationDocuments -> {
            CustomerResponse customerResponse = getCustomerResponse(applicationDocuments.getAdmins());
            byte[] photoBytes = null;
            Blob photoBlob = applicationDocuments.getCv();
            if (photoBlob != null) {
                try {
                    photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
                } catch (SQLException e) {
                    throw new PhotoRetrievalException("Error retrieving CV data");
                }
            }
            Job job = applicationDocuments.getJobs();
            Admin employer = job != null ? job.getAdmins() : null;
            return new ApplicationDocumentsResponse(
                    applicationDocuments.getId(),
                    applicationDocuments.getFullName(),
                    applicationDocuments.getEmail(),
                    applicationDocuments.getTelephone(),
                    photoBytes,
                    applicationDocuments.getLetter(),
                    applicationDocuments.getStatus(),
                    applicationDocuments.getCreateAt(),
                    customerResponse,
                    job != null ? job.getId() : null,
                    job != null ? job.getJobName() : null,
                    employer != null ? employer.getId() : null,
                    employer != null ? employer.getCompanyName() : null
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PutMapping("/update-status/{id}")
    public ResponseEntity<ApplicationDocuments> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        ApplicationDocuments updatedApplication = adService.updateStatus(id, status);
        return ResponseEntity.ok(updatedApplication);
    }

    @GetMapping("/status")
    public ResponseEntity<List<ApplicationDocumentsResponse>> getApplicationDocumentsByStatus(
            @RequestParam String status,
            @RequestParam Long adminId) {
        List<ApplicationDocuments> documents = adService.getApplicationDocumentsByStatus(status, adminId);
        List<ApplicationDocumentsResponse> responses = documents.stream().map(applicationDocuments -> {
            CustomerResponse customerResponse = getCustomerResponse(applicationDocuments.getAdmins());
            byte[] photoBytes = null;
            Blob photoBlob = applicationDocuments.getCv();
            if (photoBlob != null) {
                try {
                    photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
                } catch (SQLException e) {
                    throw new PhotoRetrievalException("Error retrieving CV data");
                }
            }
            Job job = applicationDocuments.getJobs();
            Admin employer = job != null ? job.getAdmins() : null;
            return new ApplicationDocumentsResponse(
                    applicationDocuments.getId(),
                    applicationDocuments.getFullName(),
                    applicationDocuments.getEmail(),
                    applicationDocuments.getTelephone(),
                    photoBytes,
                    applicationDocuments.getLetter(),
                    applicationDocuments.getStatus(),
                    applicationDocuments.getCreateAt(),
                    customerResponse,
                    job != null ? job.getId() : null,
                    job != null ? job.getJobName() : null,
                    employer != null ? employer.getId() : null,
                    employer != null ? employer.getCompanyName() : null
            );
        }).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
