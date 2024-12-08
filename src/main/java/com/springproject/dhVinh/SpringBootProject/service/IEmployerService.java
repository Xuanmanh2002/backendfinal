package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public interface IEmployerService {
    Admin registerEmployer(String email, String password, String firstName, String lastName, Date birthDate, String gender, String telephone, MultipartFile avatar, String companyName,  Long addressId, String scale, String fieldActivity)  throws SQLException, IOException;

    List<Admin> getEmployer();

    void deleteEmployer(String email);

    Admin getEmployer(String email);

    String getEmailByCompanyName(String companyName);

    byte[] getAvatarByEmail(String email) throws SQLException;

    Admin updateEmployer(String email, String firstName, String lastName, Date birthDate, MultipartFile avatar,
                      String gender, String telephone, String companyName,  Long addressId, String scale, String fieldActivity) throws SQLException, IOException ;

    List<Admin> findByRank();
}
