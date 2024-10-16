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
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AdminService implements IAdminService {

    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    @Override
    public Admin registerAdmin(String email, String password, String firstName, String lastName, Date birthDate, String avatar,
                               String gender, String telephone, String address)  {
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
        admin.setCompanyName("Topcv");
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow(() -> new RuntimeException("Role not found"));
        admin.getRoles().add(adminRole);

        return adminRepository.save(admin);
    }

    @Override
    public List<Admin> getAdmins() {
        return adminRepository.findByRoleAdmin();
    }

    @Transactional
    @Override
    public void deleteAdmin(String email) {
        Admin adm = getAdmin(email);
        if (adm != null) {
            adminRepository.deleteByEmail(email);
        }
    }

    @Override
    public Admin getAdmin(String email) {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));
    }

    @Override
    public Optional<Admin> getAdminById(Long adminId) {
        return Optional.of(adminRepository.findById(adminId).get());
    }
}
