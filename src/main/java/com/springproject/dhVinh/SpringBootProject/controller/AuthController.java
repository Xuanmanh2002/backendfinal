package com.springproject.dhVinh.SpringBootProject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springproject.dhVinh.SpringBootProject.exception.AdminAlreadyExistsException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.request.LoginRequest;
import com.springproject.dhVinh.SpringBootProject.response.AdminResponse;
import com.springproject.dhVinh.SpringBootProject.response.JwtResponse;
import com.springproject.dhVinh.SpringBootProject.security.admin.AdminDetail;
import com.springproject.dhVinh.SpringBootProject.security.admin.AdminDetailService;
import com.springproject.dhVinh.SpringBootProject.security.jwt.JwtUtils;
import com.springproject.dhVinh.SpringBootProject.service.IAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAdminService adminService;

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final ObjectMapper objectMapper;

    private final AdminDetailService adminDetailsService;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @SneakyThrows
    public ResponseEntity<?> registerAdmin(@ModelAttribute(name = "AdminResponse") AdminResponse response,
                                           @RequestPart("avatar") MultipartFile avatar) {

        try {
            Path basePath = Paths.get("/home/suanmanh/Documents/sendUBUNTU/argon-dashboard-react-master/src/assets/img/avatar");
            Files.createDirectories(basePath);

            Path filePath = basePath.resolve(avatar.getOriginalFilename());
            try (InputStream inputStream = avatar.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
            String avatarPath = filePath.toAbsolutePath().toString();
            Admin admin = adminService.registerAdmin(response.getEmail(),
                    response.getPassword(), response.getFirstName(), response.getLastName(), response.getBirthDate(),
                    avatarPath, response.getGender(), response.getTelephone(), response.getAddress());
            Map<String, Object> message = new HashMap<>();
            message.put("message", "success");
            message.put("data", admin);
            return ResponseEntity.ok().body(objectMapper.writeValueAsString(message));
        } catch (AdminAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest request) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtTokenForAdmin(authentication);
        AdminDetail adminDetail = (AdminDetail) authentication.getPrincipal();
        List<String> roles = adminDetail.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).toList();
        String firstName = adminDetail.getFirstName();
        String lastName = adminDetail.getLastName();
        String avatar = adminDetail.getAvatar();
        return ResponseEntity.ok(new JwtResponse(
                adminDetail.getId(),
                adminDetail.getEmail(),
                jwt,
                roles,
                firstName,
                lastName,
                avatar
        ));
    }
    @GetMapping("/check-role")
    public ResponseEntity<Map<String, String>> getRole(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_ADMIN");

        Map<String, String> response = new HashMap<>();
        response.put("role", role);

        return ResponseEntity.ok(response);
    }

}
