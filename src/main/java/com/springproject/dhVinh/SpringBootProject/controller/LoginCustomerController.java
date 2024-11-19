package com.springproject.dhVinh.SpringBootProject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.request.LoginRequest;
import com.springproject.dhVinh.SpringBootProject.response.CustomerResponse;
import com.springproject.dhVinh.SpringBootProject.response.JwtResponse;
import com.springproject.dhVinh.SpringBootProject.security.admin.AdminDetail;
import com.springproject.dhVinh.SpringBootProject.security.admin.AdminDetailService;
import com.springproject.dhVinh.SpringBootProject.security.jwt.JwtUtils;
import com.springproject.dhVinh.SpringBootProject.service.ICustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class LoginCustomerController {
    private final ICustomerService customerService;

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final ObjectMapper objectMapper;

    private final AdminDetailService adminDetailsService;

    private final PasswordEncoder passwordEncoder;

    @PostMapping(value = "/register-customer")
    public ResponseEntity<CustomerResponse> registerCustomer(@RequestParam("email") String email,
                                                             @RequestParam("password") String password,
                                                             @RequestParam("firstName") String firstName,
                                                             @RequestParam("lastName") String lastName,
                                                             @RequestParam("birthDate") Date birthDate,
                                                             @RequestParam("gender") String gender,
                                                             @RequestParam("telephone") String telephone,
                                                             @RequestParam("addressId") Long addressId,
                                                             @RequestParam("avatar") MultipartFile avatar) throws SQLException, IOException {
        Admin savedAdmin = customerService.registerCustomer(email, password, firstName, lastName, birthDate, gender, telephone, avatar, addressId);
        CustomerResponse response = new CustomerResponse(
                savedAdmin.getId(),
                savedAdmin.getEmail(),
                savedAdmin.getPassword(),
                savedAdmin.getFirstName(),
                savedAdmin.getLastName(),
                savedAdmin.getBirthDate(),
                savedAdmin.getGender(),
                savedAdmin.getTelephone(),
                savedAdmin.getAddress().getId()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/login-customer")
    public ResponseEntity<?> authenticateCustomer(@Valid @RequestBody LoginRequest request){
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtTokenForAdmin(authentication);
        AdminDetail adminDetail = (AdminDetail) authentication.getPrincipal();
        List<String> roles = adminDetail.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        String firstName = adminDetail.getFirstName();
        String lastName = adminDetail.getLastName();
        Blob avatar = adminDetail.getAvatar();
        byte[] avatarBytes = null;
        if (avatar != null) {
            try {
                avatarBytes = avatar.getBytes(1, (int) avatar.length());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.ok(new JwtResponse(
                adminDetail.getId(),
                adminDetail.getEmail(),
                jwt,
                roles,
                firstName,
                lastName,
                avatarBytes
        ));
    }

    @GetMapping("/check-role-customer")
    public ResponseEntity<Map<String, String>> getRole(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_CUSTOMER");
        Map<String, String> response = new HashMap<>();
        response.put("role", role);
        return ResponseEntity.ok(response);
    }
}
