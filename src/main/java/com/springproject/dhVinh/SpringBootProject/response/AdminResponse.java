package com.springproject.dhVinh.SpringBootProject.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminResponse {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String gender;
    private String telephone;
    private String address;

}
