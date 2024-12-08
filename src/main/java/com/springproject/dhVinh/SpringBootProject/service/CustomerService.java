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
public class CustomerService implements ICustomerService {

    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final AddressRepository addressRepository;

    @Override
    public Admin registerCustomer(String email, String password, String firstName, String lastName, Date birthDate, String gender, String telephone, MultipartFile avatar, Long addressId) throws SQLException, IOException {
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
        admin.setCompanyName(null);
        Role adminRole = roleRepository.findByName("ROLE_CUSTOMER").orElseThrow(() -> new RuntimeException("Role not found"));
        admin.getRoles().add(adminRole);

        return adminRepository.save(admin);
    }

    @Override
    public List<Admin> getCustomer() {
        return adminRepository.findByRoleCustomer();
    }

    @Transactional
    @Override
    public void deleteCustomer(Long id) {
        Admin adm = getCustomer(id);
        if (adm != null) {
            adminRepository.deleteById(id);
        }
    }

    @Override
    public Admin getCustomer(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));
    }

    @Override
    public Admin getCustomerByEmail(String email) {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));
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
    public Admin updateCustomer(String email, String firstName, String lastName, Date birthDate, MultipartFile avatar, String gender, String telephone, Long addressId) throws SQLException, IOException {
        Optional<Admin> optionalAdmin = adminRepository.findByEmail(email);
        if (optionalAdmin.isEmpty()) {
            throw new AdminAlreadyExistsException("Customer with email " + email + " not found.");
        }

        Admin admin = optionalAdmin.get();
        admin.setFirstName(firstName);
        admin.setLastName(lastName);

        if (birthDate != null) {
            admin.setBirthDate(birthDate);
        }

        if (addressId != null) {
            Address address = addressRepository.findById(addressId)
                    .orElseThrow(() -> new RuntimeException("Address not found"));
            admin.setAddress(address);
        }

        admin.setGender(gender);
        admin.setTelephone(telephone);

        if (avatar != null && !avatar.isEmpty()) {
            byte[] photoBytes = avatar.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            admin.setAvatar(photoBlob);
        }

        return adminRepository.save(admin);
    }
}
