package com.springproject.dhVinh.SpringBootProject.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import java.sql.Date;

@Data
@NoArgsConstructor
public class AdminResponse {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String avatar;
    private String gender;
    private String telephone;
    private String address;

    public AdminResponse(Long id, String email, String password, String firstName, String lastName, Date birthDate, String gender, String telephone, String address) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.telephone = telephone;
        this.address = address;
    }

    public AdminResponse(String email, String firstName, String lastName, Date birthDate, String gender, String telephone, String address) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.telephone = telephone;
        this.address = address;
    }
}
