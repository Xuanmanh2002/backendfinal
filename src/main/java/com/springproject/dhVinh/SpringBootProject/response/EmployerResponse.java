package com.springproject.dhVinh.SpringBootProject.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
public class EmployerResponse {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String gender;
    private String telephone;
    private String address;
    private String companyName;
}
