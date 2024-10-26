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
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
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
    public Admin registerAdmin(String email, String password, String firstName, String lastName, Date birthDate,
                               String gender, String telephone, String address, MultipartFile avatar)  throws SQLException, IOException {
        Admin admin = new Admin();
        if (adminRepository.existsByEmail(email)) {
            throw new AdminAlreadyExistsException(email + " already exists");
        }

        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        if (!avatar.isEmpty()){
            byte[] photoBytes = avatar.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            admin.setAvatar(photoBlob);
        }
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

    @Override
    public byte[] getAvatarByEmail(String email) throws SQLException {
        Optional<Admin> admin = adminRepository.findByEmail(email);
        if(admin.isEmpty()) {
            throw new AdminAlreadyExistsException("Sorry, admin not found");
        }
        Blob photoBlob = admin.get().getAvatar();
        if(photoBlob != null){
            return photoBlob.getBytes(1, (int) photoBlob.length());
        }
        return null;
    }

    @Override
    public Admin updateAdmin(String email, String firstName, String lastName, Date birthDate, MultipartFile avatar, String gender, String telephone, String address) throws SQLException, IOException {
        Optional<Admin> optionalAdmin = adminRepository.findByEmail(email);
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            admin.setFirstName(firstName);
            admin.setLastName(lastName);
            if (birthDate != null) {
                admin.setBirthDate(birthDate);
            }
            admin.setGender(gender);
            admin.setAddress(address);
            admin.setTelephone(telephone);
            if (avatar != null && !avatar.isEmpty()) {
                byte[] photoBytes = avatar.getBytes();
                Blob photoBlob = new SerialBlob(photoBytes);
                admin.setAvatar(photoBlob);
            }
            admin.setRegistrationDate(LocalDate.now());
            admin.setStatus(true);
            admin.setCompanyName("Topcv");

            return adminRepository.save(admin);
        } else {
            throw new AdminAlreadyExistsException("Admin with email " + email + " not found.");
        }
    }

}
