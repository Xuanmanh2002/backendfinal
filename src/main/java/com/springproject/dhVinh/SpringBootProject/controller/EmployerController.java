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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/employer")
@RequiredArgsConstructor
public class EmployerController {

    private final IEmployerService employerService;

    @GetMapping("/list-employer")
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

    @GetMapping("/show-profile/{email}")
    public ResponseEntity<?>getEmployerByEmail(@PathVariable("email") String email){
        try {
            Admin admin = employerService.getEmployer(email);
            EmployerResponse response= getEmployerResponse(admin);

            byte[] photoBytes = employerService.getAvatarByEmail(email);
            if (photoBytes != null && photoBytes.length > 0) {
                String base64Photo = Base64.encodeBase64String(photoBytes);
                response.setAvatar(base64Photo);
            }

            return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving employer profile");
        }
    }

    @DeleteMapping("/delete-employer/{email}")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN')")
    public void deleteEmployer(@PathVariable String email) {
        employerService.deleteEmployer(email);
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
        employerResponse.setRank(admin.getRank());
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

    @PutMapping(value = "/update/{email}")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<EmployerResponse> updateEmployer(
            @PathVariable("email") String email,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam(value = "birthDate", required = false) Long birthDateMillis,
            @RequestParam("gender") String gender,
            @RequestParam("telephone") String telephone,
            @RequestParam("addressId") Long addressId,
            @RequestParam("companyName") String companyName,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) throws SQLException, IOException {
        Date birthDate = null;
        if (birthDateMillis != null) {
            birthDate = new Date(birthDateMillis);
        }
        Admin savedAdmin = employerService.updateEmployer(email, firstName, lastName, birthDate, avatar, gender, telephone,companyName, addressId);
        EmployerResponse response = new EmployerResponse(
                savedAdmin.getEmail(),
                savedAdmin.getFirstName(),
                savedAdmin.getLastName(),
                savedAdmin.getBirthDate(),
                savedAdmin.getGender(),
                savedAdmin.getTelephone(),
                savedAdmin.getCompanyName(),
                savedAdmin.getAddress().getId()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("list-employer-by-rank")
    public ResponseEntity<List<EmployerResponse>> getEmployerRank() throws SQLException {
        List<Admin> admins = employerService.findByRank();
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

}
