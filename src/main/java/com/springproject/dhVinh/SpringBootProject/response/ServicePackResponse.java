package com.springproject.dhVinh.SpringBootProject.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ServicePackResponse {
    private String serviceName;
    private double price;
    private Long quantity;
    private Long validityPeriod;
    private String description;

    public ServicePackResponse(String serviceName, double price, Long quantity, Long validityPeriod, String description) {
        this.serviceName = serviceName;
        this.price = price;
        this.quantity = quantity;
        this.validityPeriod = validityPeriod;
        this.description = description;
    }
}
