package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Address;
import com.springproject.dhVinh.SpringBootProject.model.Admin;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IAdminService {
    Admin registerAdmin(String email, String password, String firstName, String lastName, Date birthDate,
                        String gender, String telephone, MultipartFile avatar, Long addressId)  throws SQLException, IOException ;

    List<Admin> getAdmins();

    void deleteAdmin(String email);

    Admin getAdmin(String email);

    Optional<Admin> getAdminById(Long adminId);

    byte[] getAvatarByEmail(String email) throws SQLException;

    Admin updateAdmin(String email, String firstName, String lastName, Date birthDate, MultipartFile avatar,
                      String gender, String telephone, Long addressId) throws SQLException, IOException ;

    Long getCountByRoleEmployer();

    Long getCountByRoleCustomer();
}
