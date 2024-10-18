package com.springproject.dhVinh.SpringBootProject.controller;

import com.springproject.dhVinh.SpringBootProject.exception.PhotoRetrievalException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.response.AdminResponse;
import com.springproject.dhVinh.SpringBootProject.service.IAdminService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Blob;
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

}
