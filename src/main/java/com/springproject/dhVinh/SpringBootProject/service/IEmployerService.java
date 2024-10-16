package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Admin;

import java.sql.Date;
import java.util.List;

public interface IEmployerService {
    Admin registerEmployer(String email, String password, String firstName, String lastName, Date birthDate, String avatar,
                          String gender, String telephone, String address, String companyName);

    List<Admin> getEmployer();

    void deleteEmployer(String email);

    Admin getEmployer(String email);
}
