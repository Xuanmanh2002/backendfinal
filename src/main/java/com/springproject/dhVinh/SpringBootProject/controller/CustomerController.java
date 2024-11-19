package com.springproject.dhVinh.SpringBootProject.controller;

import com.springproject.dhVinh.SpringBootProject.exception.PhotoRetrievalException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.response.CustomerResponse;
import com.springproject.dhVinh.SpringBootProject.service.ICustomerService;
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
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final ICustomerService customerService;

    @GetMapping("/list-customer")
    public ResponseEntity<List<CustomerResponse>> getAdmins() throws SQLException {
        List<Admin> admins = customerService.getCustomer();
        List<CustomerResponse> customerResponses = new ArrayList<>();
        for (Admin admin : admins) {
            byte[] photoBytes = customerService.getAvatarByEmail(admin.getEmail());
            if (photoBytes != null && photoBytes.length > 0) {
                String base64Photo = Base64.encodeBase64String(photoBytes);
                CustomerResponse customerResponse = getCustomerResponse(admin);
                customerResponse.setAvatar(base64Photo);
                customerResponses.add(customerResponse);
            }
        }
        return ResponseEntity.ok(customerResponses);
    }

    @GetMapping("/show-profile-by-{email}")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<?>getCustomerByEmail(@PathVariable("email") String email){
        try{
            Admin admin = customerService.getCustomer(email);
            return ResponseEntity.ok(admin);
        }catch (UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error finding customer");
        }
    }

    @DeleteMapping("/delete-customer/{email}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_ADMIN')")
    public void deleteCustomer(@PathVariable String email) {
        customerService.deleteCustomer(email);
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
}
