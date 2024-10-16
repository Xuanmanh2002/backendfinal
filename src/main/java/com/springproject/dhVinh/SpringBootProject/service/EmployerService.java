package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.exception.AdminAlreadyExistsException;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Role;
import com.springproject.dhVinh.SpringBootProject.repository.AdminRepository;
import com.springproject.dhVinh.SpringBootProject.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EmployerService implements IEmployerService {

    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    @Override
    public Admin registerEmployer(String email, String password, String firstName, String lastName, Date birthDate, String avatar, String gender, String telephone, String address, String companyName) {
        Admin admin = new Admin();
        if (adminRepository.existsByEmail(email)) {
            throw new AdminAlreadyExistsException(email + " already exists");
        }

        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        admin.setAvatar(avatar);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setBirthDate(birthDate);
        admin.setGender(gender);
        admin.setAddress(address);
        admin.setTelephone(telephone);
        admin.setRegistrationDate(LocalDate.now());
        admin.setStatus(true);
        admin.setCompanyName(companyName);
        Role adminRole = roleRepository.findByName("ROLE_EMPLOYER").orElseThrow(() -> new RuntimeException("Role not found"));
        admin.getRoles().add(adminRole);

        return adminRepository.save(admin);
    }

    @Override
    public List<Admin> getEmployer() {
        return adminRepository.findByRoleEmployer();
    }

    @Transactional
    @Override
    public void deleteEmployer(String email) {
        Admin adm = getEmployer(email);
        if (adm != null) {
            adminRepository.deleteByEmail(email);
        }

    }

    @Override
    public Admin getEmployer(String email) {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Company not found"));
    }
}
