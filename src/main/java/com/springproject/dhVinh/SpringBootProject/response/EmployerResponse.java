package com.springproject.dhVinh.SpringBootProject.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;

import java.sql.Date;

@Data
@NoArgsConstructor
public class EmployerResponse {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String avatar;
    private String gender;
    private String telephone;
    private String companyName;
    private String rank;
    private String scale;
    private String fieldActivity;
    private Long addressId;

    public EmployerResponse(Long id, String email, String firstName, String lastName, Date birthDate, String gender, String telephone, String companyName, String scale, String fieldActivity, Long addressId) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.telephone = telephone;
        this.companyName = companyName;
        this.rank = rank;
        this.scale = scale;
        this.fieldActivity = fieldActivity;
        this.addressId = addressId;
    }

    public EmployerResponse(Long id, String email, String password, String firstName, String lastName, Date birthDate, String avatar, String gender, String telephone, String companyName, String scale, String fieldActivity, Long addressId) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.avatar = avatar;
        this.gender = gender;
        this.telephone = telephone;
        this.companyName = companyName;
        this.scale = scale;
        this.fieldActivity = fieldActivity;
        this.addressId = addressId;
    }

    public EmployerResponse(Long id, String email, String password, String firstName, String lastName, Date birthDate, String gender, String telephone, String companyName, String scale, String fieldActivity, Long addressId) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.telephone = telephone;
        this.companyName = companyName;
        this.scale = scale;
        this.fieldActivity = fieldActivity;
        this.addressId = addressId;
    }
}
