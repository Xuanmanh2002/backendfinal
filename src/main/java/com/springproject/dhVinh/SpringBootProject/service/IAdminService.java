package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Admin;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface IAdminService {
    Admin registerAdmin(String email, String password, String firstName, String lastName, Date birthDate, String avatar,
                        String gender, String telephone, String address);

    List<Admin> getAdmins();

    void deleteAdmin(String email);

    Admin getAdmin(String email);

    Optional<Admin> getAdminById(Long adminId);

}
