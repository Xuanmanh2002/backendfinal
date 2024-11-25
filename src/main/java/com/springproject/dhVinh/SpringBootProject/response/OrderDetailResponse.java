package com.springproject.dhVinh.SpringBootProject.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderDetailResponse {
    private Long id;
    private Long quantity;
    private Double price;
    private Double totalAmounts;
    private Long totalValidityPeriod;
    private Long serviceId;
    private Long orderId;

    public OrderDetailResponse(Long id, Long quantity, Double price, Double totalAmounts, Long totalValidityPeriod, Long serviceId, Long orderId) {
        this.id = id;
        this.quantity = quantity;
        this.price = price;
        this.totalAmounts = totalAmounts;
        this.totalValidityPeriod = totalValidityPeriod;
        this.serviceId = serviceId;
        this.orderId = orderId;
    }
}
