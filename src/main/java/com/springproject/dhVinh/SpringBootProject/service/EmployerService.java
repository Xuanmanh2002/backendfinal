package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.exception.AdminAlreadyExistsException;
import com.springproject.dhVinh.SpringBootProject.model.Address;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Role;
import com.springproject.dhVinh.SpringBootProject.repository.AddressRepository;
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
public class EmployerService implements IEmployerService {

    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final AddressRepository addressRepository;

    @Override
    public Admin registerEmployer(String email, String password, String firstName, String lastName, Date birthDate,  String gender, String telephone, MultipartFile avatar, String companyName, Long addressId, String scale, String fieldActivity)  throws SQLException, IOException {
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
        Optional<Address> optionalAddress = addressRepository.findById(addressId);
        if (!optionalAddress.isPresent()) {
            throw new RuntimeException("Address not found");
        }
        Address address = optionalAddress.get();
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
        admin.setScale(scale);
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
                .orElseThrow(() -> new UsernameNotFoundException("Employer not found"));
    }

    @Override
    public String getEmailByCompanyName(String companyName) {
        Optional<Admin> admin = adminRepository.findByCompanyName(companyName);
        if (admin != null) {
            return admin.get().getEmail();
        }
        throw new RuntimeException("Employer with company name '" + companyName + "' not found.");
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
    public Admin updateEmployer(String email, String firstName, String lastName, Date birthDate, MultipartFile avatar, String gender, String telephone, String companyName, Long addressId, String scale, String fieldActivity) throws SQLException, IOException {
        Optional<Admin> optionalAdmin = adminRepository.findByEmail(email);
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            admin.setFirstName(firstName);
            admin.setLastName(lastName);
            if (birthDate != null) {
                admin.setBirthDate(birthDate);
            }
            Optional<Address> optionalAddress = addressRepository.findById(addressId);
            if (!optionalAddress.isPresent()) {
                throw new RuntimeException("Address not found");
            }
            Address address = optionalAddress.get();
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
            admin.setCompanyName(companyName);
            admin.setScale(scale);
            admin.setFieldActivity(fieldActivity);
            return adminRepository.save(admin);
        } else {
            throw new AdminAlreadyExistsException("Employer with email " + email + " not found.");
        }
    }

    @Override
    public List<Admin> findByRank() {
        return adminRepository.findByRank();
    }
}
