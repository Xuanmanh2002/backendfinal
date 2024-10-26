package com.springproject.dhVinh.SpringBootProject.controller;

import com.springproject.dhVinh.SpringBootProject.exception.PhotoRetrievalException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.response.AdminResponse;
import com.springproject.dhVinh.SpringBootProject.response.EmployerResponse;
import com.springproject.dhVinh.SpringBootProject.service.IEmployerService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/employer")
@RequiredArgsConstructor
public class EmployerController {

    private final IEmployerService employerService;

    @GetMapping("/list-employer")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<EmployerResponse>> getAdmins() throws SQLException{
        List<Admin> admins = employerService.getEmployer();
        List<EmployerResponse> employerResponses = new ArrayList<>();
        for (Admin admin : admins) {
            byte[] photoBytes = employerService.getAvatarByEmail(admin.getEmail());
            if (photoBytes != null && photoBytes.length > 0) {
                String base64Photo = Base64.encodeBase64String(photoBytes);
                EmployerResponse employerResponse = getEmployerResponse(admin);
                employerResponse.setAvatar(base64Photo);
                employerResponses.add(employerResponse);
            }
        }
        return ResponseEntity.ok(employerResponses);
    }

    @GetMapping("/show-profile-by-{email}")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?>getAdminByEmail(@PathVariable("email") String email){
        try{
            Admin admin = employerService.getEmployer(email);
            return ResponseEntity.ok(admin);
        }catch (UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error finding employer");
        }
    }

    @DeleteMapping("/delete-employer/{email}")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN')")
    public void deleteEmployer(@PathVariable String email) {
        employerService.deleteEmployer(email);
    }

    private EmployerResponse getEmployerResponse(Admin admin) {
        EmployerResponse employerResponse = new EmployerResponse();

        employerResponse.setEmail(admin.getEmail());
        employerResponse.setFirstName(admin.getFirstName());
        employerResponse.setLastName(admin.getLastName());
        employerResponse.setBirthDate(admin.getBirthDate());
        employerResponse.setGender(admin.getGender());
        employerResponse.setTelephone(admin.getTelephone());
        employerResponse.setAddress(admin.getAddress());
        employerResponse.setCompanyName(admin.getCompanyName());

        byte[] photoBytes = null;
        Blob photoBlob = admin.getAvatar();
        if (photoBlob != null) {
            try {
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
            } catch (SQLException e) {
                throw new PhotoRetrievalException("Error retrieving photo");
            }
        }

        return employerResponse;
    }

}
