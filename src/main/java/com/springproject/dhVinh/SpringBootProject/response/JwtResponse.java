package com.springproject.dhVinh.SpringBootProject.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;

import java.sql.Blob;
import java.util.List;

/**
 * @author Simpson Alfred
 */

@Data
@NoArgsConstructor
public class JwtResponse {
    private Long id;
    private String email;
    private String token;
    private List<String> roles;
    private String firstName;
    private String lastName;
    private Blob avatar;

    public JwtResponse(Long id, String email, String token, List<String> roles, String firstName, String lastName, byte[] photoBytes) {
        this.id = id;
        this.email = email;
        this.token = token;
        this.roles = roles;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = avatar;
    }

}
