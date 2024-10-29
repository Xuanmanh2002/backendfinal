package com.springproject.dhVinh.SpringBootProject.controller;

import com.springproject.dhVinh.SpringBootProject.exception.AdminAlreadyExistsException;
import com.springproject.dhVinh.SpringBootProject.exception.PhotoRetrievalException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.response.AdminResponse;
import com.springproject.dhVinh.SpringBootProject.service.IAdminService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final IAdminService adminService;

    @GetMapping("/list-admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AdminResponse>> getAdmins() throws SQLException {
        List<Admin> admins = adminService.getAdmins();
        List<AdminResponse> adminResponses = new ArrayList<>();
        for (Admin admin : admins) {
            byte[] photoBytes = adminService.getAvatarByEmail(admin.getEmail());
            if (photoBytes != null && photoBytes.length > 0) {
                String base64Photo = Base64.encodeBase64String(photoBytes);
                AdminResponse adminResponse = getAdminResponse(admin);
                adminResponse.setAvatar(base64Photo);
                adminResponses.add(adminResponse);
            }
        }
        return ResponseEntity.ok(adminResponses);
    }

    @GetMapping("/show-profile/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAdminByEmail(@PathVariable("email") String email) {
        try {
            Admin admin = adminService.getAdmin(email);
            AdminResponse adminResponse = getAdminResponse(admin);

            byte[] photoBytes = adminService.getAvatarByEmail(email);
            if (photoBytes != null && photoBytes.length > 0) {
                String base64Photo = Base64.encodeBase64String(photoBytes);
                adminResponse.setAvatar(base64Photo);
            }

            return ResponseEntity.ok(adminResponse);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving admin profile");
        }
    }

    private AdminResponse getAdminResponse(Admin admin) {
        AdminResponse adminResponse = new AdminResponse();
        adminResponse.setId(admin.getId());
        adminResponse.setEmail(admin.getEmail());
        adminResponse.setFirstName(admin.getFirstName());
        adminResponse.setLastName(admin.getLastName());
        adminResponse.setBirthDate(admin.getBirthDate());
        adminResponse.setGender(admin.getGender());
        adminResponse.setTelephone(admin.getTelephone());
        adminResponse.setAddress(admin.getAddress());

        byte[] photoBytes = null;
        Blob photoBlob = admin.getAvatar();
        if (photoBlob != null) {
            try {
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
            } catch (SQLException e) {
                throw new PhotoRetrievalException("Error retrieving photo");
            }
        }

        return adminResponse;
    }

    @PutMapping(value = "/update/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AdminResponse> updateAdmin(
            @PathVariable("email") String email,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam(value = "birthDate", required = false) Long birthDateMillis,
            @RequestParam("gender") String gender,
            @RequestParam("telephone") String telephone,
            @RequestParam("address") String address,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) throws SQLException, IOException {
        Date birthDate = null;
        if (birthDateMillis != null) {
            birthDate = new Date(birthDateMillis);
        }
        Admin savedAdmin = adminService.updateAdmin(email, firstName, lastName, birthDate, avatar, gender, telephone, address);
        AdminResponse response = new AdminResponse(
                savedAdmin.getEmail(),
                savedAdmin.getFirstName(),
                savedAdmin.getLastName(),
                savedAdmin.getBirthDate(),
                savedAdmin.getGender(),
                savedAdmin.getTelephone(),
                savedAdmin.getAddress()
        );
        return ResponseEntity.ok(response);
    }

}
