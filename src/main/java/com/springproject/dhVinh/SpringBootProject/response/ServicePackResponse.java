package com.springproject.dhVinh.SpringBootProject.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ServicePackResponse {
    private Long id;
    private String serviceName;
    private double price;
    private Long validityPeriod;
    private String description;

    public ServicePackResponse(Long id, String serviceName, double price, Long validityPeriod, String description) {
        this.id = id;
        this.serviceName = serviceName;
        this.price = price;
        this.validityPeriod = validityPeriod;
        this.description = description;
    }
}
