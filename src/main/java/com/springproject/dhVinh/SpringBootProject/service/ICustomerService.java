package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public interface ICustomerService {

    Admin registerCustomer(String email, String password, String firstName, String lastName, Date birthDate, String gender, String telephone,  MultipartFile avatar, Long addressId)  throws SQLException, IOException;

    List<Admin> getCustomer();

    void deleteCustomer(String email);

    Admin getCustomer(String email);

    byte[] getAvatarByEmail(String email) throws SQLException;

    Admin updateCustomer(String email, String firstName, String lastName, Date birthDate, MultipartFile avatar,
                      String gender, String telephone, Long addressId) throws SQLException, IOException ;
}
