package com.springproject.dhVinh.SpringBootProject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springproject.dhVinh.SpringBootProject.exception.AdminAlreadyExistsException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.request.LoginRequest;
import com.springproject.dhVinh.SpringBootProject.response.EmployerResponse;
import com.springproject.dhVinh.SpringBootProject.response.JwtResponse;
import com.springproject.dhVinh.SpringBootProject.security.admin.AdminDetail;
import com.springproject.dhVinh.SpringBootProject.security.admin.AdminDetailService;
import com.springproject.dhVinh.SpringBootProject.security.jwt.JwtUtils;
import com.springproject.dhVinh.SpringBootProject.service.IEmployerService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class LoginEmployerController {

    private final IEmployerService companyService;

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final ObjectMapper objectMapper;

    private final AdminDetailService adminDetailsService;

    private final PasswordEncoder passwordEncoder;

    @PostMapping(value = "/register-employer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @SneakyThrows
    public ResponseEntity<?> registerEmployer(@ModelAttribute(name = "EmployerResponse") EmployerResponse response,
                                             @RequestPart("avatar") MultipartFile avatar) {
        try {
            Path basePath = Paths.get("/home/suanmanh/Documents/sendUBUNTU/argon-dashboard-react-master/src/assets/img/avatar");
            Files.createDirectories(basePath);
            Path filePath = basePath.resolve(avatar.getOriginalFilename());
            try (InputStream inputStream = avatar.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
            String avatarPath = filePath.toAbsolutePath().toString();

            Admin admin = companyService.registerEmployer(response.getEmail(),
                    response.getPassword(), response.getFirstName(), response.getLastName(), response.getBirthDate(),
                    avatarPath, response.getGender(), response.getTelephone(), response.getAddress(), response.getCompanyName());
            Map<String, Object> message = new HashMap<>();
            message.put("message", "success");
            message.put("data", admin);
            return ResponseEntity.ok().body(objectMapper.writeValueAsString(message));
        } catch (AdminAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest request){
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

}
