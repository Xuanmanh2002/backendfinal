package com.springproject.dhVinh.SpringBootProject.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class AddressResponse {
    private Long id;
    private String name;

    public AddressResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public AddressResponse(String name) {
        this.name = name;
    }
}
