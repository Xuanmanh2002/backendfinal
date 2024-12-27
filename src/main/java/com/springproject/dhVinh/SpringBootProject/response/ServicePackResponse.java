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
    private Long benefit;
    private String displayPosition;
    private String description;

    public ServicePackResponse(String serviceName, double price, Long validityPeriod, Long benefit, String displayPosition, String description) {
        this.serviceName = serviceName;
        this.price = price;
        this.validityPeriod = validityPeriod;
        this.benefit = benefit;
        this.displayPosition = displayPosition;
        this.description = description;
    }

    public ServicePackResponse(Long id, String serviceName, double price, Long validityPeriod, Long benefit, String displayPosition, String description) {
        this.id = id;
        this.serviceName = serviceName;
        this.price = price;
        this.validityPeriod = validityPeriod;
        this.benefit = benefit;
        this.displayPosition = displayPosition;
        this.description = description;
    }
}
