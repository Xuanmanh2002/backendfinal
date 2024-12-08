package com.springproject.dhVinh.SpringBootProject.controller;

import com.springproject.dhVinh.SpringBootProject.exception.PhotoRetrievalException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.response.CustomerResponse;
import com.springproject.dhVinh.SpringBootProject.response.EmployerResponse;
import com.springproject.dhVinh.SpringBootProject.service.ICustomerService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final ICustomerService customerService;

    @GetMapping("/list")
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

    @GetMapping("/show-profile")
    @PreAuthorize("hasRole('ROLE_EMPLOYER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<CustomerResponse> getCustomerByEmail(Principal principal) throws SQLException {

        String customerEmail = principal.getName();
        Admin customer = customerService.getCustomerByEmail(customerEmail);
        Admin savedAdmin = customerService.getCustomer(customer.getId());

        Admin admin = customerService.getCustomerByEmail(customerEmail);
        if (admin == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] photoBytes = customerService.getAvatarByEmail(customerEmail);
        CustomerResponse customerResponse = getCustomerResponse(admin);

        if (photoBytes != null && photoBytes.length > 0) {
            String base64Photo = Base64.encodeBase64String(photoBytes);
            customerResponse.setAvatar(base64Photo);
        }

        return ResponseEntity.ok(customerResponse);
    }

    @DeleteMapping("/delete-{id}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_ADMIN')")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
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

    @PutMapping(value = "/update")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<CustomerResponse> updateEmployer(
            @RequestParam("email") String email,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam(value = "birthDate", required = false) Long birthDateMillis,
            @RequestParam("gender") String gender,
            @RequestParam("telephone") String telephone,
            @RequestParam("addressId") Long addressId,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar,
            Principal principal) throws SQLException, IOException {
        String customerEmail = principal.getName();
        Admin existingCustomer = customerService.getCustomerByEmail(customerEmail);
        Date birthDate = birthDateMillis != null ? new Date(birthDateMillis) : null;
        Admin updatedCustomer = customerService.updateCustomer(
                email,
                firstName,
                lastName,
                birthDate,
                avatar,
                gender,
                telephone,
                addressId
        );
        CustomerResponse response = new CustomerResponse(
                updatedCustomer.getEmail(),
                updatedCustomer.getFirstName(),
                updatedCustomer.getLastName(),
                updatedCustomer.getBirthDate(),
                updatedCustomer.getGender(),
                updatedCustomer.getTelephone(),
                updatedCustomer.getAddress().getId()
        );

        return ResponseEntity.ok(response);
    }
}
